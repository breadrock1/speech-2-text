package server.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.handler.HandlerException;
import server.handler.context.HandlerContext;
import server.handler.context.Response;
import server.http.provider.HttpHandlerDataProvider;
import server.http.provider.HttpHandlerDataProviderManager;
import server.response.NonLocalizedErrorResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

class HttpMethodHandler implements HttpHandler {

    private final String httpMethod;
    private final Class<?> handlerClass;
    private final Method handlerMethod;
    private final HttpHandlerDataProviderManager dataProviderManager;
    private final Gson gson = new Gson();

    HttpMethodHandler(
            String httpMethod,
            Class<?> handlerClass,
            Method handlerMethod,
            HttpHandlerDataProviderManager dataProviderManager
    ) {
        this.httpMethod = httpMethod;
        this.handlerClass = handlerClass;
        this.handlerMethod = handlerMethod;
        this.dataProviderManager = dataProviderManager;

        handlerMethod.setAccessible(true);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            handleMethod(httpExchange);
        } catch (HandlerException e) {
            new Response(httpExchange, gson).setJson(e.getHttpCode(), e.getErrorResponse());
        } catch (Exception e) {
            new Response(httpExchange, gson).setJson(500, new NonLocalizedErrorResponse(e.getMessage()));
        }
    }

    private void handleMethod(HttpExchange httpExchange) throws HandlerException, IOException {
        if (httpExchange.getRequestMethod().equalsIgnoreCase(httpMethod)) {
            handleExpectedMethod(httpExchange);
        } else {
            throw new HandlerException(
                    400,
                    new NonLocalizedErrorResponse(
                            String.format(
                                    "%s expected but was %s",
                                    httpMethod.toUpperCase(),
                                    httpExchange.getRequestMethod()
                            )
                    )
            );
        }
    }

    private void handleExpectedMethod(HttpExchange httpExchange) throws HandlerException, IOException {
        Object[] args = collectArgs(httpExchange);
        Object handler = createHandler(httpExchange);
        Object result;
        try {
            result = handlerMethod.invoke(handler, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof HandlerException) {
                throw (HandlerException) e.getCause();
            }
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw new RuntimeException(e.getCause());
        }
        Response response = new Response(httpExchange, gson);
        if (result instanceof ResponseData) {
            ResponseData data = (ResponseData) result;
            data.headers.forEach(httpExchange.getResponseHeaders()::put);
            setHttpCodeAndBody(response, data.httpCode, data.body);
        } else {
            setHttpCodeAndBody(response, 200, result);
        }
    }

    private void setHttpCodeAndBody(Response response, int httpCode, Object body) throws IOException {
        if (body instanceof String) {
            response.setString(httpCode, (String) body);
        } else if (body instanceof byte[]) {
            response.setBinary(httpCode, (byte[]) body);
        } else {
            response.setJson(httpCode, body);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object createHandler(HttpExchange httpExchange) throws IOException, HandlerException {
        try {
            Object handler = handlerClass.newInstance();
            for (Field field : handlerClass.getDeclaredFields()) {
                HttpHandlerDataProvider rawProvider = null;
                Annotation mainAnnotation = null;
                for (Annotation annotation : field.getAnnotations()) {
                    rawProvider = dataProviderManager.getFor(annotation);
                    if (rawProvider != null) {
                        mainAnnotation = annotation;
                        break;
                    }
                }
                if (rawProvider == null) {
                    continue;
                }
                Object value = rawProvider.provide(HandlerContext.fromHttpExchange(httpExchange), handlerClass, mainAnnotation, field.getType());
                field.setAccessible(true);
                field.set(handler, value);
            }
            return handler;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object[] collectArgs(HttpExchange httpExchange) throws IOException, HandlerException {
        Object[] args = new Object[handlerMethod.getParameterCount()];
        for (int i = 0; i < args.length; i++) {
            final Annotation[] annotations = handlerMethod.getParameterAnnotations()[i];
            HttpHandlerDataProvider rawProvider = null;
            Annotation mainAnnotation = null;
            for (Annotation annotation : handlerMethod.getParameterAnnotations()[i]) {
                rawProvider = dataProviderManager.getFor(annotation);
                if (rawProvider != null) {
                    mainAnnotation = annotation;
                    break;
                }
            }
            if (rawProvider == null) {
                throw new IllegalStateException("No provider for annotations " + Arrays.toString(annotations));
            }
            Class<?> parameterType = handlerMethod.getParameterTypes()[i];
            args[i] = rawProvider.provide(HandlerContext.fromHttpExchange(httpExchange), handlerClass, mainAnnotation, parameterType);
        }
        return args;
    }

}

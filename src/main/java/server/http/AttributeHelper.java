package server.http;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.Method;
import java.util.List;

public class AttributeHelper {

    private static final String KEY_HANDLER_METHOD = "handler_method";
    private static final String KEY_HTTP_METHODS = "http_methods";

    private AttributeHelper() {
    }

    public static Method getHandlerMethod(HttpExchange httpExchange) {
        return (Method) httpExchange.getAttribute(KEY_HANDLER_METHOD);
    }

    public static void putHandlerMethod(HttpContext context, Method handlerMethod) {
        context.getAttributes().put(KEY_HANDLER_METHOD, handlerMethod);
    }

    @SuppressWarnings("unchecked")
    public static List<String> getHttpMethods(HttpExchange httpExchange) {
        return (List<String>) httpExchange.getAttribute(KEY_HTTP_METHODS);
    }

    public static void putHttpMethods(HttpContext context, List<String> httpMethods) {
        context.getAttributes().put(KEY_HTTP_METHODS, httpMethods);
    }
}

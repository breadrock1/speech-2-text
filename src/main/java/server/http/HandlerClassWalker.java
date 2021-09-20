package server.http;

import server.http.annotation.HandleGet;
import server.http.annotation.HandlePost;
import server.http.annotation.SummaryHttpHandler;

import java.lang.reflect.Method;

import static server.util.UrlUtils.concatPath;

public class HandlerClassWalker {

    private final Callback callback;

    public HandlerClassWalker(Callback callback) {
        this.callback = callback;
    }

    public void walk(Class<?> handlerClass) {
        SummaryHttpHandler handlerAnnotation = requireHttpHandler(handlerClass);
        for (Method method : handlerClass.getDeclaredMethods()) {
            HandleGet handleGet = method.getAnnotation(HandleGet.class);
            if (handleGet != null) {
                String path = concatPath(handlerAnnotation.path(), handleGet.value());
                callback.onMethodFound("GET", path, method);
                continue;
            }
            HandlePost handlePost = method.getAnnotation(HandlePost.class);
            if (handlePost != null) {
                String path = concatPath(handlerAnnotation.path(), handlePost.value());
                callback.onMethodFound("POST", path, method);
                continue;
            }
        }
    }

    private static SummaryHttpHandler requireHttpHandler(Class<?> handlerClass) {
        SummaryHttpHandler annotation = handlerClass.getAnnotation(SummaryHttpHandler.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    String.format("Class %s does not have %s annotation", handlerClass, SummaryHttpHandler.class)
            );
        }
        return annotation;
    }

    @FunctionalInterface
    public interface Callback {
        void onMethodFound(String httpMethod, String path, Method method);
    }

}

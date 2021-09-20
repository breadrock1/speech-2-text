package server.http.provider;

import server.handler.context.HandlerContext;
import server.http.annotation.Path;

import javax.annotation.Nullable;

public class PathHttpHandlerDataProvider implements HttpHandlerDataProvider<Path> {

    @Nullable
    @Override
    public Object provide(HandlerContext context, Class<?> handlerClass, Path annotation, Class<?> type) {
        if (type != String.class) {
            throw new IllegalArgumentException(
                    String.format("Path must be %s but was %s", String.class, type)
            );
        }
        return context.getRequest().getURI().getPath();
    }
}

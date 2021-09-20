package server.http.provider;

import server.handler.HandlerException;
import server.handler.context.HandlerContext;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;

public interface HttpHandlerDataProvider<T extends Annotation> {

    @Nullable
    Object provide(HandlerContext context, Class<?> handlerClass, T annotation, Class<?> type) throws HandlerException, IOException;
}

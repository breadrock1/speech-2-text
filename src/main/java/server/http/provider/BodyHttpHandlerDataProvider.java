package server.http.provider;

import server.handler.HandlerException;
import server.handler.context.HandlerContext;
import server.http.annotation.Body;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class BodyHttpHandlerDataProvider implements HttpHandlerDataProvider<Body> {

    @Nullable
    @Override
    public Object provide(
            HandlerContext context,
            Class<?> handlerClass,
            Body annotation,
            Class<?> type
    ) throws HandlerException, IOException {
        if (type == String.class) {
            return context.getRequest().readBodyAsString();
        }
        if (type == byte[].class) {
            return context.getRequest().readBody();
        }
        if (type == InputStream.class) {
            return context.getRequest().getBodyStream();
        }
        return context.getRequest().readBodyAsValidJson(type);
    }
}

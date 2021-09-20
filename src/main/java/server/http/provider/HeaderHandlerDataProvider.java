package server.http.provider;

import server.handler.HandlerException;
import server.handler.context.HandlerContext;
import server.handler.validator.InvalidRequestException;
import server.http.annotation.Header;

import javax.annotation.Nullable;

public class HeaderHandlerDataProvider implements HttpHandlerDataProvider<Header> {

    @Nullable
    @Override
    public Object provide(HandlerContext context, Class<?> handlerClass, Header annotation, Class<?> type) throws HandlerException {
        String name = annotation.value();
        String value = context.getRequest().getHeader(name);
        if (value == null) {
            return null;
        }
        return castToType(value, type);
    }

    private Object castToType(String value, Class<?> type) throws HandlerException {
        if (type == String.class) {
            return value;
        }
        if (type == int.class || type == Integer.class) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw InvalidRequestException.unexpectedQueryParameterType("integer", value);
            }
        }
        throw new IllegalArgumentException(String.format("Header value of type %s in not supported", type));
    }

}

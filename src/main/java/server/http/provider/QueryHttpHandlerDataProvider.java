package server.http.provider;

import server.handler.HandlerException;
import server.handler.context.HandlerContext;
import server.handler.validator.InvalidRequestException;
import server.http.annotation.Query;

import javax.annotation.Nullable;

import static com.google.common.base.Defaults.defaultValue;
import static server.util.UrlUtils.getOptionalParameterOrNull;

public class QueryHttpHandlerDataProvider implements HttpHandlerDataProvider<Query> {

    @Nullable
    @Override
    public Object provide(HandlerContext context, Class<?> handlerClass, Query annotation, Class<?> type) throws HandlerException {
        String query = context.getRequest().getURI().getQuery();
        String value;
        if (annotation.optional()) {
            value = getOptionalParameterOrNull(query, annotation.value());
            if (value == null) {
                return defaultValue(type);
            }
        } else {
            value = getRequiredParameter(query, annotation.value());
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
        if (type == boolean.class || type == Boolean.class) {
            if (value.isEmpty()) {
                return true; // key is passed without value
            }
            return Boolean.parseBoolean(value);
        }
        throw new IllegalArgumentException(String.format("Query parameter of type %s is not supported", type));
    }

    private static String getRequiredParameter(String query, String name) throws HandlerException {
        String value = getOptionalParameterOrNull(query, name);
        if (value == null) {
            throw InvalidRequestException.parameterIsRequired(name);
        }
        return value;
    }
}

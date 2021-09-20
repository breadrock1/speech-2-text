package server.handler.validator;

import server.handler.HandlerException;
import server.response.NonLocalizedErrorResponse;

public class InvalidRequestException extends HandlerException {

    private InvalidRequestException(String message) {
        super(400, new NonLocalizedErrorResponse(message));
    }

    public static InvalidRequestException parameterIsRequired(String parameterName) {
        String message = String.format("Parameter '%s' is required", parameterName);
        return new InvalidRequestException(message);
    }

    public static InvalidRequestException fieldIsRequired(String fieldName) {
        String message = String.format("Field '%s' is required", fieldName);
        return new InvalidRequestException(message);
    }

    public static InvalidRequestException fieldIsEmpty(String fieldName) {
        String message = String.format("Field '%s' is empty", fieldName);
        return new InvalidRequestException(message);
    }

    public static InvalidRequestException unexpectedQueryParameterType(String expectedType, String actualValue) {
        String message = String.format("'%s' is expected to be of type '%s'", actualValue, expectedType);
        return new InvalidRequestException(message);
    }
}

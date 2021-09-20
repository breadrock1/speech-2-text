package server.handler;

import server.response.GenericResponse;

public class HandlerException extends Exception {

    private final int httpCode;
    private final GenericResponse errorResponse;

    public HandlerException(final int httpCode, final GenericResponse errorResponse) {
        this.httpCode = httpCode;
        this.errorResponse = errorResponse;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public GenericResponse getErrorResponse() {
        return errorResponse;
    }

}

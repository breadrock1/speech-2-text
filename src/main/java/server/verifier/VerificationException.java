package server.verifier;

import server.response.GenericResponse;

public class VerificationException extends Exception {

    private final int httpCode;
    private final GenericResponse errorResponse;

    public VerificationException(final int httpCode, final GenericResponse errorResponse) {
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

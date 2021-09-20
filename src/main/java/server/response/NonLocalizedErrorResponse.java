package server.response;

public class NonLocalizedErrorResponse extends GenericResponse {

    private final String errorMessage;

    public NonLocalizedErrorResponse(final String errorMessage) {
        super(false);
        this.errorMessage = errorMessage;
    }
}

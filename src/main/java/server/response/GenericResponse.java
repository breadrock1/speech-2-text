package server.response;

import doc.annotation.Description;

public class GenericResponse {

    @Description("true - успех, false - произошла ошибка")
    private final boolean success;

    public GenericResponse() {
        this(true);
    }

    public GenericResponse(boolean success) {
        this.success = success;
    }

}

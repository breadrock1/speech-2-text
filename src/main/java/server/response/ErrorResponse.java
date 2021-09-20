package server.response;

import server.localization.Localization;
import server.localization.StringId;

import java.util.Locale;

public class ErrorResponse extends GenericResponse {

    private final String errorMessage;

    public ErrorResponse(final StringId errorId, final Locale locale) {
        super(false);
        this.errorMessage = Localization.forLocale(locale).get(errorId);
    }

    public static ErrorResponse errorResponse(final StringId errorId, final Locale locale) {
        return new ErrorResponse(errorId, locale);
    }

}

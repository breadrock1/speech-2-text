package server.localization;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {

    private final Locale locale;

    public Localization(Locale locale) {
        this.locale = locale;
    }

    public String get(final StringId id) {
        ResourceBundle bundle = ResourceBundle.getBundle("strings", locale);
        return bundle.getString(id.name());
    }

    public static Localization forLocale(Locale locale) {
        return new Localization(locale);
    }
}

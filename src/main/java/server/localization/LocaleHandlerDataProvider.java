package server.localization;

import server.handler.context.HandlerContext;
import server.http.provider.HttpHandlerDataProvider;

import javax.annotation.Nullable;
import java.util.Locale;

import static server.util.UrlUtils.getOptionalParameterOrNull;

public class LocaleHandlerDataProvider implements HttpHandlerDataProvider<CurrentLocale> {

    private static final Locale DEFAULT_LOCALE = new Locale.Builder()
            .setLanguage("ru")
            .setRegion("RU")
            .build();

    @Nullable
    @Override
    public Object provide(HandlerContext context, Class<?> handlerClass, CurrentLocale annotation, Class<?> type) {
        if (type != Locale.class) {
            throw new IllegalArgumentException("Only " + Locale.class + " is supported");
        }
        String localeParam = getOptionalParameterOrNull(context.getRequest().getURI().getQuery(), "locale");
        return (localeParam == null) ? DEFAULT_LOCALE : new Locale(localeParam);
    }
}

package server.http.provider;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class HttpHandlerDataProviderManager {

    private final Map<Class<?>, HttpHandlerDataProvider<?>> annotationProviders = new HashMap<>();

    public <T extends Annotation> void register(Class<T> annotationType, HttpHandlerDataProvider<T> provider) {
        annotationProviders.put(annotationType, provider);
    }

    @Nullable
    public HttpHandlerDataProvider<?> getFor(Annotation annotation) {
        return annotationProviders.get(annotation.annotationType());
    }

}

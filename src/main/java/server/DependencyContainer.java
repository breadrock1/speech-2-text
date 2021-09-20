package server;

import server.handler.context.HandlerContext;
import server.http.provider.HttpHandlerDataProvider;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

class DependencyContainer implements HttpHandlerDataProvider<Inject> {

    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Factory<?>> factories = new HashMap<>();

    void put(Object instance) {
        instances.put(instance.getClass(), instance);
    }

    <T> void put(Class<T> clazz, T instance) {
        instances.put(clazz, instance);
    }

    <T> void addFactory(Class<T> type, Factory<T> factory) {
        factories.put(type, factory);
    }

    @Nullable
    @Override
    public Object provide(HandlerContext context, Class<?> handlerClass, Inject annotation, Class<?> type) {
        Object instance = instances.get(type);
        if (instance != null) {
            return instance;
        }
        return factories.get(type).createInstance(handlerClass);
    }

    @FunctionalInterface
    interface Factory<T> {
        T createInstance(Class<?> handlerClass);
    }

}

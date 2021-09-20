package server.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoggerFactory {

    private static final Map<Class<?>, Logger> cache = new HashMap<>();

    private static InstanceCreator instanceCreator;
    private static Set<Class<?>> disabledClasses;

    public static void init(String loggerName, Set<Class<?>> disabledClasses) {
        LoggerFactory.disabledClasses = disabledClasses;
        switch (loggerName) {
            case "system-out":
                instanceCreator = SystemOutLogger::new;
                break;
            case "google-cloud":
                instanceCreator = GoogleCloudLogger::new;
                break;
            default:
                throw new IllegalArgumentException("Unsupported logger " + loggerName);
        }
    }

    public static Logger createFor(Class<?> clazz) {
        if (disabledClasses.contains(clazz)) {
            return new FakeLogger();
        }
        synchronized (cache) {
            return cache.computeIfAbsent(clazz, key -> instanceCreator.create(key.getSimpleName()));
        }
    }

    private interface InstanceCreator {
        Logger create(String name);
    }

}

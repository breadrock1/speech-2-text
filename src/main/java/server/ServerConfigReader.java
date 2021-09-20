package server;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;

public class ServerConfigReader {

    private final File file;

    public ServerConfigReader(File file) {
        this.file = file;
    }

    public ServerConfig read() throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            return validate(gson.fromJson(reader, ServerConfig.class));
        }
    }

    private ServerConfig validate(ServerConfig config) {
        Field[] fields = config.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (getField(config, f) == null) {
                throw new IllegalArgumentException("Field '" + f.getName() + "' is not set");
            }
        }
        return config;
    }

    private static Object getField(Object obj, Field f) {
        try {
            return f.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

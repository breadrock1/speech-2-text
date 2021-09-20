package server.util;

import javax.annotation.Nullable;

public class StringUtils {
    private StringUtils() {
    }

    public static String orEmpty(@Nullable String value) {
        return (value == null) ? "" : value;
    }
}

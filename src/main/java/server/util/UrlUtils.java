package server.util;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class UrlUtils {
    private UrlUtils() {
    }

    public static String concatPath(String path1, String path2) {
        if (path2.isEmpty()) {
            return path1;
        }
        if (path2.startsWith("/")) {
            path2 = path2.substring(1);
        }
        if (path1.endsWith("/")) {
            return path1 + path2;
        }
        return path1 + "/" + path2;
    }

    @Nullable
    public static String getOptionalParameterOrNull(String query, String name) {
        if (StringUtils.isEmpty(query)) {
            return null;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue[0].equalsIgnoreCase(name)) {
                return keyValue.length == 2 ? keyValue[1] : "";
            }
        }
        return null;
    }
}

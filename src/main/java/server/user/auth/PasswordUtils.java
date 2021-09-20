package server.user.auth;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordUtils {
    private PasswordUtils() {
    }

    public static String hash(String password) {
        return DigestUtils.md5Hex(password);
    }
}

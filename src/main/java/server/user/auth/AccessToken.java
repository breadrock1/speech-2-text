package server.user.auth;

import server.user.User;

import javax.annotation.Nullable;

public class AccessToken {

    @Nullable
    private final String login;

    public AccessToken(@Nullable String login) {
        this.login = login;
    }

    public boolean isValid() {
        return login != null;
    }

    public String getLogin() {
        if (!isValid()) {
            throw new IllegalStateException("Trying to get login for invalid token");
        }
        return login;
    }

    public static AccessToken forUser(User user) {
        return new AccessToken(user.getLogin());
    }
}

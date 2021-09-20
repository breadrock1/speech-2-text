package server.user;

public class UserCreationException extends Exception {

    public final Reason reason;

    public UserCreationException(Reason reason) {
        this.reason = reason;
    }

    public enum Reason {
        ALREADY_EXIST,
        BAD_LOGIN,
        PASSWORD_TOO_SHORT,
    }
}

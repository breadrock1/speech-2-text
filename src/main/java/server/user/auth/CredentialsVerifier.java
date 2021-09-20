package server.user.auth;

import server.user.UserCreationException;

import java.util.function.IntPredicate;

public class CredentialsVerifier {

    private final IntPredicate codePointPredicate =
            codePoint -> Character.isLetterOrDigit(codePoint) || codePoint == '_';

    public void verifyLogin(String login) throws UserCreationException {
        if (!login.codePoints().allMatch(codePointPredicate)) {
            throw new UserCreationException(UserCreationException.Reason.BAD_LOGIN);
        }
    }

    public void verifyPassword(String password) throws UserCreationException {
        if (password.length() < 6) {
            throw new UserCreationException(UserCreationException.Reason.PASSWORD_TOO_SHORT);
        }
    }
}

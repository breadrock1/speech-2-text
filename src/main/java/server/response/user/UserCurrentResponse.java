package server.response.user;

import server.user.User;
import server.response.GenericResponse;

public class UserCurrentResponse extends GenericResponse {

    private final String login;

    public UserCurrentResponse(User user) {
        this.login = user.getLogin();
    }
}

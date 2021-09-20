package server.request.user;

import server.handler.validator.Required;

public class UserLoginRequest {

    @Required
    public String login;

    @Required
    public String password;
}

package server.request.user;

import server.handler.validator.Required;

public class UserCreateRequest {

    @Required
    public String login;

    @Required
    public String password;
}

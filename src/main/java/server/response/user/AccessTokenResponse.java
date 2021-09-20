package server.response.user;

import server.response.GenericResponse;

public class AccessTokenResponse extends GenericResponse {

    private final String accessToken;

    private final String tokenType = "Bearer";

    public AccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}


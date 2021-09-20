package server.user.auth;

import server.handler.context.Request;
import server.http.verifier.RequestVerifier;
import server.response.NonLocalizedErrorResponse;
import server.verifier.VerificationException;

import java.lang.reflect.Method;

public class AuthHttpRequestVerifier implements RequestVerifier {

    private static final String TOKEN_TYPE = "Bearer";

    private final AccessTokenManager accessTokenManager;

    public AuthHttpRequestVerifier(AccessTokenManager accessTokenManager) {
        this.accessTokenManager = accessTokenManager;
    }

    @Override
    public void verify(Request request, Method handlerMethod) throws VerificationException {
        boolean isUnauthorized = handlerMethod.getAnnotation(Unauthorized.class) != null;
        if (!isUnauthorized) {
            checkAuthorization(accessTokenManager, request);
        }
    }

    static AccessToken checkAuthorization(AccessTokenManager accessTokenManager, Request request) throws VerificationException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            throw new VerificationException(401, new NonLocalizedErrorResponse("No Authorization header provided"));
        }
        if (!authHeader.startsWith(TOKEN_TYPE)) {
            throw new VerificationException(401, new NonLocalizedErrorResponse("Not a " + TOKEN_TYPE + " token"));
        }
        String tokenValue = authHeader.substring(TOKEN_TYPE.length() + 1).trim();
        AccessToken token = accessTokenManager.decodeFromJws(tokenValue);
        if (!token.isValid()) {
            throw new VerificationException(401, new NonLocalizedErrorResponse("Auth token is invalid"));
        }
        return token;
    }
}

package server.user.auth;

import server.handler.HandlerException;
import server.handler.context.HandlerContext;
import server.http.provider.HttpHandlerDataProvider;
import server.user.UserManager;
import server.verifier.VerificationException;

import javax.annotation.Nullable;

public class AuthorizedUserProvider implements HttpHandlerDataProvider<AuthorizedUser> {

    private final UserManager userManager;
    private final AccessTokenManager accessTokenManager;

    public AuthorizedUserProvider(UserManager userManager, AccessTokenManager accessTokenManager) {
        this.userManager = userManager;
        this.accessTokenManager = accessTokenManager;
    }

    @Nullable
    @Override
    public Object provide(HandlerContext context, Class<?> handlerClass, AuthorizedUser annotation, Class<?> type) throws HandlerException {
        AccessToken accessToken;
        try {
            accessToken = AuthHttpRequestVerifier.checkAuthorization(accessTokenManager, context.getRequest());
        } catch (VerificationException e) {
            throw new HandlerException(e.getHttpCode(), e.getErrorResponse());
        }
        return userManager.getUser(accessToken.getLogin());
    }
}

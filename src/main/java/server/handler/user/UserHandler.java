package server.handler.user;

import doc.annotation.Description;
import server.handler.HandlerException;
import server.http.annotation.Body;
import server.http.annotation.HandleGet;
import server.http.annotation.HandlePost;
import server.http.annotation.SummaryHttpHandler;
import server.localization.CurrentLocale;
import server.localization.StringId;
import server.logging.Logger;
import server.request.user.UserCreateRequest;
import server.request.user.UserLoginRequest;
import server.response.user.AccessTokenResponse;
import server.response.user.UserCurrentResponse;
import server.user.User;
import server.user.UserCreationException;
import server.user.UserManager;
import server.user.auth.AccessToken;
import server.user.auth.AccessTokenManager;
import server.user.auth.AuthorizedUser;
import server.user.auth.Unauthorized;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Locale;

import static server.response.ErrorResponse.errorResponse;

@SummaryHttpHandler(path = "/1/user")
public class UserHandler {

    @Inject
    UserManager userManager;

    @Inject
    AccessTokenManager accessTokenManager;

    @Inject
    Logger logger;

    @CurrentLocale
    Locale locale;

    @Description("Создание пользователя")
    @HandlePost("/create")
    @Unauthorized
    AccessTokenResponse create(@Body UserCreateRequest request) throws HandlerException {
        logger.info("Handle user create; login=%s", request.login);
        try {
            User user = userManager.createUser(request.login, request.password);
            AccessToken token = AccessToken.forUser(user);
            return new AccessTokenResponse(accessTokenManager.encodeToJws(token));
        } catch (UserCreationException e) {
            switch (e.reason) {
                case ALREADY_EXIST:
                    throw new HandlerException(200, errorResponse(StringId.USER_ALREADY_EXIST, locale));
                case BAD_LOGIN:
                    throw new HandlerException(200, errorResponse(StringId.USER_BAD_LOGIN, locale));
                case PASSWORD_TOO_SHORT:
                    throw new HandlerException(200, errorResponse(StringId.USER_PASSWORD_TOO_SHORT, locale));
                default:
                    throw new HandlerException(200, errorResponse(StringId.GENERIC_ERROR, locale));
            }
        }
    }

    @Description("Логин")
    @HandlePost("/login")
    @Unauthorized
    AccessTokenResponse login(@Body UserLoginRequest request) throws HandlerException {
        logger.info("Handle user login; login=%s", request.login);
        User user = userManager.getUser(request.login);
        if (user == null) {
            throw new HandlerException(200, errorResponse(StringId.USER_NOT_EXIST, locale));
        } else {
            if (userManager.isPasswordCorrect(user, request.password)) {
                AccessToken token = AccessToken.forUser(user);
                return new AccessTokenResponse(accessTokenManager.encodeToJws(token));
            } else {
                throw new HandlerException(200, errorResponse(StringId.USER_WRONG_CREDENTIALS, locale));
            }
        }
    }

    @Description("Получение текущего авторизованного пользователя")
    @HandleGet("/current")
    UserCurrentResponse current(@AuthorizedUser @Nullable User user) throws HandlerException {
        logger.info("Handle current user; login=%s", user != null ? user.getLogin() : null);
        if (user == null) {
            throw new HandlerException(200, errorResponse(StringId.USER_NOT_EXIST, locale));
        } else {
            return new UserCurrentResponse(user);
        }
    }
}

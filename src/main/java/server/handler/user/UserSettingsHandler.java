package server.handler.user;

import doc.annotation.Description;
import doc.annotation.Responses;
import server.http.annotation.Body;
import server.http.annotation.HandleGet;
import server.http.annotation.HandlePost;
import server.http.annotation.Query;
import server.http.annotation.SummaryHttpHandler;
import server.response.GenericResponse;
import server.user.User;
import server.user.UserSettingsManager;
import server.user.auth.AuthorizedUser;

import javax.inject.Inject;

@SummaryHttpHandler(path = "/1/user/settings")
public class UserSettingsHandler {

    private static final String SET_BODY_DOCUMENTATION = "произвольное содержимое, в том числе произвольный JSON";

    @Inject
    UserSettingsManager userSettingsManager;

    @AuthorizedUser
    User user;

    @Description("Запись настройки по ключу")
    @HandlePost("/set")
    GenericResponse set(
            @Query("key") String key,
            @Body @Description(SET_BODY_DOCUMENTATION) String value) {
        userSettingsManager.set(user, key, value);
        return new GenericResponse();
    }

    @Description("Удаление настройки по ключу")
    @HandlePost("/remove")
    GenericResponse remove(@Query("key") String key) {
        userSettingsManager.remove(user, key);
        return new GenericResponse();
    }

    @Description("Получение настройки по ключу")
    @HandleGet("/get")
    @Responses(
            explanations = {"произвольное содержимое, переданное ранее в set по тому же ключу. Пустая строка означает отсутствие значения."},
            responses = {String.class}
    )
    String get(@Query("key") String key) {
        return userSettingsManager.get(user, key);
    }

}

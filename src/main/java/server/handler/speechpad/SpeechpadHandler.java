package server.handler.speechpad;

import doc.annotation.Description;
import server.handler.HandlerException;
import server.http.annotation.Body;
import server.http.annotation.HandleGet;
import server.http.annotation.HandlePost;
import server.http.annotation.Query;
import server.http.annotation.SummaryHttpHandler;
import server.localization.CurrentLocale;
import server.localization.StringId;
import server.logging.Logger;
import server.response.GenericResponse;
import server.response.speechpad.SpeechpadChunkResponse;
import server.response.speechpad.SpeechpadCreateResponse;
import server.response.speechpad.SpeechpadGetAllResponse;
import server.response.speechpad.SpeechpadGetResponse;
import server.response.speechpad.SpeechpadRenameResponse;
import server.response.transcribe.TranscribeResult;
import server.speechpad.NoSuchSpeechpadException;
import server.speechpad.Speechpad;
import server.speechpad.SpeechpadManager;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static server.response.ErrorResponse.errorResponse;

@SummaryHttpHandler(path = "/1/speechpad")
public class SpeechpadHandler {

    @Inject
    SpeechpadManager speechpadManager;

    @Inject
    Logger logger;

    @CurrentLocale
    Locale locale;

    @Description("Создание голосового блокнота")
    @HandlePost("/create")
    SpeechpadCreateResponse create(
            @Query("model") String model,
            @Query("name") String name
    ) {
        logger.info("Handle speechpad create");
        Speechpad speechpad = speechpadManager.create(model, name);
        return new SpeechpadCreateResponse(speechpad.getId(), name);
    }

    @Description("Отправка порции аудио")
    @HandlePost("/chunk")
    SpeechpadChunkResponse chunk(
            @Query("speechpad_id") String speechpadId,
            @Body byte[] body
    ) throws IOException, HandlerException {
        logger.info("Handle speechpad chunk");
        try {
            Speechpad speechpad = speechpadManager.getSpeechpad(speechpadId);
            List<TranscribeResult> result = speechpad.append(body);
            return new SpeechpadChunkResponse(result);
        } catch (NoSuchSpeechpadException e) {
            throw new HandlerException(200, errorResponse(StringId.SPEECHPAD_NOT_EXIST, locale));
        }
    }


    //TODO: need realized methods to edit, delete and create new archive speech
    @Description("Изменить название архивной записи")
    @HandlePost("/remove")
    GenericResponse remove(
            @Query("speechpad_id") String speechpadId
    ) {
        logger.info("Handle speechpad remove");
        boolean status = speechpadManager.delete(speechpadId);
        return new GenericResponse(status);
    }

    @Description("Изменить название архивной записи")
    @HandlePost("/rename")
    SpeechpadRenameResponse rename(
            @Query("speechpad_id") String speechpadId,
            @Query("new_name") String newName
    ) throws NoSuchSpeechpadException {
        logger.info("Handle speechpad rename");
        speechpadManager.rename(speechpadId, newName);
        return new SpeechpadRenameResponse(speechpadId, newName);
    }

    @Description("Получить все архивные записи")
    @HandleGet("/getAll")
    SpeechpadGetAllResponse getAll() {
        logger.info("Handle get all speechpad archives");
        List<Speechpad> allSpeechpad = speechpadManager.getAllSpeechpad();
        return new SpeechpadGetAllResponse(allSpeechpad);
    }

    @Description("Получить архивную запись по id")
    @HandleGet("/get")
    SpeechpadGetResponse get(
            @Query("speechpad_id") String speechpadId
    ) throws NoSuchSpeechpadException {
        logger.info("Handle get speechpad archive by id");
        Speechpad speechpad = speechpadManager.getSpeechpad(speechpadId);
        return new SpeechpadGetResponse(speechpad);
    }

}

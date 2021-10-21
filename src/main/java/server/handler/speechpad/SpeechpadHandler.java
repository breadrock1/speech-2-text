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
import server.response.speechpad.SpeechpadFullObjectResponse;
import server.response.speechpad.SpeechpadListResponse;
import server.speechpad.NoSuchSpeechpadException;
import server.speechpad.Speechpad;
import server.speechpad.SpeechpadManager;

import javax.inject.Inject;
import java.io.IOException;
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
        logger.info("Handle create speechpad");
        Speechpad speechpad = speechpadManager.createSpeechpad(model, name);
        return new SpeechpadCreateResponse(speechpad.getId(), speechpad.getName());
    }

    @Description("Удаление голосового блокнота")
    @HandlePost("/remove")
    GenericResponse remove(
        @Query("speechpad_id") String speechpadId
    ) throws NoSuchSpeechpadException {
        logger.info("Handle remove speechpad");
        try {
            speechpadManager.deleteSpeechpad(speechpadId);
            return new GenericResponse(true);
        } catch (NoSuchSpeechpadException e) {
            throw new NoSuchSpeechpadException(speechpadId);
        }
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
            return new SpeechpadChunkResponse(speechpad.append(body));
        } catch (NoSuchSpeechpadException e) {
            throw new HandlerException(200, errorResponse(StringId.SPEECHPAD_NOT_EXIST, locale));
        }
    }

    @Description("Получение голосового блокнота по идентификатору")
    @HandleGet("/get")
    SpeechpadFullObjectResponse get(
        @Query("speechpad_id") String speechpadId
    ) throws NoSuchSpeechpadException {
        logger.info("Handle get speechpad by id");
        try {
            Speechpad speechpad = speechpadManager.getSpeechpad(speechpadId);
            return new SpeechpadFullObjectResponse(speechpad.getId(), speechpad.getName(), speechpad.getTranscribe());
        } catch (NoSuchSpeechpadException e) {
            throw new NoSuchSpeechpadException(speechpadId);
        }
    }

    @Description("Получение списка идентификаторов всех голосовых блокнотов")
    @HandleGet("/getAll")
    SpeechpadListResponse getAll() {
        logger.info("Handle get all speechpad ids");
        return new SpeechpadListResponse(speechpadManager.getAllSpeechpads());
    }

    @Description("Переименование голосового блокнота")
    @HandlePost("/rename")
    SpeechpadCreateResponse rename(
        @Query("speechpad_id") String speechpadId,
        @Query("new_name") String newName
    ) throws NoSuchSpeechpadException {
        logger.info("Handle rename speechpad");
        try {
            Speechpad speechpad = speechpadManager.renameSpeechpad(speechpadId, newName);
            return new SpeechpadCreateResponse(speechpad.getId(), speechpad.getName());
        } catch (NoSuchSpeechpadException e) {
            throw new NoSuchSpeechpadException(speechpadId);
        }
    }

    @Description("Изменение транскрипции голосового блокнота")
    @HandlePost("/update")
    SpeechpadFullObjectResponse update(
        @Query("speechpad_id") String speechpadId,
        @Query("transcribe") String data
    ) throws NoSuchSpeechpadException {
        logger.info("Handle update speechpad transcribe");
        try {
            Speechpad speechpad = speechpadManager.editSpeechpadTranscribe(speechpadId, data);
            return new SpeechpadFullObjectResponse(speechpad.getId(), speechpad.getName(), speechpad.getTranscribe());
        } catch (NoSuchSpeechpadException e) {
            throw new NoSuchSpeechpadException(speechpadId);
        }
    }

}

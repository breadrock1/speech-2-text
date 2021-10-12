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
        @Query("model") String model
    ) {
        logger.info("Handle create speechpad");
        Speechpad speechpad = speechpadManager.create(model);
        return new SpeechpadCreateResponse(speechpad.getId());
    }

    @Description("Удаление голосового блокнота")
    @HandlePost("/remove")
    GenericResponse remove(
        @Query("speechpad_id") String speechpadId
    ) throws NoSuchSpeechpadException {
        logger.info("Handle remove speechpad");
        boolean status = speechpadManager.delete(speechpadId);
        return new GenericResponse(status);
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

    @Description("Получение голосового блокнота по идентификатору")
    @HandleGet("/get")
    SpeechpadCreateResponse get(
        @Query("speechpad_id") String speechpadId
    ) throws NoSuchSpeechpadException {
        logger.info("Handle get speechpad by id");
        try {
            Speechpad speechpad = speechpadManager.getSpeechpad(speechpadId);
            return new SpeechpadCreateResponse(speechpad.getId());
        } catch (NoSuchSpeechpadException e) {
            throw new NoSuchSpeechpadException(speechpadId);
        }
    }

}

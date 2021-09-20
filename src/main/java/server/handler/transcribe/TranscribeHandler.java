package server.handler.transcribe;

import doc.annotation.Description;
import server.http.annotation.Body;
import server.http.annotation.HandleGet;
import server.http.annotation.HandlePost;
import server.http.annotation.Query;
import server.http.annotation.SummaryHttpHandler;
import server.logging.Logger;
import server.response.GenericResponse;
import server.response.transcribe.TranscribeGenerateUploadUrlResponse;
import server.response.transcribe.TranscribeGetResponse;
import server.response.transcribe.TranscribeStartResponse;
import server.response.transcribe.TranscribeStateResponse;
import server.single_transcription.SingleTranscriptionState;
import server.transcribe.TranscribeContent;
import server.transcribe.TranscribeManager;
import server.transcribe.TranscribeResult;
import server.transcribe.TranscriptionConfig;
import server.transcribe.UploadUrl;

import javax.inject.Inject;

@SummaryHttpHandler(path = "/2/transcribe")
public class TranscribeHandler {

    private static final String MODEL_DOCUMENTATION = "command_and_search, phone_call или default";

    @Inject
    TranscribeManager transcribeManager;

    @Inject
    Logger logger;

    @Description("Проверка статуса расшифровки")
    @HandleGet("/check_state")
    TranscribeStateResponse checkState(@Query("transcribe_id") String transcribeId) {
        logger.info("Handle transcribe get status; transcribe_id=%s", transcribeId);
        SingleTranscriptionState state = transcribeManager.getState(transcribeId);
        return new TranscribeStateResponse(state);
    }

    @Description("Редактирование расшифровки")
    @HandlePost("/edit")
    GenericResponse edit(@Query("transcribe_id") String transcribeId, @Body TranscribeContent content) {
        logger.info("Handle transcribe edit; transcribe_id=%s", transcribeId);
        boolean success = transcribeManager.editResult(transcribeId, content);
        return new GenericResponse(success);
    }

    @Description("Создание ссылки для загрузки аудио больше 32 мегабайт")
    @HandlePost("/generate_upload_url")
    TranscribeGenerateUploadUrlResponse generateUploadUrlHandler(@Query("service") String service) {
        logger.info("Handle generating upload url for transcribe upload; service=%s", service);
        UploadUrl url = transcribeManager.generateUploadUrl(service);
        return new TranscribeGenerateUploadUrlResponse(url);
    }

    @Description("Получение расшифровки")
    @HandleGet("/get")
    TranscribeGetResponse get(@Query("transcribe_id") String transcribeId) {
        logger.info("Handle transcribe get; transcribe_id=%s", transcribeId);
        TranscribeResult result = transcribeManager.getResult(transcribeId);
        return TranscribeGetResponse.from(result);
    }

    @Description("Запуск процесса расшифровки")
    @HandlePost("/start")
    TranscribeStartResponse start(
            @Query("transcribe_id") String transcribeId,
            @Query(value = "diarization_enabled", optional = true) boolean diarizationEnabled,
            @Query(value = "min_speaker_count", optional = true) int minSpeakerCount,
            @Query(value = "max_speaker_count", optional = true) int maxSpeakerCount,
            @Query(value = "model", optional = true) @Description(MODEL_DOCUMENTATION) String model
    ) {
        logger.info(
                "Handle transcribe start; transcribe_id=%s; diarization_enabled=%s; min_speaker_count=%d; max_speaker_count=%d; model=%s",
                transcribeId, diarizationEnabled, minSpeakerCount, maxSpeakerCount, model
        );

        if (transcribeManager.getState(transcribeId) == SingleTranscriptionState.NOT_STARTED) {
            TranscriptionConfig config = TranscriptionConfig.newBuilder()
                    .service(transcribeManager.getServiceByTranscribeId(transcribeId))
                    .diarizationEnabled(diarizationEnabled)
                    .minSpeakerCount(minSpeakerCount)
                    .maxSpeakerCount(maxSpeakerCount)
                    .model(model)
                    .build();
            transcribeManager.start(transcribeId, config);
        }
        return new TranscribeStartResponse(transcribeId);
    }

}

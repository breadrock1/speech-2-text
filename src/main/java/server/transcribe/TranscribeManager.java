package server.transcribe;

import server.single_transcription.SingleTranscriptionState;
import server.transcribe.google.GoogleTranscribeService;
import server.transcribe.yandex.YandexTranscribeService;

import java.io.IOException;
import java.util.UUID;

public class TranscribeManager {

    public static final String SERVICE_GOOGLE = "google";
    public static final String SERVICE_YANDEX = "yandex";

    private final TranscribeIdProcessor idProcessor = new TranscribeIdProcessor();
    private final TranscribeService googleService = new GoogleTranscribeService();
    private final TranscribeService yandexService = new YandexTranscribeService();

    public TranscribeManager init() throws IOException {
        googleService.init();
        return this;
    }

    public String getServiceByTranscribeId(String id) {
        TranscribeId transcribeId = idProcessor.decode(id);
        if (transcribeId == null) {
            throw new IllegalArgumentException("Cannot get service by transcribe id: " + id);
        }
        switch (transcribeId.serviceName) {
            case GOOGLE:
                return SERVICE_GOOGLE;
            case YANDEX:
                return SERVICE_YANDEX;
            default:
                throw new IllegalArgumentException("Cannot get service by transcribe id: " + id);
        }
    }

    public UploadUrl generateUploadUrl(String service) {
        final String internalId = UUID.randomUUID().toString();
        switch (service) {
            case "google":
                return new UploadUrl(
                        idProcessor.encode(new TranscribeId(TranscribeServiceName.GOOGLE, internalId)),
                        googleService.generateUploadUrl(internalId)
                );
            case "yandex":
                return new UploadUrl(
                        idProcessor.encode(new TranscribeId(TranscribeServiceName.YANDEX, internalId)),
                        yandexService.generateUploadUrl(internalId)
                );
            default:
                throw new IllegalArgumentException("Unsupported service: " + service);
        }
    }

    public void start(final String id, final TranscriptionConfig config) {
        TranscribeId transcribeId = idProcessor.decode(id);
        if (transcribeId == null) {
            return;
        }
        switch (transcribeId.serviceName) {
            case GOOGLE:
                googleService.startTranscription(transcribeId.internalId, config);
                break;
            case YANDEX:
                yandexService.startTranscription(transcribeId.internalId, config);
                break;
        }
    }


    public String start(final byte[] data, final TranscriptionConfig config) {
        final String internalId = UUID.randomUUID().toString();
        switch (config.getService()) {
            case SERVICE_GOOGLE:
                googleService.startTranscription(internalId, data, config);
                return idProcessor.encode(new TranscribeId(TranscribeServiceName.GOOGLE, internalId));
            case SERVICE_YANDEX:
                yandexService.startTranscription(internalId, data, config);
                return idProcessor.encode(new TranscribeId(TranscribeServiceName.YANDEX, internalId));
            default:
                throw new IllegalArgumentException("Unsupported async transcriber: " + config.getService());
        }
    }

    public SingleTranscriptionState getState(final String id) {
        TranscribeId transcribeId = idProcessor.decode(id);
        if (transcribeId == null) {
            return SingleTranscriptionState.NOT_STARTED;
        }
        switch (transcribeId.serviceName) {
            case GOOGLE:
                return googleService.getState(transcribeId.internalId);
            case YANDEX:
                return yandexService.getState(transcribeId.internalId);
            default:
                return SingleTranscriptionState.NOT_STARTED;
        }
    }

    public TranscribeResult getResult(final String id) {
        TranscribeId transcribeId = idProcessor.decode(id);
        if (transcribeId == null) {
            return TranscribeResult.EMPTY;
        }
        switch (transcribeId.serviceName) {
            case GOOGLE:
                return googleService.getResult(transcribeId.internalId);
            case YANDEX:
                return yandexService.getResult(transcribeId.internalId);
            default:
                return TranscribeResult.EMPTY;
        }
    }

    public boolean editResult(final String id, final TranscribeContent content) {
        TranscribeId transcribeId = idProcessor.decode(id);
        if (transcribeId == null) {
            return false;
        }
        switch (transcribeId.serviceName) {
            case GOOGLE:
                return googleService.editResult(transcribeId.internalId, content);
            case YANDEX:
                return yandexService.editResult(transcribeId.internalId, content);
            default:
                return false;
        }
    }
}

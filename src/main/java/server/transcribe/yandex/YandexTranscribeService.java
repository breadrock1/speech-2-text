package server.transcribe.yandex;

import okhttp3.ResponseBody;
import retrofit2.Response;
import server.single_transcription.SingleTranscriptionState;
import server.transcribe.TranscribeContent;
import server.transcribe.TranscribeResult;
import server.transcribe.TranscribeService;
import server.transcribe.TranscriptionConfig;
import server.transcribe.TranscriptionInput;
import server.transcribe.common.TranscribeMeta;
import server.transcribe.yandex.api.GetResultResponse;
import server.transcribe.yandex.api.LongRunningRecognizeResponse;
import server.transcribe.yandex.api.RecognitionParams;
import server.transcribe.yandex.api.YandexApi;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YandexTranscribeService implements TranscribeService {

    private final YandexObjectStorage storage = new YandexObjectStorage();

    private final YandexApi yandexApi = YandexApi.create();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public String generateUploadUrl(String id) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void startTranscription(String id, TranscriptionConfig config) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void startTranscription(String id, byte[] data, TranscriptionConfig config) {
        TranscribeContext context = new TranscribeContext(id, data, config);
        storage.saveMeta(id, TranscribeMeta.inProgress(config));
        executor.submit(new UploadTask(context));
    }

    @Override
    public SingleTranscriptionState getState(String id) {
        return storage.readMeta(id).state;
    }

    @Override
    public TranscribeResult getResult(String id) {
        TranscribeMeta meta = storage.readMeta(id);
        if (meta.state != SingleTranscriptionState.READY) {
            return TranscribeResult.EMPTY;
        }
        TranscribeContent content = storage.readResult(id);
        return new TranscribeResult(new TranscriptionInput(meta.config, meta.audioUrl), content);
    }

    @Override
    public boolean editResult(String id, TranscribeContent content) {
        TranscribeMeta meta = storage.readMeta(id);
        if (meta.state != SingleTranscriptionState.READY) {
            return false;
        }
        storage.saveResult(id, content);
        return true;
    }

    private void scheduleNextPollingAttempt(TranscribeContext context) {
        executor.schedule(new PollingTask(context), 10, TimeUnit.SECONDS);
    }

    private void finishWithError(TranscribeContext context, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        storage.saveResult(context.id, TranscribeContent.error(sw.toString()));
        storage.saveMeta(context.id, TranscribeMeta.error(context.config));
    }

    private static class TranscribeContext {
        final String id;
        final byte[] data;
        final TranscriptionConfig config;
        final String filename;

        StorageObjectInfo audio;

        String operationId;

        TranscribeContext(String id, byte[] data, TranscriptionConfig config) {
            this.id = id;
            this.data = data;
            this.config = config;

            filename = id;
        }

    }

    private abstract class Task implements Runnable {

        final TranscribeContext context;

        public abstract void execute() throws IOException;

        Task(TranscribeContext context) {
            this.context = context;
        }

        @Override
        public final void run() {
            try {
                execute();
            } catch (IOException e) {
                finishWithError(context, e);
            }
        }
    }

    private class UploadTask extends Task {

        public UploadTask(TranscribeContext context) {
            super(context);
        }

        @Override
        public void execute() {
            context.audio = storage.saveAudio(context.filename, context.data);
            executor.execute(new StartRecognitionTask(context));
        }
    }

    private class StartRecognitionTask extends Task {

        StartRecognitionTask(TranscribeContext context) {
            super(context);
        }

        @Override
        public void execute() throws IOException {
            context.operationId = sendFileForRecognition(context.audio.url);
            scheduleNextPollingAttempt(context);
        }

        private String sendFileForRecognition(String uri) throws IOException {
            Response<LongRunningRecognizeResponse> response = yandexApi.speechKitApi
                    .longRunningRecognize(RecognitionParams.withUri(uri)).execute();
            if (!response.isSuccessful()) {
                ResponseBody errorBody = response.errorBody();
                throw new IOException(
                        String.format(
                                "%s%n%s%n%s",
                                "longRunningRecognize was not successful",
                                response.message(),
                                errorBody != null ? errorBody.string() : ""
                        )
                );
            }
            LongRunningRecognizeResponse body = response.body();
            if (body == null) {
                throw new IOException(
                        String.format("%s%n%s", response.message(), "longRunningRecognize body is null")
                );
            }
            return body.id;
        }
    }

    private class PollingTask extends Task {

        PollingTask(TranscribeContext context) {
            super(context);
        }

        @Override
        public void execute() throws IOException {
            Response<GetResultResponse> response = yandexApi.operationApi.getResult(context.operationId).execute();
            if (!response.isSuccessful()) {
                ResponseBody errorBody = response.errorBody();
                throw new IOException(
                        String.format(
                                "%s%n%s%n%s",
                                "getResult is not successful",
                                response.message(),
                                errorBody != null ? errorBody.string() : ""
                        )
                );
            }
            GetResultResponse body = response.body();
            if (body == null) {
                throw new IOException(
                        String.format("%s%n%s", response.message(), "getResult body is null")
                );
            }
            if (body.done) {
                if (body.response != null) {
                    setResult(body.response.chunks);
                } else {
                    throw new IOException("No response");
                }
            } else {
                scheduleNextPollingAttempt(context);
            }
        }

        private void setResult(List<GetResultResponse.Chunk> chunks) {
            TranscribeContent content = new ResultConverter().convert(chunks);
            storage.saveResult(context.id, content);
            storage.saveMeta(context.id, TranscribeMeta.ready(context.config, context.audio.downloadLink));
        }

    }

}

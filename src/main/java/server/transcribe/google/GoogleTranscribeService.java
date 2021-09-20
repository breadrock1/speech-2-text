package server.transcribe.google;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeakerDiarizationConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import server.single_transcription.SingleTranscriptionState;
import server.transcribe.TranscribeContent;
import server.transcribe.TranscribeResult;
import server.transcribe.TranscribeService;
import server.transcribe.TranscriptionConfig;
import server.transcribe.TranscriptionInput;
import server.transcribe.common.TranscribeMeta;
import server.util.GoogleCloudUtils;
import wav.WavProcessor;

import javax.annotation.Nullable;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleTranscribeService implements TranscribeService {

    private static final String DEFAULT_MODEL = "default";

    private GoogleObjectStorage storage;

    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    @Override
    public void init() throws IOException {
        storage = new GoogleObjectStorage(GoogleCloudUtils.getProjectId());
    }

    @Override
    public String generateUploadUrl(String id) {
        return storage.generateUploadUrl(id);
    }

    @Override
    public void startTranscription(String id, TranscriptionConfig config) {
        TranscribeContext context = new TranscribeContext(id, null, config);
        storage.saveMeta(id, TranscribeMeta.inProgress(config));
        executor.submit(new LoadTask(context));
    }

    @Override
    public void startTranscription(String id, byte[] data, TranscriptionConfig config) {
        TranscribeContext context = new TranscribeContext(id, data, config);
        storage.saveMeta(id, TranscribeMeta.inProgress(config));
        executor.submit(new StereoToMonoTask(context));
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

    private void finishWithError(TranscribeContext context, Exception e) {
        storage.saveResult(context.id, TranscribeContent.error(e.getMessage()));
        storage.saveMeta(context.id, TranscribeMeta.error(context.config));
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

    private class LoadTask extends Task {

        LoadTask(TranscribeContext context) {
            super(context);
        }

        @Override
        public void execute() {
            context.data = storage.readAudio(context.fileName);
            executor.execute(new StereoToMonoTask(context));
        }
    }

    private class StereoToMonoTask extends Task {

        StereoToMonoTask(TranscribeContext context) {
            super(context);
        }

        @Override
        public void execute() throws IOException {
            try {
                context.data = new WavProcessor().stereoToMono(context.data);
                executor.execute(new UploadTask(context));
            } catch (UnsupportedAudioFileException e) {
                throw new IOException(e);
            }
        }
    }

    private class UploadTask extends Task {

        UploadTask(TranscribeContext context) {
            super(context);
        }

        @Override
        public void execute() {
            context.audio = storage.saveAudio(context.fileName, context.data);
            executor.execute(new RecognitionTask(context));
        }
    }

    private class RecognitionTask extends Task {

        RecognitionTask(TranscribeContext context) {
            super(context);
        }

        @Override
        public void execute() throws IOException {
            context.speechClient = SpeechClient.create();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(context.audio.gsUri).build();

            // Use non-blocking call for getting file transcription
            context.response = context.speechClient.longRunningRecognizeAsync(
                    toRecognitionConfig(context.config), audio
            );
            context.response.addListener(new RecognitionFinishTask(context), executor);
        }

        private RecognitionConfig toRecognitionConfig(TranscriptionConfig config) {
            RecognitionConfig.Builder recognitionBuilder = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setAudioChannelCount(1)
                    .setLanguageCode("ru-RU")
                    .setModel(config.getModel() == null ? DEFAULT_MODEL : config.getModel())
                    .setEnableAutomaticPunctuation(true)
                    .setEnableWordTimeOffsets(true);

            if (config.isDiarizationEnabled()) {
                SpeakerDiarizationConfig.Builder diarizationBuilder = SpeakerDiarizationConfig.newBuilder()
                        .setEnableSpeakerDiarization(config.isDiarizationEnabled());
                if (config.getMinSpeakerCount() > 0) {
                    diarizationBuilder.setMinSpeakerCount(config.getMinSpeakerCount());
                }
                if (config.getMaxSpeakerCount() > 0) {
                    diarizationBuilder.setMaxSpeakerCount(config.getMaxSpeakerCount());
                }
                recognitionBuilder.setDiarizationConfig(diarizationBuilder.build());
            }

            return recognitionBuilder.build();
        }
    }

    private class RecognitionFinishTask extends Task {

        RecognitionFinishTask(TranscribeContext context) {
            super(context);
        }

        @Override
        public void execute() {
            try {
                saveResult(context.response.get().getResultsList());
                storage.saveMeta(context.id, TranscribeMeta.ready(context.config, context.audio.downloadLink));
            } catch (Exception e) {
                finishWithError(context, e);
            }
            context.speechClient.close();
        }

        private void saveResult(List<SpeechRecognitionResult> resultsList) {
            TranscribeContent content = new ResultConverter().convert(context.config, resultsList);
            storage.saveResult(context.id, content);
        }

    }

    private static class TranscribeContext {
        final String id;
        final TranscriptionConfig config;
        final String fileName;

        byte[] data;
        StorageObjectInfo audio;
        SpeechClient speechClient;
        OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response;

        private TranscribeContext(String id, @Nullable byte[] data, TranscriptionConfig config) {
            this.id = id;
            this.data = data;
            this.config = config;

            fileName = id;
        }
    }

}

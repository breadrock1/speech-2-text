package server.realtime_transcribe;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;
import java.util.Arrays;
import java.util.Collections;
import server.logging.Logger;
import server.logging.LoggerFactory;
import server.response.transcribe.TranscribeResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RealtimeTranscriber {

    private final String model;

    private boolean ready;

    private SpeechClient client;
    private Observer responseObserver;
    private ClientStream<StreamingRecognizeRequest> clientStream;

    private final List<TranscribeResult> transcribeResults = new ArrayList<>();
    private final Logger logger = LoggerFactory.createFor(RealtimeTranscriber.class);

    public RealtimeTranscriber(String model) {
        this.model = model;
    }

    public synchronized void append(final byte[] data) throws IOException {
        prepareIfNeeded();

        StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                .setAudioContent(ByteString.copyFrom(data))
                .build();
        clientStream.send(request);
    }

    public List<TranscribeResult> update(TranscribeResult transcribeResult) {
        synchronized (transcribeResults) {
            this.transcribeResults.clear();
            this.transcribeResults.addAll(Collections.singletonList(transcribeResult));
        }
        return transcribeResults;
    }

    public List<TranscribeResult> flushResult() {
        List<TranscribeResult> result;
        synchronized (transcribeResults) {
            result = new ArrayList<>(transcribeResults);
            transcribeResults.clear();
        }
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    public synchronized void stop() {
        if (!ready) {
            return;
        }
        ready = false;

        client.close();
        client = null;

        responseObserver = null;

        clientStream.closeSend();
        clientStream = null;
    }

    private void prepareIfNeeded() throws IOException {
        if (ready) {
            return;
        }

        client = SpeechClient.create();
        responseObserver = new Observer();
        clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

        RecognitionConfig recognitionConfig =
                RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setLanguageCode("ru-RU")
                        .setSampleRateHertz(16000)
                        .setAudioChannelCount(1)
                        .setModel(model) // https://cloud.google.com/speech-to-text/docs/transcription-model
                        .setEnableAutomaticPunctuation(true)
                        .build();
        StreamingRecognitionConfig streamingRecognitionConfig =
                StreamingRecognitionConfig.newBuilder()
                        .setConfig(recognitionConfig)
                        .setInterimResults(true)
                        .build();

        StreamingRecognizeRequest request =
                StreamingRecognizeRequest.newBuilder()
                        .setStreamingConfig(streamingRecognitionConfig)
                        .build(); // The first request in a streaming call has to be a config

        clientStream.send(request);

        ready = true;
    }

    private class Observer implements ResponseObserver<StreamingRecognizeResponse> {

        @Override
        public void onStart(final StreamController controller) {
        }

        @Override
        public void onResponse(final StreamingRecognizeResponse response) {
            StreamingRecognitionResult result = null;
            try {
                result = response.getResultsList().get(0);
            } catch (IndexOutOfBoundsException e) {
                return;
            }
            synchronized (transcribeResults) {
                transcribeResults.add(new TranscribeResult(result));
            }
        }

        @Override
        public void onError(final Throwable t) {
            stop();
            logger.error(t, "Observer.onError");
        }

        @Override
        public void onComplete() {
            stop();

        }
    }
}

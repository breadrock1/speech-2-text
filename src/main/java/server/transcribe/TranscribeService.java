package server.transcribe;

import server.single_transcription.SingleTranscriptionState;

import java.io.IOException;

public interface TranscribeService {

    void init() throws IOException;

    String generateUploadUrl(String id);

    void startTranscription(String id, TranscriptionConfig config);

    void startTranscription(String id, byte[] data, TranscriptionConfig config);

    SingleTranscriptionState getState(String id);

    TranscribeResult getResult(String id);

    boolean editResult(String id, TranscribeContent content);
}

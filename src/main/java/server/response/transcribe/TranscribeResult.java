package server.response.transcribe;

import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognitionResult;

public class TranscribeResult {
    public final boolean isFinal;
    public final String transcript;
    public final float confidence;

    public TranscribeResult(final StreamingRecognitionResult result) {
        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
        this.isFinal = result.getIsFinal();
        this.transcript = alternative.getTranscript().trim();
        this.confidence = alternative.getConfidence();
    }

    public TranscribeResult(final SpeechRecognitionResult result) {
        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
        this.isFinal = true;
        this.transcript = alternative.getTranscript().trim();
        this.confidence = alternative.getConfidence();
    }

    public TranscribeResult(final String transcript) {
        this.isFinal = true;
        this.transcript = transcript.trim();
        this.confidence = -1;
    }
}

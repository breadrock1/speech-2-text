package server.transcribe;

public class TranscriptionInput {

    public static final String NO_URL = "NO_URL";

    private final TranscriptionConfig config;
    private final String audioUrl;

    public TranscriptionInput(TranscriptionConfig config) {
        this(config, NO_URL);
    }

    public TranscriptionInput(TranscriptionConfig config, String audioUrl) {
        this.config = config;
        this.audioUrl = audioUrl;
    }

    public TranscriptionConfig getConfig() {
        return config;
    }

    public String getAudioUrl() {
        return audioUrl;
    }
}

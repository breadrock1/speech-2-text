package server.transcribe.common;

import server.single_transcription.SingleTranscriptionState;
import server.transcribe.TranscriptionConfig;

import javax.annotation.Nullable;

public class TranscribeMeta {
    private static final String NO_URL = "NO_URL";

    @Nullable
    public final TranscriptionConfig config;

    public final SingleTranscriptionState state;

    public final String audioUrl;

    protected TranscribeMeta(@Nullable TranscriptionConfig config, SingleTranscriptionState state, String audioUrl) {
        this.config = config;
        this.state = state;
        this.audioUrl = audioUrl;
    }

    public static TranscribeMeta notStarted() {
        return new TranscribeMeta(null, SingleTranscriptionState.NOT_STARTED, NO_URL);
    }

    public static TranscribeMeta inProgress(TranscriptionConfig config) {
        return new TranscribeMeta(config, SingleTranscriptionState.IN_PROGRESS, NO_URL);
    }

    public static TranscribeMeta ready(TranscriptionConfig config, String audioUrl) {
        return new TranscribeMeta(config, SingleTranscriptionState.READY, audioUrl);
    }

    public static TranscribeMeta error(TranscriptionConfig config) {
        return new TranscribeMeta(config, SingleTranscriptionState.READY, NO_URL);
    }
}

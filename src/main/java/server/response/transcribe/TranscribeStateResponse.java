package server.response.transcribe;

import server.response.GenericResponse;
import server.single_transcription.SingleTranscriptionState;

public class TranscribeStateResponse extends GenericResponse {

    public final SingleTranscriptionState state;

    public TranscribeStateResponse(final SingleTranscriptionState state) {
        this.state = state;
    }
}

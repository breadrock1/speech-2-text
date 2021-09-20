package server.response.transcribe;

import server.response.GenericResponse;

public class TranscribeStartResponse extends GenericResponse {

    private final String transcribeId;

    public TranscribeStartResponse(String transcribeId) {
        this.transcribeId = transcribeId;
    }
}

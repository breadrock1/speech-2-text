package server.response.transcribe;

import server.response.GenericResponse;

public class TranscribeUploadResponse extends GenericResponse {

    private final String transcribeId;

    public TranscribeUploadResponse(String transcribeId) {
        this.transcribeId = transcribeId;
    }
}

package server.response.transcribe;

import server.response.GenericResponse;
import server.transcribe.UploadUrl;

public class TranscribeGenerateUploadUrlResponse extends GenericResponse {

    private final String transcribeId;
    private final String url;

    public TranscribeGenerateUploadUrlResponse(UploadUrl url) {
        this.transcribeId = url.transcribeId;
        this.url = url.url;
    }
}

package server.transcribe;

public class UploadUrl {

    public final String transcribeId;
    public final String url;

    UploadUrl(String transcribeId, String url) {
        this.transcribeId = transcribeId;
        this.url = url;
    }
}

package server.response.speechpad;

import java.util.List;
import server.response.GenericResponse;
import server.response.transcribe.TranscribeResult;

public class SpeechpadGetResponse extends GenericResponse {

    private final String speechpadId;
    private final String speechpadName;
    private final List<TranscribeResult> transcribeResult;

    public SpeechpadGetResponse(String speechpadId, String speechpadName, List<TranscribeResult> transcribeResult) {
        this.speechpadId = speechpadId;
        this.speechpadName = speechpadName;
        this.transcribeResult = transcribeResult;
    }
}

package server.response.speechpad;

import server.response.GenericResponse;
import server.response.transcribe.TranscribeResult;

public class SpeechpadGetResponse extends GenericResponse {

    private final String speechpadId;
    private final String speechpadName;
    private final TranscribeResult transcribeResult;

    public SpeechpadGetResponse(String speechpadId, String speechpadName, TranscribeResult transcribeResult) {
        this.speechpadId = speechpadId;
        this.speechpadName = speechpadName;
        this.transcribeResult = transcribeResult;
    }
}

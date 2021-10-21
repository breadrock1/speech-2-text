package server.response.speechpad;

import server.response.GenericResponse;

public class SpeechpadFullObjectResponse extends GenericResponse {

    private final String speechpadId;
    private final String speechpadName;
    private final String transcribeResult;

    public SpeechpadFullObjectResponse(String speechpadId, String speechpadName, String transcribeResult) {
        this.speechpadId = speechpadId;
        this.speechpadName = speechpadName;
        this.transcribeResult = transcribeResult;
    }
}

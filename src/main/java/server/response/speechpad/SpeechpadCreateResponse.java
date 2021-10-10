package server.response.speechpad;

import server.response.GenericResponse;

public class SpeechpadCreateResponse extends GenericResponse {

    private final String speechpadId;
    private final String speechPadName;

    public SpeechpadCreateResponse(String speechpadId, String speechPadName) {
        this.speechpadId = speechpadId;
        this.speechPadName = speechPadName;
    }
}

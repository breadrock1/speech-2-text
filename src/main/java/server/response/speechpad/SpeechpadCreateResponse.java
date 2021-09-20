package server.response.speechpad;

import server.response.GenericResponse;

public class SpeechpadCreateResponse extends GenericResponse {

    private final String speechpadId;

    public SpeechpadCreateResponse(String speechpadId) {
        this.speechpadId = speechpadId;
    }
}

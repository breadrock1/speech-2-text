package server.response.speechpad;

import server.response.GenericResponse;

public class SpeechpadRemoveResponse extends GenericResponse {

    private final String speechpadId;

    public SpeechpadRemoveResponse(String speechpadId) {
        this.speechpadId = speechpadId;
    }
}

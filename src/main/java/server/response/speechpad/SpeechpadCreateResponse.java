package server.response.speechpad;

import server.response.GenericResponse;

public class SpeechpadCreateResponse extends GenericResponse {

    private final String speechpadId;
    private final String speechpadName;

    public SpeechpadCreateResponse(String speechpadId, String speechpadName) {
        this.speechpadId = speechpadId;
        this.speechpadName = speechpadName;
    }
}

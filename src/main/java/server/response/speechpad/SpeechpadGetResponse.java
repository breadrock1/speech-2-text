package server.response.speechpad;

import server.response.GenericResponse;
import server.speechpad.Speechpad;

public class SpeechpadGetResponse extends GenericResponse {

    private final Speechpad speechpad;

    public SpeechpadGetResponse(Speechpad speechpad) {
        this.speechpad = speechpad;
    }
}

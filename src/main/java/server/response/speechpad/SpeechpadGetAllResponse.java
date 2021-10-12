package server.response.speechpad;

import server.response.GenericResponse;
import server.speechpad.Speechpad;

import java.util.ArrayList;
import java.util.List;

public class SpeechpadGetAllResponse extends GenericResponse {

    private final List<Speechpad> speechpads;

    public SpeechpadGetAllResponse(List<Speechpad> speechpads) {
        this.speechpads = speechpads;
    }
}
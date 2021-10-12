package server.response.speechpad;

import server.response.GenericResponse;
import server.speechpad.Speechpad;

import java.util.ArrayList;
import java.util.List;

public class SpeechpadGetAllResponse extends GenericResponse {

    private List<Speechpad> speechpads = new ArrayList<>();

    public SpeechpadGetAllResponse(List<Speechpad> speechpads) {
        this.speechpads = speechpads;
    }
}
package server.response.speechpad;

import server.response.GenericResponse;
import server.speechpad.Speechpad;

import java.util.ArrayList;
import java.util.List;

public class SpeechpadGetAllResponse extends GenericResponse {

    private List<Speechpad> allSpeechpad = new ArrayList<>();

    public SpeechpadGetAllResponse(List<Speechpad> allSpeechpad) {
        this.allSpeechpad = allSpeechpad;
    }
}

package server.response.speechpad;

import java.util.Map;
import server.response.GenericResponse;
import server.speechpad.Speechpad;

public class SpeechpadMapResponse extends GenericResponse {

    private final Map<String, Speechpad> archives;

    public SpeechpadMapResponse(Map<String, Speechpad> archives) {
        this.archives = archives;
    }

}



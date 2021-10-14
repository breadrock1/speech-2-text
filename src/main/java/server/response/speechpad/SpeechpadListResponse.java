package server.response.speechpad;

import java.util.List;
import server.response.GenericResponse;
import server.speechpad.Speechpad;

public class SpeechpadListResponse extends GenericResponse {

    private final List<Speechpad> archives;

    public SpeechpadListResponse(final List<Speechpad> archives) {
        this.archives = archives;
    }
}

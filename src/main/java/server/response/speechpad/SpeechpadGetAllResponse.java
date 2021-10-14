package server.response.speechpad;

import java.util.List;
import server.speechpad.Speechpad;

public class SpeechpadGetAllResponse {

    private final List<Speechpad> speechpads;

    public SpeechpadGetAllResponse(final List<Speechpad> speechpads) {
        this.speechpads = speechpads;
    }
}

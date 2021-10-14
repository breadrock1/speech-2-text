package server.response.speechpad;

import java.util.List;
import server.response.GenericResponse;

public class SpeechpadListResponse extends GenericResponse {

    private final List<String> speechpads;

    public SpeechpadListResponse(final List<String> speechpads) {
        this.speechpads = speechpads;
    }
}

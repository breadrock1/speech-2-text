package server.response.speechpad;

import java.util.List;
import javax.annotation.Nullable;
import server.response.GenericResponse;

public class SpeechpadListResponse extends GenericResponse {

    @Nullable
    private final List<String> speechpads;

    public SpeechpadListResponse(@Nullable final List<String> speechpads) {
        this.speechpads = speechpads;
    }
}

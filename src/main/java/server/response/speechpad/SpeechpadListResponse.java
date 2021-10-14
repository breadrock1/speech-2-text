package server.response.speechpad;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import server.response.GenericResponse;

public class SpeechpadListResponse extends GenericResponse {

    @Nullable
    private final List<Map<String, String>> speechpads;

    public SpeechpadListResponse(@Nullable final List<Map<String, String>> speechpads) {
        this.speechpads = speechpads;
    }
}

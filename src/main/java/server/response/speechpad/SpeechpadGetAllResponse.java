package server.response.speechpad;

import javax.annotation.Nullable;
import server.response.GenericResponse;
import server.speechpad.Speechpad;

import java.util.List;

public class SpeechpadGetAllResponse extends GenericResponse {

    @Nullable
    public final List<Speechpad> speechpads;

    public SpeechpadGetAllResponse(@Nullable final List<Speechpad> speechpads) {
        this.speechpads = speechpads;
    }
}
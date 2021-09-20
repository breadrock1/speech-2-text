package server.response.conference;

import java.util.List;

public class ConferenceParticipantsResponse {

    private final List<ConferenceParticipant> participants;

    public ConferenceParticipantsResponse(List<ConferenceParticipant> participants) {
        this.participants = participants;
    }

}

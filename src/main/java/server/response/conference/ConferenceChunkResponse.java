package server.response.conference;

import server.response.GenericResponse;

import javax.annotation.Nullable;
import java.util.List;

public class ConferenceChunkResponse extends GenericResponse {

    public final List<ConferenceParticipantTranscript> transcripts;

    @Nullable
    private final List<ConferenceParticipant> participants;

    public ConferenceChunkResponse(
            final List<ConferenceParticipantTranscript> transcripts,
            @Nullable final List<ConferenceParticipant> participants
    ) {
        this.transcripts = transcripts;
        this.participants = participants;
    }

}

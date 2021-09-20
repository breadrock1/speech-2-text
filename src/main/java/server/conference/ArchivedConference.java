package server.conference;

import server.response.conference.ConferenceParticipantTranscript;

import java.util.ArrayList;
import java.util.List;

public class ArchivedConference {

    private final String id;
    private final String name;
    private final List<ConferenceParticipantTranscript> transcripts = new ArrayList<>();

    public ArchivedConference(
            final String id,
            final String name,
            final List<ConferenceParticipantTranscript> transcripts
    ) {
        this.id = id;
        this.name = name;
        this.transcripts.addAll(transcripts);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ConferenceParticipantTranscript> getTranscripts() {
        return new ArrayList<>(transcripts);
    }
}

package server.conference;

import server.response.conference.ConferenceParticipantTranscript;

import java.util.List;

public class ConferencePrinter {

    public String printAsString(final ArchivedConference conference) {
        List<ConferenceParticipantTranscript> transcripts = conference.getTranscripts();
        StringBuilder result = new StringBuilder();
        result.append(conference.getName()).append('\n');
        // TODO creation date
        for (ConferenceParticipantTranscript t : transcripts) {
            result.append(t.participantName).append(": ").append(t.value).append('\n');
        }
        return result.toString();
    }

}

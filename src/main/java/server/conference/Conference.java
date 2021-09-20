package server.conference;

import server.realtime_transcribe.RealtimeTranscriber;
import server.response.conference.ConferenceParticipantTranscript;
import server.response.transcribe.TranscribeResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Conference {

    private final String id;
    private final String name;

    private final Map<String, Participant> participantMap = new HashMap<>();
    private final Map<String, Integer> participantPositionMap = new HashMap<>();
    final List<ConferenceParticipantTranscript> transcripts = new ArrayList<>();

    private String hostId;

    public Conference(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public synchronized String getHostId() {
        return Objects.requireNonNull(hostId, "Conference has no host");
    }

    public synchronized void setHost(final String hostId, final String hostName) {
        this.hostId = hostId;
        addSpeaker(hostId, hostName);
    }

    public synchronized void addSpeaker(final String speakerId, final String speakerName) {
        participantMap.put(speakerId, new Participant(this, speakerId, speakerName));
        participantPositionMap.put(speakerId, transcripts.size());
    }

    public synchronized Participant getSpeaker(final String id) throws NoSuchParticipantException {
        Participant participant = participantMap.get(id);
        if (participant == null) {
            throw new NoSuchParticipantException(id);
        }
        return participant;
    }

    public synchronized List<Participant> getAllSpeakers(Participant.State state) {
        return participantMap.values()
                .stream()
                .filter(participant -> participant.getState() == state)
                .collect(Collectors.toList());
    }

    public synchronized List<ConferenceParticipantTranscript> flushTranscriptsForParticipant(final Participant participant) {
        int position = participantPositionMap.getOrDefault(participant.id, 0);
        List<ConferenceParticipantTranscript> result = transcripts.subList(position, transcripts.size());
        participantPositionMap.put(participant.id, position + result.size());
        return result;
    }

    synchronized void appendResultsForParticipant(
            final Participant participant,
            final List<TranscribeResult> results
    ) {
        for (TranscribeResult result : results) {
            transcripts.add(new ConferenceParticipantTranscript(participant.id, participant.name, result));
        }
    }

    RealtimeTranscriber createTranscriber() {
        return new RealtimeTranscriber("phone_call");
    }

    synchronized void clear() {
        for (Participant participant : participantMap.values()) {
            participant.clear();
        }
        participantMap.clear();
        participantPositionMap.clear();
        transcripts.clear();
    }
}

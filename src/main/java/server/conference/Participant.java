package server.conference;

import server.realtime_transcribe.RealtimeTranscriber;

import java.io.IOException;

public class Participant {

    private final Conference conference;
    private final RealtimeTranscriber transcriber;

    final String id;
    final String name;

    private long lastActivityTimestamp;
    private State state = State.INACTIVE;

    Participant(
            final Conference conference,
            final String id,
            final String name
    ) {
        this.conference = conference;
        this.id = id;
        this.name = name;
        this.transcriber = conference.createTranscriber();
    }

    public String getName() {
        return name;
    }

    public synchronized void append(final byte[] body) throws IOException {
        state = State.ACTIVE;
        transcriber.append(body);
        conference.appendResultsForParticipant(this, transcriber.flushResult());
        updateLastActivity();
    }

    public synchronized void updateLastActivity() {
        lastActivityTimestamp = System.currentTimeMillis();
    }

    public synchronized long getLastActivityTimestamp() {
        return lastActivityTimestamp;
    }

    public synchronized State getState() {
        return this.state;
    }

    public synchronized void deactivate() {
        this.state = State.INACTIVE;
    }

    synchronized void clear() {
        transcriber.stop();
    }

    public enum State {
        ACTIVE,
        INACTIVE
    }
}

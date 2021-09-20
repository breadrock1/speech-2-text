package server.conference;

public class NoSuchParticipantException extends Exception {

    public NoSuchParticipantException(final String participantId) {
        super(String.format("There is participant with id '%s'", participantId));
    }

}

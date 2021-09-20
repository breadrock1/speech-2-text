package server.conference;

public class NoSuchConferenceException extends Exception {

    public NoSuchConferenceException(final String conferenceId) {
        super(String.format("There is no conference with id '%s'", conferenceId));
    }

}

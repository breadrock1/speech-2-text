package server.speechpad;

public class NoSuchSpeechpadException extends Exception {

    public NoSuchSpeechpadException(String speechpadId) {
        super(String.format("There is no speechpad with id '%s'", speechpadId));
    }
}

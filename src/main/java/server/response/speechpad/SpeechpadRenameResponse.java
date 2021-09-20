package server.response.speechpad;

import server.response.GenericResponse;

public class SpeechpadRenameResponse extends GenericResponse {

    private final String newName;
    private final String speechpadId;

    public SpeechpadRenameResponse(String speechpadId, String newName) {
        this.newName = newName;
        this.speechpadId = speechpadId;
    }
}

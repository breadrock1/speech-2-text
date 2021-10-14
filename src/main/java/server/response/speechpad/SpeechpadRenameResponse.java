package server.response.speechpad;

import server.response.GenericResponse;

public class SpeechpadRenameResponse extends GenericResponse {

    private final String speechpadId;
    private final String speechpadName;

    public SpeechpadRenameResponse(String speechpadId, String speechpadName) {
        this.speechpadId = speechpadId;
        this.speechpadName = speechpadName;
    }
}

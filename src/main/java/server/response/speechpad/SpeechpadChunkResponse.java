package server.response.speechpad;

import server.response.GenericResponse;
import server.response.transcribe.TranscribeResult;

import java.util.List;

public class SpeechpadChunkResponse extends GenericResponse {

    private final List<TranscribeResult> results;

    public SpeechpadChunkResponse(List<TranscribeResult> results) {
        this.results = results;
    }
}

package server.response.speechpad;

import javax.annotation.Nullable;
import server.response.GenericResponse;
import server.response.transcribe.TranscribeResult;

import java.util.List;

public class SpeechpadChunkResponse extends GenericResponse {

    @Nullable
    private final List<TranscribeResult> results;

    public SpeechpadChunkResponse(@Nullable List<TranscribeResult> results) {
        this.results = results;
    }
}

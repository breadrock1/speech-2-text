package server.speechpad;

import server.response.transcribe.TranscribeResult;
import server.realtime_transcribe.RealtimeTranscriber;

import java.io.IOException;
import java.util.List;

public class Speechpad {

    private String name;
    private final String id;
    private final RealtimeTranscriber transcriber;

    public Speechpad(String id, String name, RealtimeTranscriber transcriber) {
        this.id = id;
        this.name = name;
        this.transcriber = transcriber;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TranscribeResult> append(byte[] body) throws IOException {
        transcriber.append(body);
        return transcriber.flushResult();
    }

    public List<TranscribeResult> setEditedResult(TranscribeResult result) {
        transcriber.setEditedResult(result);
        return transcriber.flushResult();
    }
}

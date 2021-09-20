package server.speechpad;

import server.response.transcribe.TranscribeResult;
import server.realtime_transcribe.RealtimeTranscriber;

import java.io.IOException;
import java.util.List;

public class Speechpad {

    private final String id;
    private final RealtimeTranscriber transcriber;

    public Speechpad(String id, RealtimeTranscriber transcriber) {
        this.id = id;
        this.transcriber = transcriber;
    }

    public String getId() {
        return id;
    }

    public List<TranscribeResult> append(byte[] body) throws IOException {
        transcriber.append(body);
        return transcriber.flushResult();
    }
}

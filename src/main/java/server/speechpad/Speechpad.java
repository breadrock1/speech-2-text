package server.speechpad;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import server.response.transcribe.TranscribeResult;
import server.realtime_transcribe.RealtimeTranscriber;


public class Speechpad {

    private String name;
    private String id;
    private RealtimeTranscriber transcriber;

    public Speechpad() {
    }

    public Speechpad(String id, String name, RealtimeTranscriber transcriber) {
        this.id = id;
        this.name = name;
        this.transcriber = transcriber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTranscribe() {
        List<TranscribeResult> result = transcriber.flushResult();
        return result.stream()
            .map(t -> t.transcript)
            .collect(Collectors.joining(" "));
    }

    public void setTranscribe(String result) {
        this.transcriber = new RealtimeTranscriber("default");
        this.transcriber.update(new TranscribeResult(result));
    }

    public List<TranscribeResult> append(byte[] body) throws IOException {
        transcriber.append(body);
        return transcriber.flushResult();
    }

}

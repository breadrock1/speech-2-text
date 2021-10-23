package server.speechpad;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import server.response.transcribe.TranscribeResult;
import server.realtime_transcribe.RealtimeTranscriber;

public class Speechpad {

    private String id;
    private String name;
    private TranscribeResult transcribe;
    public RealtimeTranscriber realtimeTranscriber;

    public Speechpad() {
        this.transcribe = new TranscribeResult("");
        this.realtimeTranscriber = new RealtimeTranscriber("default");
    }

    public Speechpad(String id, String name, TranscribeResult transcribe) {
        this.id = id;
        this.name = name;
        this.transcribe = transcribe;
        this.realtimeTranscriber = new RealtimeTranscriber("default");
    }

    public Speechpad(String id, String name, RealtimeTranscriber realtimeTranscriber) {
        this.id = id;
        this.name = name;
        this.transcribe = new TranscribeResult("");
        this.realtimeTranscriber = realtimeTranscriber;
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

    public TranscribeResult getTranscribe() {
        return this.transcribe;
    }

    public void setTranscribe(TranscribeResult transcribe) {
        this.transcribe = transcribe;
    }

    public List<TranscribeResult> append(byte[] body) throws IOException {
        realtimeTranscriber.append(body);
        List<TranscribeResult> flushed = realtimeTranscriber.flushResult();
        realtimeTranscriber.updateAllTrascripts(flushed);
        return flushed;
    }

}

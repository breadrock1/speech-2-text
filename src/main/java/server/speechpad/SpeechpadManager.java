package server.speechpad;

import java.util.ArrayList;
import java.util.List;
import server.realtime_transcribe.RealtimeTranscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeechpadManager {

    private final Map<String, Speechpad> speechpadMap = new HashMap<>();

    public Speechpad create(String model) {
        String speechpadId = UUID.randomUUID().toString();
        Speechpad speechpad = new Speechpad(speechpadId, new RealtimeTranscriber(model));
        synchronized (speechpadMap) {
            speechpadMap.put(speechpadId, speechpad);
        }
        return speechpad;
    }

    public boolean delete(String speechpadId) {
        synchronized (speechpadMap) {
            if (!speechpadMap.containsKey(speechpadId)) {
                return false;
            }
            speechpadMap.remove(speechpadId);
            return true;
        }
    }

    public List<Speechpad> getAllSpeechpads() {
        synchronized (speechpadMap) {
            return new ArrayList<>(speechpadMap.values());
        }
    }

    public Speechpad getSpeechpad(String speechpadId) throws NoSuchSpeechpadException {
        Speechpad speechpad;
        synchronized (speechpadMap) {
            speechpad = speechpadMap.get(speechpadId);
        }
        if (speechpad == null) {
            throw new NoSuchSpeechpadException(speechpadId);
        }
        return speechpad;
    }
}

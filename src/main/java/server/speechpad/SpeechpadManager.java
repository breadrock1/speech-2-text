package server.speechpad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import server.realtime_transcribe.RealtimeTranscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeechpadManager {

    private final Map<String, Speechpad> speechpadMap = new HashMap<>();

    public Speechpad create(String model, String name) {
        String speechpadId = UUID.randomUUID().toString();
        String speechpadName = (name == null) ? speechpadId : name;
        Speechpad speechpad = new Speechpad(speechpadId, speechpadName, new RealtimeTranscriber(model));
        synchronized (speechpadMap) {
            speechpadMap.put(speechpadId, speechpad);
        }
        return speechpad;
    }

    public void delete(String speechpadId) throws NoSuchSpeechpadException {
        synchronized (speechpadMap) {
            if (!speechpadMap.containsKey(speechpadId)) {
                throw new NoSuchSpeechpadException(speechpadId);
            }
            speechpadMap.remove(speechpadId);
        }
    }

    public synchronized List<Map<String, String>> getAllSpeechpads() {
        return speechpadMap.values()
                .stream()
                .map(s -> new HashMap<String, String>() {{
                    put("speechpadId", s.getId());
                    put("speechpadName", s.getName());
                }})
                .collect(Collectors.toList());
    }

    public Speechpad getSpeechpad(String speechpadId) throws NoSuchSpeechpadException {
        Speechpad speechpad;
        synchronized (speechpadMap) {
            speechpad = speechpadMap.getOrDefault(speechpadId, null);
        }
        if (speechpad == null) {
            throw new NoSuchSpeechpadException(speechpadId);
        }
        return speechpad;
    }
}

package server.speechpad;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public void delete(String speechpadId) throws NoSuchSpeechpadException {
        synchronized (speechpadMap) {
            if (!speechpadMap.containsKey(speechpadId)) {
                throw new NoSuchSpeechpadException(speechpadId);
            }
            speechpadMap.remove(speechpadId);
        }
    }

    public void rename(String speechpadId, String newName) throws NoSuchSpeechpadException {
        synchronized (speechpadMap) {
            Optional.ofNullable(speechpadMap.get(speechpadId))
                .orElseThrow(() -> new NoSuchSpeechpadException(speechpadId))
                .setName(newName);
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
            speechpad = speechpadMap.getOrDefault(speechpadId, null);
        }
        if (speechpad == null) {
            throw new NoSuchSpeechpadException(speechpadId);
        }
        return speechpad;
    }
}

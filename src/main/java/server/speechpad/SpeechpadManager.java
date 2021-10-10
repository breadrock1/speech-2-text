package server.speechpad;

import server.realtime_transcribe.RealtimeTranscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public synchronized List<Speechpad> getAllSpeechpad() {
        return new ArrayList<>(speechpadMap.values());
    }

    public synchronized Speechpad getSpeechpad(String speechpadId) throws NoSuchSpeechpadException {
        return Optional.ofNullable(speechpadMap.get(speechpadId))
            .orElseThrow(() -> new NoSuchSpeechpadException(speechpadId));
    }

    public synchronized boolean delete(String speechpadId) {
        if (!speechpadMap.containsKey(speechpadId)) {
            return false;
        }
        speechpadMap.remove(speechpadId);
        return true;
    }

    public synchronized void rename(String speechpadId, String newName) throws NoSuchSpeechpadException {
        Optional.ofNullable(speechpadMap.get(speechpadId))
                .orElseThrow(() -> new NoSuchSpeechpadException(speechpadId))
                .setName(newName);
    }

}

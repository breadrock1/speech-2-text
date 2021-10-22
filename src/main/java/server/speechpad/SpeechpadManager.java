package server.speechpad;

import com.google.cloud.firestore.Firestore;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import server.realtime_transcribe.RealtimeTranscriber;
import server.response.transcribe.TranscribeResult;

public class SpeechpadManager {

    private final Firestore db;

    private final Map<String, Speechpad> speechpadMap = new HashMap<>();

    public SpeechpadManager(Firestore db) {
        this.db = db;
    }

    public Speechpad create(String model, String name) {
        String speechpadId = UUID.randomUUID().toString();
        String speechpadName = (name == null) ? speechpadId : name;
        Speechpad speechpad = new Speechpad(speechpadId, speechpadName, new RealtimeTranscriber(model));
        synchronized (speechpadMap) {
            speechpadMap.put(speechpadId, speechpad);
        }
        return speechpad;
    }

    private void dbDelete(String speechpadId) {
        db.runTransaction(transaction -> {
            db.collection("speechpads")
                .document(speechpadId)
                .delete();
            return null;
        });
    }

    public void delete(String speechpadId) throws NoSuchSpeechpadException {
        dbDelete(speechpadId);
        synchronized (speechpadMap) {
            //if (!speechpadMap.containsKey(speechpadId)) {
            //    throw new NoSuchSpeechpadException(speechpadId);
            //}
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

    public void storeSpeechpad(Speechpad speechpad) {
        TranscribeResult data = speechpad.realtimeTranscriber
                .getAllTranscribeResult()
                .parallelStream()
                .filter(t -> t.isFinal)
                .findFirst()
                .orElse(new TranscribeResult(""));
        speechpad.setTranscribe(data);
        speechpad.realtimeTranscriber.deleteAllTranscribeResult();

        db.runTransaction(transaction -> {
            db.collection("speechpads")
                .document(speechpad.getId())
                .set(speechpad);
            return null;
        });
    }
}

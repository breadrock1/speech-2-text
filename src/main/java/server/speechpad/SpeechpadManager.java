package server.speechpad;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.stream.StreamSupport;
import server.realtime_transcribe.RealtimeTranscriber;
import server.response.transcribe.TranscribeResult;

public class SpeechpadManager {

    private final Firestore db;

    private final Map<String, Speechpad> speechpadMap = new HashMap<>();


    public SpeechpadManager(Firestore db) {
        this.db = db;
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

    public Speechpad create(String model, String name) {
        String speechpadId = UUID.randomUUID().toString();
        String speechpadName = (name == null) ? speechpadId : name;
        Speechpad speechpad = new Speechpad(speechpadId, speechpadName, new RealtimeTranscriber(model));
        synchronized (speechpadMap) {
            speechpadMap.put(speechpadId, speechpad);
        }
        return speechpad;
    }


    private DocumentSnapshot getSnapshot(DocumentReference docRef) {
        try {
            return docRef.get().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> getAllSpeechpads() {
        Iterable<DocumentReference> iterable = db.collection("speechpads")
            .listDocuments();
        return StreamSupport.stream(iterable.spliterator(), true)
            .map(this::getSnapshot)
            .filter(Objects::nonNull)
            .map(d -> new HashMap<String, String>() {{
                put("speechpadId", d.get("id").toString());
                put("speechpadName", d.get("name").toString());
            }})
            .collect(Collectors.toList());
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
            speechpadMap.remove(speechpadId);
        }
    }


    public boolean renameSpeechpad(String speechpadId, String newName) throws NoSuchSpeechpadException {
        db.collection("speechpads")
            .document(speechpadId)
            .update("name", newName);

        return true;
    }


    public boolean editTransriptResult(String speechpadId, String data) throws NoSuchSpeechpadException {
        Speechpad speechpad = getSpeechpad(speechpadId);
        TranscribeResult result = new TranscribeResult(data);
        speechpad.setTranscribe(result);
        speechpad.realtimeTranscriber.deleteAllTranscribeResult();

        db.runTransaction(transaction -> {
            db.collection("speechpads")
                .document(speechpad.getId())
                .update("transcribe", result);
            return null;
        });

        return true;
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

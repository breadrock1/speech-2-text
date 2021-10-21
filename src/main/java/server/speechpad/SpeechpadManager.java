package server.speechpad;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import server.realtime_transcribe.RealtimeTranscriber;


public class SpeechpadManager {

    private final Firestore db;

    private static final String COLLECTION_SPEECHPADS = "speechpads";

    private final Map<String, Speechpad> speechpadMap = new HashMap<>();


    public SpeechpadManager(Firestore db) {
        this.db = db;
    }


    private Speechpad instanceObjectToSpeechpad(DocumentSnapshot documentSnapshot) {
        return documentSnapshot.toObject(Speechpad.class);
    }

    private Speechpad loadSpeechpadFromDatabase(String speechpadId) {
        try {
            DocumentSnapshot docSnapshot = db.collection(COLLECTION_SPEECHPADS)
                .document(speechpadId)
                .get()
                .get();
            return instanceObjectToSpeechpad(docSnapshot);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteSpeechpadFromDatabase(String speechpadId) {
        db.collection(COLLECTION_SPEECHPADS)
            .document(speechpadId)
            .delete();
    }

    public void storeSpeechpadToDatabase(Speechpad speechpad) {
        db.collection(COLLECTION_SPEECHPADS)
            .document(speechpad.getId())
            .set(speechpad);
    }

    public Speechpad createSpeechpad(String model, String name) {
        String speechpadId = UUID.randomUUID().toString();
        String speechpadName = (name == null) ? speechpadId : name;
        Speechpad speechpad = new Speechpad(speechpadId, speechpadName, new RealtimeTranscriber(model));
        storeSpeechpadToDatabase(speechpad);
        synchronized (speechpadMap) {
            speechpadMap.put(speechpadId, speechpad);
        }
        return speechpad;
    }

    public void deleteSpeechpad(String speechpadId) throws NoSuchSpeechpadException {
        deleteSpeechpadFromDatabase(speechpadId);
        synchronized (speechpadMap) {
            speechpadMap.remove(speechpadId);
        }
    }

    public synchronized List<Map<String, String>> getAllSpeechpads() {
        Iterable<DocumentReference> iterable = db.collection(COLLECTION_SPEECHPADS).listDocuments();
        return StreamSupport.stream(iterable.spliterator(), false)
            .map(DocumentReference::getId)
            .map(this::loadSpeechpadFromDatabase)
            .map(speechpad -> new HashMap<String, String>() {{
                put("speechpadId", speechpad.getId());
                put("speechpadName", speechpad.getName());
            }})
            .collect(Collectors.toList());
    }

    public Speechpad getSpeechpad(String speechpadId) throws NoSuchSpeechpadException {
        Speechpad speechpad;
        synchronized (speechpadMap) {
            speechpad = loadSpeechpadFromDatabase(speechpadId);
        }
        if (speechpad == null) {
            throw new NoSuchSpeechpadException(speechpadId);
        }
        return speechpad;
    }

    public Speechpad renameSpeechpad(String speechpadId, String newName) throws NoSuchSpeechpadException {
        Speechpad speechpad = Optional.ofNullable(loadSpeechpadFromDatabase(speechpadId))
            .orElseThrow(() -> new NoSuchSpeechpadException(speechpadId));
        speechpad.setName(newName);

        db.collection(COLLECTION_SPEECHPADS)
            .document(speechpadId)
            .update("name", newName);

        return speechpad;
    }

    public Speechpad updateSpeechpadTranscribe(String speechpadId, String data) throws NoSuchSpeechpadException {
        Speechpad speechpad = Optional.ofNullable(loadSpeechpadFromDatabase(speechpadId))
            .orElseThrow(() -> new NoSuchSpeechpadException(speechpadId));
        speechpad.setTranscribe(data);

        db.collection(COLLECTION_SPEECHPADS)
            .document(speechpadId)
            .update("transcribe", data);

        return speechpad;
    }

}

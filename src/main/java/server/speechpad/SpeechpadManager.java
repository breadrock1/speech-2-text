package server.speechpad;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldMask;
import com.google.cloud.firestore.Firestore;
import io.jsonwebtoken.io.IOException;
import server.realtime_transcribe.RealtimeTranscriber;

import static server.util.FutureUtils.getFuture;

public class SpeechpadManager {

    private final Firestore db;

    private static final String COLLECTION_SPEECHPADS = "speechpads";

    private final Map<String, Speechpad> speechpadMap = new HashMap<>();


    public SpeechpadManager(Firestore db) {
        this.db = db;
    }


    public void storeSpeechpadToDatabase(Speechpad speechpad) {
        System.out.println(db.collection(COLLECTION_SPEECHPADS));
        db.collection(COLLECTION_SPEECHPADS).add(speechpad);

//        Optional.ofNullable(getFuture(db.runTransaction(transaction -> speechpad)))
//                .orElseThrow(() -> new IOException(""));
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


    private void deleteSpeechpadFromDatabase(String speechpadId) {

    }

    public void delete(String speechpadId) throws NoSuchSpeechpadException {
        synchronized (speechpadMap) {
            if (!speechpadMap.containsKey(speechpadId)) {
                throw new NoSuchSpeechpadException(speechpadId);
            }
            speechpadMap.remove(speechpadId);
        }
    }



    private Speechpad instanceObjectToSpeechpad(DocumentSnapshot docSnapshot) throws NoSuchSpeechpadException {
        return Optional.ofNullable(docSnapshot.toObject(Speechpad.class))
                .orElseThrow(() -> new NoSuchSpeechpadException(""));
    }

    private DocumentReference loadSpeechpadFromDatabase(String speechpadId) {
        return db.collection(COLLECTION_SPEECHPADS).document(speechpadId);
    }

    public Speechpad getStoredSpeechpad(String speechpadId) {
        try {
            ApiFuture<DocumentSnapshot> docRefs = loadSpeechpadFromDatabase(speechpadId).get();
            DocumentSnapshot docSnapshot = docRefs.get();
            return instanceObjectToSpeechpad(docSnapshot);
        } catch (InterruptedException | ExecutionException | NoSuchSpeechpadException e) {
            e.printStackTrace();
            return null;
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


    private List<String> getAllSpeechpadsFromDatabase() throws ExecutionException, InterruptedException {
        return db.getAll(loadSpeechpadFromDatabase("")).get()
                .stream()
                .map(d -> Objects.requireNonNull(d.get(COLLECTION_SPEECHPADS)).toString())
                .collect(Collectors.toList());
    }

    public synchronized List<Map<String, String>> getAllSpeechpads() {
        return speechpadMap.values()
                .stream()
                .map(s -> new HashMap<String, String>() {{
                    put("speechpadId", s.getId());
                    put("speechpadName", s.getName());
                }})
                .collect(Collectors.toList());
        
        StreamSupport.stream(db.collection(COLLECTION_SPEECHPADS).listDocuments().spliterator(), true)
                        .map(doc -> new HashMap<String, String>() {{
                            put("speechpadId", doc.get("id"));
                            put("speechpadName", doc.get("name"));
                        }})
                        .collect(Collectors.toList());
    }


}

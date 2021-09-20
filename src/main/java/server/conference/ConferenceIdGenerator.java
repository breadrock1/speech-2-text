package server.conference;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;

import static server.util.FutureUtils.getFuture;

public class ConferenceIdGenerator {

    private static final String COLLECTION_GENERATOR = "conference_id_generator";
    private static final String DOCUMENT_COUNTER = "counter";

    private final Firestore firestore;

    private ConferenceIdGenerator(Firestore firestore) {
        this.firestore = firestore;
    }

    public String next() {
        return getFuture(firestore.runTransaction(transaction -> {
            DocumentReference document = firestore.collection(COLLECTION_GENERATOR).document(DOCUMENT_COUNTER);
            Counter counter = getFuture(document.get()).toObject(Counter.class);
            if (counter == null) {
                counter = new Counter();
            }
            counter.value += 1;
            getFuture(document.set(counter));
            return String.valueOf(counter.value);
        }));
    }

    public static ConferenceIdGenerator create(Firestore firestore) {
        return new ConferenceIdGenerator(firestore);
    }

    private static class Counter {
        public int value; // need to be public for Firestore
    }
}

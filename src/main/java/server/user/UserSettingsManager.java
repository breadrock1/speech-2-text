package server.user;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;

import java.util.HashMap;
import java.util.Map;

import static server.util.FutureUtils.getFuture;

public class UserSettingsManager {

    private static final String SETTINGS_COLLECTION = "user-settings";

    private final Firestore db;

    public UserSettingsManager(Firestore db) {
        this.db = db;
    }

    public void set(User user, String key, String value) {
        db.runTransaction(transaction -> {
            DocumentReference docRef = settingsDocument(user);
            Map<String, Object> data = getFuture(docRef.get()).getData();
            if (data == null) {
                data = new HashMap<>();
            }
            data.put(key, value);
            getFuture(docRef.set(data));
            return null;
        });
    }

    public String get(User user, String key) {
        Map<String, Object> data = getFuture(settingsDocument(user).get()).getData();
        if (data == null) {
            return "";
        }
        return (String) data.getOrDefault(key, "");
    }

    public void remove(User user, String key) {
        db.runTransaction(transaction -> {
            DocumentReference docRef = settingsDocument(user);
            Map<String, Object> data = getFuture(docRef.get()).getData();
            if (data != null) {
                data.remove(key);
                getFuture(docRef.set(data));
            }
            return null;
        });
    }

    private DocumentReference settingsDocument(User user) {
        return db.collection(SETTINGS_COLLECTION).document(user.getLogin());
    }

}

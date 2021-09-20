package server.user;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import server.user.auth.CredentialsVerifier;
import server.user.auth.PasswordUtils;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static server.util.FutureUtils.getFuture;

public class UserManager {

    private static final String COLLECTION_USERS = "users";

    private final CredentialsVerifier credentialsVerifier = new CredentialsVerifier();

    private final Firestore db;

    public UserManager(Firestore db) {
        this.db = db;
    }


    //TODO: Need test this codeblock
    private User createNewUser(String login, String password) {
        try {
            ApiFuture<DocumentSnapshot> docRefs = userDocument(login).get();
            DocumentSnapshot docSnapshot = docRefs.get();
            User user = Optional.ofNullable(docSnapshot.toObject(User.class))
                    .orElse(new User(login, PasswordUtils.hash(password), System.currentTimeMillis()));

            save(user);
            return user;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User createUser(String login, String password) throws UserCreationException {
        credentialsVerifier.verifyLogin(login);
        credentialsVerifier.verifyPassword(password);

//        User result = getFuture(db.runTransaction(transaction -> {
//            User currentUser = userDocument(login).get().get().toObject(User.class);
//            if (currentUser != null) {
//                return null;
//            }
//            User newUser = new User(login, PasswordUtils.hash(password), System.currentTimeMillis());
//            save(newUser);
//            return newUser;
//        }));
//
//        if (result == null) {
//            throw new UserCreationException(UserCreationException.Reason.ALREADY_EXIST);
//        }
//        return result;

        return Optional.ofNullable(getFuture(db.runTransaction(transaction -> createNewUser(login, password))))
                .orElseThrow(() -> new UserCreationException(UserCreationException.Reason.ALREADY_EXIST));
    }

    @Nullable
    public User getUser(String login) {
        return getFuture(userDocument(login).get()).toObject(User.class);
    }

    public boolean isPasswordCorrect(User user, String password) {
        return Objects.equals(user.getPasswordHash(), PasswordUtils.hash(password));
    }

    private void save(User user) {
        getFuture(userDocument(user.getLogin()).set(user));
    }

    private DocumentReference userDocument(String login) {
        return db.collection(COLLECTION_USERS).document(login.toLowerCase(Locale.US));
    }

}

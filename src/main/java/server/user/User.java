package server.user;

public class User {
    private final String login;
    private final String passwordHash;
    private final long creationDate;

    // For firestore
    @SuppressWarnings("unused")
    public User() {
        login = null;
        passwordHash = null;
        creationDate = 0L;
    }

    public User(String login, String passwordHash, long creationDate) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.creationDate = creationDate;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public long getCreationDate() {
        return creationDate;
    }
}

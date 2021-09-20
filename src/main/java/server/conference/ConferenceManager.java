package server.conference;

import com.google.cloud.firestore.Firestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConferenceManager {

    private final ConferenceIdGenerator conferenceIdGenerator;
    private final Archive archive;
    private final Map<String, Conference> conferenceMap = new HashMap<>();

    private ConferenceManager(ConferenceIdGenerator conferenceIdGenerator, Archive archive) {
        this.conferenceIdGenerator = conferenceIdGenerator;
        this.archive = archive;
    }

    public synchronized Conference create(String name) {
        String conferenceId = conferenceIdGenerator.next();
        Conference conference = new Conference(conferenceId, name);
        conferenceMap.put(conferenceId, conference);
        return conference;
    }

    public synchronized void finish(final String conferenceId, final String currentUserId) throws PermissionException {
        Conference conference = conferenceMap.get(conferenceId);
        if (conference == null) {
            return;
        }
        if (conference.getHostId().equals(currentUserId)) {
            archive.archive(conference);
            conference.clear();
            conferenceMap.remove(conference.getId());
        } else {
            throw new PermissionException("Only host can finish this conference");
        }
    }

    public synchronized Conference getConference(final String conferenceId) throws NoSuchConferenceException {
        Conference conference = conferenceMap.get(conferenceId);
        if (conference == null) {
            throw new NoSuchConferenceException(conferenceId);
        }
        return conference;
    }

    public synchronized List<Conference> getAllConferences() {
        return new ArrayList<>(conferenceMap.values());
    }

    public synchronized ArchivedConference getArchivedConference(
            final String conferenceId
    ) throws NoSuchConferenceException {
        ArchivedConference conference = archive.get(conferenceId);
        if (conference == null) {
            throw new NoSuchConferenceException(conferenceId);
        }
        return conference;
    }

    public static ConferenceManager createInstance(Firestore firestore) throws IOException {
        return new ConferenceManager(ConferenceIdGenerator.create(firestore), Archive.create());
    }

}

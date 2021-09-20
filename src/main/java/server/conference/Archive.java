package server.conference;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import server.GlobalConfig;
import server.response.conference.ConferenceParticipantTranscript;
import server.util.GoogleCloudUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

class Archive {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Storage storage;
    private final Gson gson = new Gson();

    private Archive(final Storage storage) {
        this.storage = storage;
    }

    void archive(final Conference conference) {
        List<ConferenceParticipantTranscript> finalTranscripts = conference.transcripts
                .stream()
                .filter(t -> t.isFinal)
                .collect(Collectors.toList());
        ArchivedConference archivedConference = new ArchivedConference(
                conference.getId(),
                conference.getName(),
                finalTranscripts
        );
        saveToStorage(archivedConference);
    }

    @Nullable
    public ArchivedConference get(final String conferenceId) {
        try {
            BlobId blobId = BlobId.of(GlobalConfig.ARCHIVED_CONFERENCE_BUCKET_NAME, conferenceId);
            return gson.fromJson(new String(storage.readAllBytes(blobId)), ArchivedConference.class);
        } catch (StorageException e) {
            return null;
        }
    }

    private void saveToStorage(ArchivedConference archivedConference) {
        String json = gson.toJson(archivedConference);

        BlobId blobId = BlobId.of(GlobalConfig.ARCHIVED_CONFERENCE_BUCKET_NAME, archivedConference.getId());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, json.getBytes(DEFAULT_CHARSET));
    }

    public static Archive create() throws IOException {
        final Storage storage = StorageOptions.newBuilder()
                .setProjectId(GoogleCloudUtils.getProjectId())
                .build()
                .getService();
        return new Archive(storage);
    }
}

package server.transcribe.google;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import server.GlobalConfig;
import server.transcribe.TranscribeContent;
import server.transcribe.common.TranscribeMeta;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

class GoogleObjectStorage {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Storage storage;
    private final Gson gson = new Gson();

    public GoogleObjectStorage(String projectId) {
        storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }

    String generateUploadUrl(String name) {
        BlobId blobId = BlobId.of(GlobalConfig.AUDIO_BUCKET_NAME, name);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .build();
        return storage.signUrl(
                blobInfo,
                1, TimeUnit.HOURS,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT)
        ).toString();
    }

    byte[] readAudio(String name) {
        BlobId blobId = BlobId.of(GlobalConfig.AUDIO_BUCKET_NAME, name);
        return storage.readAllBytes(blobId);
    }

    StorageObjectInfo saveAudio(String name, byte[] data) {
        BlobId blobId = BlobId.of(GlobalConfig.AUDIO_BUCKET_NAME, name);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, data);
        return new StorageObjectInfo(blob);
    }

    public TranscribeMeta readMeta(String name) {
        BlobId blobId = BlobId.of(GlobalConfig.META_BUCKET_NAME, name);
        try {
            byte[] data = storage.readAllBytes(blobId);
            return gson.fromJson(new String(data), TranscribeMeta.class);
        } catch (StorageException e) {
            return TranscribeMeta.notStarted();
        }
    }

    void saveMeta(String name, TranscribeMeta meta) {
        BlobId blobId = BlobId.of(GlobalConfig.META_BUCKET_NAME, name);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, gson.toJson(meta).getBytes(DEFAULT_CHARSET));
    }

    public TranscribeContent readResult(String name) {
        BlobId blobId = BlobId.of(GlobalConfig.RESULT_BUCKET_NAME, name);
        return gson.fromJson(new String(storage.readAllBytes(blobId)), TranscribeContent.class);
    }

    void saveResult(String name, TranscribeContent content) {
        BlobId blobId = BlobId.of(GlobalConfig.RESULT_BUCKET_NAME, name);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, gson.toJson(content).getBytes(DEFAULT_CHARSET));
    }
}

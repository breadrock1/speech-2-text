package server.transcribe.yandex;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import server.GlobalConfig;
import server.transcribe.TranscribeContent;
import server.transcribe.common.TranscribeMeta;

import java.io.ByteArrayInputStream;

class YandexObjectStorage {

    private final Gson gson = new Gson();

    public StorageObjectInfo saveAudio(String name, byte[] data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("audio/wav");

        PutObjectRequest request = new PutObjectRequest(
                GlobalConfig.AUDIO_BUCKET_NAME,
                name,
                new ByteArrayInputStream(data),
                metadata
        );
        request.setMetadata(metadata);
        createAmazonClient().putObject(request);

        return new StorageObjectInfo(GlobalConfig.AUDIO_BUCKET_NAME, name);
    }

    public TranscribeMeta readMeta(String name) {
        return gson.fromJson(readFromCloud(GlobalConfig.META_BUCKET_NAME, name), TranscribeMeta.class);
    }

    public void saveMeta(String name, TranscribeMeta meta) {
        saveToCloud(GlobalConfig.META_BUCKET_NAME, name, gson.toJson(meta));
    }

    public TranscribeContent readResult(String name) {
        return gson.fromJson(readFromCloud(GlobalConfig.RESULT_BUCKET_NAME, name), TranscribeContent.class);
    }

    public void saveResult(String name, TranscribeContent content) {
        saveToCloud(GlobalConfig.RESULT_BUCKET_NAME, name, gson.toJson(content));
    }

    private void saveToCloud(String bucket, String name, String content) {
        createAmazonClient().putObject(bucket, name, content);
    }

    private String readFromCloud(String bucket, String name) {
        return createAmazonClient().getObjectAsString(bucket, name);
    }

    private static AmazonS3 createAmazonClient() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return "SwDzbeTjdfALPX7J2ouf";
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return "8jkpPRIoA-l_UoDiHTEWQW0H2pEUiXAtVSJEjBXq";
                    }
                }))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net", "ru-central1"
                        )
                )
                .build();
    }
}

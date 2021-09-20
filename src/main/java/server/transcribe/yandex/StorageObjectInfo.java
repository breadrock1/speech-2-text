package server.transcribe.yandex;

class StorageObjectInfo {

    private static final String STORAGE_URL = "https://storage.yandexcloud.net/%s/%s";

    final String downloadLink;
    final String url;

    public StorageObjectInfo(String bucket, String name) {
        downloadLink = String.format(STORAGE_URL, bucket, name);
        url = downloadLink;
    }
}

package server.transcribe.google;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;

class StorageObjectInfo {

    final String downloadLink;
    final String gsUri;

    StorageObjectInfo(Blob blob) {
        downloadLink = blob.getMediaLink();
        gsUri = toGsUri(blob.getBlobId());
    }

    private static String toGsUri(BlobId blobId) {
        return String.format("gs://%s/%s", blobId.getBucket(), blobId.getName());
    }
}

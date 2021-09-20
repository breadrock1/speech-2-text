package server.transcribe;

class TranscribeId {

    final TranscribeServiceName serviceName;
    final String internalId;

    TranscribeId(TranscribeServiceName serviceName, String internalId) {
        this.serviceName = serviceName;
        this.internalId = internalId;
    }
}

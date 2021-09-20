package server.transcribe;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class TranscribeIdProcessor {

    String encode(TranscribeId id) {
        byte[] internalIdBytes = id.internalId.getBytes(StandardCharsets.UTF_8);
        // 2 - for version, 1 - for service name
        ByteBuffer buffer = ByteBuffer.allocate(internalIdBytes.length + 2 + 1);
        buffer.putShort((short) 1);
        buffer.put(toByte(id.serviceName));
        buffer.put(internalIdBytes);
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    @Nullable
    TranscribeId decode(String id) {
        ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(id));
        short version = buffer.getShort();
        if (version != 1) {
            return null;
        }
        TranscribeServiceName serviceName = toTranscribeServiceName(buffer.get());
        // 2 - for version, 1 - for service name
        byte[] internalIdBytes = new byte[buffer.capacity() - 2 - 1];
        buffer.get(internalIdBytes);
        return new TranscribeId(serviceName, new String(internalIdBytes));
    }

    private static byte toByte(TranscribeServiceName name) {
        switch (name) {
            case GOOGLE:
                return 'g';
            case YANDEX:
                return 'y';
            default:
                throw new IllegalArgumentException("Unsupported service " + name);
        }
    }

    @Nullable
    private static TranscribeServiceName toTranscribeServiceName(byte b) {
        switch (b) {
            case 'g':
                return TranscribeServiceName.GOOGLE;
            case 'y':
                return TranscribeServiceName.YANDEX;
            default:
                return null;
        }
    }

}

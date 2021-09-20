package splitter;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class SplitResultBuilder {
    private final Map<String, ChunkList> chunkMap = new HashMap<>();

    void addChunk(final String channelId, final byte[] chunk) {
        ChunkList list = chunkMap.computeIfAbsent(channelId, id -> new ChunkList());
        list.add(chunk);
    }

    SplitResult build(AudioFileFormat fileFormat) throws IOException {
        Map<String, byte[]> result = new HashMap<>();
        for (Map.Entry<String, ChunkList> entry : chunkMap.entrySet()) {
            byte[] data = entry.getValue().getAllBytes();
            AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), fileFormat.getFormat(), data.length);
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream(data.length);
            AudioSystem.write(ais, fileFormat.getType(), dataStream);
            result.put(entry.getKey(), dataStream.toByteArray());
        }
        return new SplitResult(result);
    }

    private static class ChunkList extends LinkedList<byte[]> {

        int totalByteCount = 0;

        @Override
        public boolean add(byte[] data) {
            totalByteCount += data.length;
            return super.add(data);
        }

        byte[] getAllBytes() {
            byte[] allBytes = new byte[totalByteCount];
            int destPos = 0;
            for (byte[] chunk : this) {
                System.arraycopy(chunk, 0, allBytes, destPos, chunk.length);
                destPos += chunk.length;
            }
            return allBytes;
        }
    }
}

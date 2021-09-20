package splitter;

import java.util.Map;

public class SplitResult {

    private final byte[] empty = new byte[0];

    private final Map<String, byte[]> result;

    SplitResult(Map<String, byte[]> result) {
        this.result = result;
    }

    public byte[] getChannelData(String channelId) {
        return result.getOrDefault(channelId, empty);
    }
}

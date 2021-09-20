package splitter;

public class SplitDescription {

    public String channelId;
    public SplitRange range;

    public SplitDescription(String channelId, SplitRange range) {
        this.channelId = channelId;
        this.range = range;
    }

}

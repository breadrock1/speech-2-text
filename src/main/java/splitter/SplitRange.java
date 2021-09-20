package splitter;

public class SplitRange {

    public final float startSecond;
    public final float endSecond;

    public SplitRange(float startSecond, float endSecond) {
        this.startSecond = startSecond;
        this.endSecond = endSecond;
    }

    public float length() {
        return endSecond - startSecond;
    }
}

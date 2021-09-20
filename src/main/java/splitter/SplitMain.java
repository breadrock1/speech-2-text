package splitter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SplitMain {

    public static void main(String[] args) throws IOException, WavSplitException {
        Path inputPath = Paths.get(args[0]);
        byte[] inputData = Files.readAllBytes(inputPath);

        SplitResult result = new WavSplitter().split(inputData, Arrays.asList(
                new SplitDescription("spk1", new SplitRange(0, 3.0f)),
                new SplitDescription("spk2", new SplitRange(3.0f, 7f))
        ));

        Files.write(Paths.get(inputPath.getParent().toString(), "spk1.wav"), result.getChannelData("spk1"));
        Files.write(Paths.get(inputPath.getParent().toString(), "spk2.wav"), result.getChannelData("spk2"));
    }

}

package wav;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WavProcessorMain {

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        Path inputPath = Paths.get(args[0]);
        byte[] stereoData = Files.readAllBytes(inputPath);
        byte[] monoData = new WavProcessor().stereoToMono(stereoData);

        String outputFilename = inputPath.getFileName().toString().replace(".wav", "") + "-mono.wav";
        Files.write(Paths.get(inputPath.getParent().toString(), outputFilename), monoData);
    }
}

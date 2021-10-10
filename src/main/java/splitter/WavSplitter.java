package splitter;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class WavSplitter {

    public SplitResult split(byte[] data, List<SplitDescription> descriptions) throws WavSplitException {
        try {
            return splitInternal(data, descriptions);
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new WavSplitException(e);
        }
    }

    private SplitResult splitInternal(
            final byte[] data,
            final List<SplitDescription> descriptions
    ) throws IOException, UnsupportedAudioFileException {
        final ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
        final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(dataStream);
        dataStream.reset();
        final AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(dataStream);
        final AudioFormat format = fileFormat.getFormat();
        final int bytesPerSecond = format.getFrameSize() * (int) format.getFrameRate();

        final SplitResultBuilder resultBuilder = new SplitResultBuilder();

        //TODO: Optimized??
        for (SplitDescription description : descriptions) {
            int toSkip = roundToFrameSize(bytesPerSecond * description.range.startSecond, format.getFrameSize());
            //noinspection ResultOfMethodCallIgnored
            audioInputStream.skip(toSkip);
            int toCopy = roundToFrameSize(description.range.length() * bytesPerSecond, format.getFrameSize());
            byte[] buffer = new byte[toCopy];
            int readCount = audioInputStream.read(buffer);

            if (readCount > 0) {
                byte[] chunk;
                if (readCount == buffer.length) {
                    chunk = buffer;
                } else {
                    chunk = new byte[readCount];
                    System.arraycopy(buffer, 0, chunk, 0, readCount);
                }

                resultBuilder.addChunk(description.channelId, chunk);
            }
            audioInputStream.reset();
        }

        return resultBuilder.build(fileFormat);
    }


    private int roundToFrameSize(float value, int frameSize) {
        int n = Math.round(value);
        if ((n % frameSize) != 0) {
            n -= (n % frameSize);
        }
        return n;
    }

}

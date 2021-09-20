package wav;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WavProcessor {
    public byte[] stereoToMono(byte[] data) throws IOException, UnsupportedAudioFileException {
        final ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
        final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(dataStream);
        final AudioFormat sourceFormat = audioInputStream.getFormat();
        if (sourceFormat.getChannels() == 1) {
            return data;
        }


        final AudioFormat monoFormat = toMonoFormat(sourceFormat);
        try (AudioInputStream monoStream = AudioSystem.getAudioInputStream(monoFormat, audioInputStream)) {
            try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                AudioSystem.write(monoStream, AudioFileFormat.Type.WAVE, baos);
                return baos.toByteArray();
            }
        }
    }

    private AudioFormat toMonoFormat(AudioFormat sourceFormat) {
        return new AudioFormat(
                sourceFormat.getEncoding(),
                sourceFormat.getSampleRate(),
                sourceFormat.getSampleSizeInBits(),
                1,
                sourceFormat.getFrameSize() / 2,
                sourceFormat.getFrameRate(),
                sourceFormat.isBigEndian(),
                sourceFormat.properties()
        );
    }
}

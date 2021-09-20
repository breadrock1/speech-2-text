package demo;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeakerDiarizationConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.WordInfo;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MultiVoiceRecognize {

    public static void main(String[] args) throws IOException {
        transcribeDiarization(args[0]);
    }
    /**
     * Transcribe the given audio file using speaker diarization.
     *
     * @param fileName the path to an audio file.
     */
    private static void transcribeDiarization(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        byte[] content = Files.readAllBytes(path);

        try (SpeechClient speechClient = SpeechClient.create()) {
            // Get the contents of the local audio file
            RecognitionAudio recognitionAudio =
                    RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(content)).build();

            SpeakerDiarizationConfig speakerDiarizationConfig =
                    SpeakerDiarizationConfig.newBuilder()
                            .setEnableSpeakerDiarization(true)
                            .setMinSpeakerCount(2)
                            .setMaxSpeakerCount(2)
                            .build();

            // Configure request to enable Speaker diarization
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setLanguageCode("ru-RU")
                            .setSampleRateHertz(44100)
                            .setDiarizationConfig(speakerDiarizationConfig)
                            .build();

            // Perform the transcription request
            RecognizeResponse recognizeResponse = speechClient.recognize(config, recognitionAudio);

            // Speaker Tags are only included in the last result object, which has only one alternative.
            SpeechRecognitionAlternative alternative =
                    recognizeResponse.getResults(recognizeResponse.getResultsCount() - 1).getAlternatives(0);

            // The alternative is made up of WordInfo objects that contain the speaker_tag.
            WordInfo wordInfo = alternative.getWords(0);
            int currentSpeakerTag = wordInfo.getSpeakerTag();

            // For each word, get all the words associated with one speaker, once the speaker changes,
            // add a new line with the new speaker and their spoken words.
            StringBuilder speakerWords =
                    new StringBuilder(
                            String.format("Speaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));

            for (int i = 1; i < alternative.getWordsCount(); i++) {
                wordInfo = alternative.getWords(i);
                if (currentSpeakerTag == wordInfo.getSpeakerTag()) {
                    speakerWords.append(" ");
                    speakerWords.append(wordInfo.getWord());
                } else {
                    speakerWords.append(
                            String.format("\nSpeaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));
                    currentSpeakerTag = wordInfo.getSpeakerTag();
                }
            }

            System.out.println(speakerWords.toString());
        }
    }
}

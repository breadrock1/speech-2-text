package server.transcribe.google;

import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.WordInfo;
import server.transcribe.TranscribeContent;
import server.transcribe.TranscriptionConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ResultConverter {

    TranscribeContent convert(TranscriptionConfig config, List<SpeechRecognitionResult> recognitionResults) {
        if (config.isDiarizationEnabled()) {
            // Speaker Tags are only included in the last result object, which has only one alternative.
            SpeechRecognitionAlternative alternative = recognitionResults.get(recognitionResults.size() - 1)
                    .getAlternatives(0);

            Entry currentEntry = null;
            List<Entry> entries = new ArrayList<>();

            for (int i = 0; i < alternative.getWordsCount(); i++) {
                WordInfo wordInfo = alternative.getWords(i);
                if (currentEntry == null || currentEntry.speakerTag != wordInfo.getSpeakerTag()) {
                    currentEntry = new Entry();
                    currentEntry.speakerTag = wordInfo.getSpeakerTag();
                    currentEntry.startTime = wordInfo.getStartTime().getSeconds();
                    entries.add(currentEntry);
                }
                Objects.requireNonNull(currentEntry).appendWord(wordInfo.getWord());
            }

            List<TranscribeContent.Entry> contentEntries = new ArrayList<>(entries.size());
            for (Entry entry : entries) {
                contentEntries.add(entry.toContentEntry());
            }
            return TranscribeContent.withEntries(contentEntries);
        } else {
            List<TranscribeContent.Entry> contentEntries = new ArrayList<>(recognitionResults.size());
            for (SpeechRecognitionResult result : recognitionResults) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                long startTime = alternative.getWords(0).getStartTime().getSeconds();
                contentEntries.add(
                        new TranscribeContent.Entry(
                                startTime,
                                -1,
                                alternative.getTranscript().trim()
                        )
                );
            }
            return TranscribeContent.withEntries(contentEntries);
        }
    }

    private static class Entry {
        private long startTime;
        private int speakerTag;
        private final StringBuilder text = new StringBuilder();

        void appendWord(String word) {
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(word);
        }

        TranscribeContent.Entry toContentEntry() {
            return new TranscribeContent.Entry(startTime, speakerTag, text.toString());
        }
    }

}

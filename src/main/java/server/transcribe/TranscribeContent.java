package server.transcribe;

import server.handler.validator.Required;
import server.handler.validator.RequirementType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TranscribeContent {

    public static final TranscribeContent EMPTY = new TranscribeContent(Collections.emptyList(), null);

    @Nullable
    @Required(type = RequirementType.NOT_NULL_NOT_EMPTY)
    public final List<Entry> entries;

    @Nullable
    public final String errorText;

    private TranscribeContent(@Nullable List<Entry> entries, @Nullable String errorText) {
        this.entries = entries;
        this.errorText = errorText;
    }

    public static class Entry {
        @Required
        public final Long time;

        @Required
        public final Integer speakerId;

        @Required(type = RequirementType.NOT_NULL_NOT_EMPTY)
        public final String speakerTag;

        public final String text;

        public Entry(long time, int speakerId, String text) {
            this.time = time;
            this.speakerId = speakerId;
            this.speakerTag = String.valueOf(speakerId);
            this.text = text;
        }
    }

    public static TranscribeContent withEntries(List<Entry> entries) {
        return new TranscribeContent(entries, null);
    }

    public static TranscribeContent error(String message) {
        return new TranscribeContent(null, message);
    }

}

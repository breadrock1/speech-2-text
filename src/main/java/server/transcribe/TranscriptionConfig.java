package server.transcribe;

import javax.annotation.Nullable;

public class TranscriptionConfig {

    private final String service;
    private final boolean diarizationEnabled;
    private final int minSpeakerCount;
    private final int maxSpeakerCount;
    private final String audioFormat;

    @Nullable
    private final String model;

    private TranscriptionConfig(Builder builder) {
        service = builder.service;
        diarizationEnabled = builder.diarizationEnabled;
        minSpeakerCount = builder.minSpeakerCount;
        maxSpeakerCount = builder.maxSpeakerCount;
        model = builder.model;
        audioFormat = builder.audioFormat;
    }

    public String getService() {
        return service;
    }

    public boolean isDiarizationEnabled() {
        return diarizationEnabled;
    }

    public int getMinSpeakerCount() {
        return minSpeakerCount;
    }

    public int getMaxSpeakerCount() {
        return maxSpeakerCount;
    }

    public String getAudioFormat() {
        return audioFormat;
    }

    @Nullable
    public String getModel() {
        return model;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {
        private String service;
        private boolean diarizationEnabled = false;
        private int minSpeakerCount = 0;
        private int maxSpeakerCount = 0;
        private String audioFormat = "";

        @Nullable
        private String model;

        public Builder service(String service) {
            this.service = service;
            return this;
        }

        public Builder diarizationEnabled(boolean diarizationEnabled) {
            this.diarizationEnabled = diarizationEnabled;
            return this;
        }

        public Builder minSpeakerCount(int minSpeakerCount) {
            this.minSpeakerCount = minSpeakerCount;
            return this;
        }

        public Builder maxSpeakerCount(int maxSpeakerCount) {
            this.maxSpeakerCount = maxSpeakerCount;
            return this;
        }

        public Builder model(@Nullable String model) {
            this.model = model;
            return this;
        }

        public Builder audioFormat(String audioFormat) {
            this.audioFormat = audioFormat;
            return this;
        }

        public TranscriptionConfig build() {
            if (service == null) {
                throw new IllegalArgumentException("service is required");
            }
            return new TranscriptionConfig(this);
        }
    }
}

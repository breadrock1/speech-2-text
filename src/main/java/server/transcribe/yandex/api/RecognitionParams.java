package server.transcribe.yandex.api;

@SuppressWarnings("unused")
public class RecognitionParams {

    public final Config config = new Config();

    public final Audio audio = new Audio();

    public static RecognitionParams withUri(String uri) {
        RecognitionParams params = new RecognitionParams();
        params.audio.uri = uri;
        return params;
    }

    public static class Config {
        public Specification specification = new Specification();
    }

    public static class Specification {
        /**
         * The language that recognition will be performed for.
         * Only Russian is currently supported (ru-RU).
         */
        public String languageCode = "ru-RU";

        /**
         * The profanity filter.
         * Acceptable values:
         * <ul>
         *      <li>true — Exclude profanity from recognition results</li>
         *      <li>false (by default) — Do not exclude profanity from recognition results</li>
         * </ul>
         */
        public boolean profanityFilter = true;

        /**
         * The format of the submitted audio.
         * Acceptable values:
         * <ul>
         *      <li>LINEAR16_PCM — LPCM with no WAV header</li>
         *      <li>OGG_OPUS (default) — OggOpus format</li>
         * </ul>
         */
        public String audioEncoding = "LINEAR16_PCM";

        /**
         * The sampling frequency of the submitted audio.
         * Required if format is set to LINEAR16_PCM. Acceptable values:
         * <ul>
         *      <li>48000 (default) — Sampling rate of 48 kHz</li>
         *      <li>16000 — Sampling rate of 16 kHz</li>
         *      <li>8000 — Sampling rate of 8 kHz</li>
         * </ul>
         */
        public int sampleRateHertz = 48000;

        /**
         * The number of channels in LPCM files. By default, 1.
         * Don't use this field for OggOpus files.
         */
        public int audioChannelCount = 1;
    }

    public static class Audio {
        /**
         * The URI of the audio file for recognition. Supports only links to files stored in Yandex Object Storage.
         */
        public String uri;
    }
}

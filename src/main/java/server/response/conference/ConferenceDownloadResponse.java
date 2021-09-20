package server.response.conference;

import server.response.GenericResponse;

import java.util.List;

public class ConferenceDownloadResponse extends GenericResponse {

    private final String text;
    private final String conferenceName;
    private final List<Entry> entries;

    public ConferenceDownloadResponse(
            final String text,
            final String conferenceName,
            final List<Entry> entries
    ) {
        this.text = text;
        this.conferenceName = conferenceName;
        this.entries = entries;
    }

    public static class Entry {
        private final String participantName;
        private final String text;

        public Entry(String participantName, String text) {
            this.participantName = participantName;
            this.text = text;
        }
    }
}

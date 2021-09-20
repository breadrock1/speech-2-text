package server.response.conference;

import doc.annotation.Description;
import server.response.GenericResponse;

public class ConferenceJoinResponse extends GenericResponse {
    public final String conferenceId;
    public final String conferenceName;

    @Description("deprecated")
    public final String hostId = "deprecated";

    @Description("deprecated")
    public final String participantId = "deprecated";
    public final String participantName;

    private ConferenceJoinResponse(final Builder builder) {
        this.conferenceId = builder.conferenceId;
        this.conferenceName = builder.conferenceName;
        this.participantName = builder.participantName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String conferenceId;
        private String conferenceName;
        private String participantName;

        public Builder conferenceId(final String conferenceId) {
            this.conferenceId = conferenceId;
            return this;
        }

        public Builder conferenceName(final String conferenceName) {
            this.conferenceName = conferenceName;
            return this;
        }

        public Builder participantName(final String participantName) {
            this.participantName = participantName;
            return this;
        }

        public ConferenceJoinResponse build() {
            return new ConferenceJoinResponse(this);
        }
    }
}


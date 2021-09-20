package server.response.conference;

import doc.annotation.Description;
import server.response.GenericResponse;

public class ConferenceCreateResponse extends GenericResponse {
    public final String conferenceId;
    public final String conferenceName;

    @Description("deprecated")
    public final String hostId = "deprecated";
    public final String hostName;

    private ConferenceCreateResponse(final Builder builder) {
        conferenceId = builder.conferenceId;
        conferenceName = builder.conferenceName;
        hostName = builder.hostName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String conferenceId;
        private String conferenceName;
        private String hostName;

        public Builder conferenceId(final String conferenceId) {
            this.conferenceId = conferenceId;
            return this;
        }

        public Builder conferenceName(final String conferenceName) {
            this.conferenceName = conferenceName;
            return this;
        }

        public Builder hostName(final String hostName) {
            this.hostName = hostName;
            return this;
        }

        public ConferenceCreateResponse build() {
            return new ConferenceCreateResponse(this);
        }

    }
}

package server.response.conference;

import doc.annotation.Description;
import server.response.transcribe.TranscribeResult;

public class ConferenceParticipantTranscript {

    public final String participantId;

    public final String participantName;

    @Description("true - результат финальный, false - промежуточный результат, который еще уточняется")
    public final boolean isFinal;

    @Description("Реплика участника")
    public final String value;

    @Description("Уверенность в результате от 0 до 1")
    public final float confidence;

    public ConferenceParticipantTranscript(
            final String participantId,
            final String participantName,
            final TranscribeResult result
    ) {
        this.participantId = participantId;
        this.participantName = participantName;
        this.isFinal = result.isFinal;
        this.value = result.transcript;
        this.confidence = result.confidence;
    }

}

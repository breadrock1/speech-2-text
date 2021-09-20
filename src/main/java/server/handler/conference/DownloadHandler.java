package server.handler.conference;

import doc.annotation.Description;
import doc.annotation.Responses;
import server.conference.ArchivedConference;
import server.conference.ConferenceManager;
import server.conference.ConferencePrinter;
import server.conference.NoSuchConferenceException;
import server.handler.HandlerException;
import server.http.annotation.HandleGet;
import server.http.annotation.Query;
import server.http.annotation.SummaryHttpHandler;
import server.localization.CurrentLocale;
import server.localization.StringId;
import server.logging.Logger;
import server.response.conference.ConferenceDownloadResponse;
import server.response.conference.ConferenceParticipantTranscript;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static server.response.ErrorResponse.errorResponse;
import static server.util.StringUtils.orEmpty;

@SummaryHttpHandler(path = "/1/conference")
public class DownloadHandler {

    private static final String FORMAT_PLAIN_TEXT = "plaintext";
    private static final String FORMAT_JSON = "json";

    private static final String FORMAT_DOCUMENTATION = FORMAT_JSON + " или " + FORMAT_PLAIN_TEXT;

    @Inject
    ConferenceManager conferenceManager;

    @Inject
    Logger logger;

    @CurrentLocale
    Locale locale;

    @Description("Скачавание расшифровки конференции")
    @Responses(
            responses = {
                    ConferenceDownloadResponse.class,
                    String.class
            },
            explanations = {
                    "если format=" + FORMAT_JSON,
                    "если format=" + FORMAT_PLAIN_TEXT,
            }
    )
    @HandleGet("/download")
    Object download(
            @Query("conference_id") String conferenceId,
            @Query("format") @Description(FORMAT_DOCUMENTATION) String format
    ) throws HandlerException {
        logger.info("Handle conference download; conference_id=%s; format=%s", conferenceId, format);
        try {
            ArchivedConference conference = conferenceManager.getArchivedConference(conferenceId);
            String content = new ConferencePrinter().printAsString(conference);
            switch (orEmpty(format)) {
                case FORMAT_JSON:
                    return createJsonResponse(content, conference);
                case FORMAT_PLAIN_TEXT:
                default:
                    return content;
            }
        } catch (NoSuchConferenceException e) {
            throw new HandlerException(200, errorResponse(StringId.CONFERENCE_NOT_FINISHED, locale));
        }
    }

    private ConferenceDownloadResponse createJsonResponse(String content, ArchivedConference conference) {
        List<ConferenceDownloadResponse.Entry> entries = new ArrayList<>(conference.getTranscripts().size());
        for (ConferenceParticipantTranscript transcript : conference.getTranscripts()) {
            entries.add(new ConferenceDownloadResponse.Entry(transcript.participantName, transcript.value));
        }
        return new ConferenceDownloadResponse(content, conference.getName(), entries);
    }
}

package server.handler.conference;

import doc.annotation.Description;
import doc.annotation.Responses;
import server.conference.Conference;
import server.conference.ConferenceManager;
import server.conference.NoSuchConferenceException;
import server.conference.NoSuchParticipantException;
import server.conference.Participant;
import server.conference.PermissionException;
import server.handler.HandlerException;
import server.http.annotation.Body;
import server.http.annotation.HandleGet;
import server.http.annotation.HandlePost;
import server.http.annotation.Query;
import server.http.annotation.SummaryHttpHandler;
import server.localization.CurrentLocale;
import server.localization.StringId;
import server.logging.Logger;
import server.response.ErrorResponse;
import server.response.GenericResponse;
import server.response.conference.ConferenceChunkResponse;
import server.response.conference.ConferenceCreateResponse;
import server.response.conference.ConferenceJoinResponse;
import server.response.conference.ConferenceParticipant;
import server.response.conference.ConferenceParticipantsResponse;
import server.user.User;
import server.user.auth.AuthorizedUser;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static server.response.ErrorResponse.errorResponse;

@SummaryHttpHandler(path = "/1/conference")
public class ConferenceHandler {

    @Inject
    ConferenceManager conferenceManager;

    @Inject
    Logger logger;

    @AuthorizedUser
    User user;

    @CurrentLocale
    Locale locale;

    @Description("Отправка порции аудио участника")
    @HandlePost("/chunk")
    ConferenceChunkResponse chunk(
            @Query("conference_id") String conferenceId,
            @Query(value = "include_participants", optional = true) boolean includeParticipants,
            @Body byte[] body
    ) throws IOException, HandlerException {
        logger.info(
                "Handle transcribe chunk; participant=%s; conference_id=%s; include_participants=%s, body.length=%d",
                user.getLogin(), conferenceId, includeParticipants, body.length
        );
        try {
            Conference conference = conferenceManager.getConference(conferenceId);
            Participant participant = conference.getSpeaker(user.getLogin());
            participant.append(body);
            return new ConferenceChunkResponse(
                    conference.flushTranscriptsForParticipant(participant),
                    includeParticipants ? mapParticipants(conference.getAllSpeakers(Participant.State.ACTIVE)) : null
            );
        } catch (NoSuchConferenceException e) {
            throw new HandlerException(200, errorResponse(StringId.CONFERENCE_NOT_EXIST, locale));
        } catch (NoSuchParticipantException e) {
            throw new HandlerException(200, errorResponse(StringId.CONFERENCE_PARTICIPANT_NOT_EXIST, locale));
        }
    }

    @Description("Создание конференции")
    @HandlePost("/create")
    ConferenceCreateResponse create(
            @Query("conference_name") String conferenceName,
            @Query("host_name") String hostName
    ) {
        logger.info(
                "Handle conference create; host=%s; conference_name=%s; host_name=%s",
                user.getLogin(), conferenceName, hostName
        );
        Conference conference = conferenceManager.create(conferenceName);
        conference.setHost(user.getLogin(), hostName);
        return ConferenceCreateResponse.builder()
                .conferenceId(conference.getId())
                .conferenceName(conference.getName())
                .hostName(hostName)
                .build();
    }

    @Description("Завершение конференции. Только организатор может завершить конференцию.")
    @Responses(
            responses = {GenericResponse.class, ErrorResponse.class},
            explanations = {"Если конференция успешно удалена", "В случае ошибки"}
    )
    @HandlePost("/finish")
    GenericResponse finish(
            @Query("conference_id") String conferenceId
    ) throws HandlerException {
        logger.info("Handle conference finish; participant=%s; conference_id=%s", user.getLogin(), conferenceId);
        try {
            conferenceManager.finish(conferenceId, user.getLogin());
            return new GenericResponse();
        } catch (PermissionException e) {
            throw new HandlerException(403, errorResponse(StringId.CONFERENCE_FINISH_NOT_PERMITTED, locale));
        }
    }

    @Description("Подключение участника к конференции")
    @HandlePost("/join_speaker")
    ConferenceJoinResponse join(
            @Query("conference_id") String conferenceId,
            @Query("speaker_name") String speakerName
    ) throws HandlerException {
        logger.info(
                "Handle conference join; participant=%s; conference_id=%s; speaker_name=%s",
                user.getLogin(), conferenceId, speakerName
        );
        try {
            Conference conference = conferenceManager.getConference(conferenceId);
            conference.addSpeaker(user.getLogin(), speakerName);
            return ConferenceJoinResponse.builder()
                    .conferenceId(conferenceId)
                    .conferenceName(conference.getName())
                    .participantName(speakerName)
                    .build();
        } catch (NoSuchConferenceException e) {
            throw new HandlerException(200, errorResponse(StringId.CONFERENCE_NOT_EXIST, locale));
        }
    }

    @Description("Получение списка участников")
    @HandleGet("/get_participants")
    ConferenceParticipantsResponse getParticipants(
            @Query("conference_id") String conferenceId
    ) throws HandlerException {
        logger.info("Handle get conference participants; participant=%s; conference_id=%s", user.getLogin(), conferenceId);
        try {
            Conference conference = conferenceManager.getConference(conferenceId);
            return new ConferenceParticipantsResponse(
                    mapParticipants(conference.getAllSpeakers(Participant.State.ACTIVE))
            );
        } catch (NoSuchConferenceException e) {
            throw new HandlerException(200, errorResponse(StringId.CONFERENCE_NOT_EXIST, locale));
        }
    }

    private static List<ConferenceParticipant> mapParticipants(List<Participant> participants) {
        return participants.stream()
                .map(participant -> new ConferenceParticipant(participant.getName()))
                .sorted(Comparator.comparing(o -> o.name))
                .collect(Collectors.toList());
    }

}

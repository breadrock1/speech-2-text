package client;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import server.response.conference.ConferenceChunkResponse;
import server.response.conference.ConferenceCreateResponse;
import server.response.conference.ConferenceJoinResponse;
import server.response.GenericResponse;

public interface ConferenceService {

    String AUTHORIZATION = "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImludDAyaCJ9.rj6ba-b1YxhHbdFOysiyF3lJWKAbUaRDuGtSzUTTqW4";

    @POST("1/conference/create")
    @Headers(AUTHORIZATION)
    Call<ConferenceCreateResponse> create(
            @Query("conference_name") String conferenceName,
            @Query("host_name") String hostName
    );

    @POST("1/conference/join_speaker")
    @Headers(AUTHORIZATION)
    Call<ConferenceJoinResponse> joinSpeaker(
            @Query("conference_id") String conferenceId,
            @Query("speaker_name") String name
    );

    @POST("1/conference/chunk")
    @Headers(AUTHORIZATION)
    Call<ConferenceChunkResponse> chunk(
            @Query("conference_id") String conferenceId,
            @Query("participant_id") String participantId,
            @Body RequestBody data
    );

    @POST("1/conference/finish")
    @Headers(AUTHORIZATION)
    Call<GenericResponse> finish(
            @Query("conference_id") String conferenceId
    );

}

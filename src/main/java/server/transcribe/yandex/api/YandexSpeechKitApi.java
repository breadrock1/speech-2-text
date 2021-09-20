package server.transcribe.yandex.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface YandexSpeechKitApi {

    @POST("speech/stt/v2/longRunningRecognize")
    Call<LongRunningRecognizeResponse> longRunningRecognize(@Body RecognitionParams params);

}

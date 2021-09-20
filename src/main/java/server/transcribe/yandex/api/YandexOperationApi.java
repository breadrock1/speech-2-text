package server.transcribe.yandex.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface YandexOperationApi {

    @GET("operations/{operationId}")
    Call<GetResultResponse> getResult(@Path("operationId") String operationId);

}

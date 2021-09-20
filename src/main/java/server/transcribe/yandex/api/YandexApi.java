package server.transcribe.yandex.api;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

public class YandexApi {

    public final YandexOperationApi operationApi;

    public final YandexSpeechKitApi speechKitApi;

    private YandexApi(YandexOperationApi operationApi, YandexSpeechKitApi speechKitApi) {
        this.operationApi = operationApi;
        this.speechKitApi = speechKitApi;
    }

    public static YandexApi create() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthorizationHeaderInterceptor())
                .build();

        YandexOperationApi operationApi = new Retrofit.Builder()
                .baseUrl("https://operation.api.cloud.yandex.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(YandexOperationApi.class);

        YandexSpeechKitApi speechKitApi = new Retrofit.Builder()
                .baseUrl("https://transcribe.api.cloud.yandex.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(YandexSpeechKitApi.class);

        return new YandexApi(operationApi, speechKitApi);
    }

    private static class AuthorizationHeaderInterceptor implements Interceptor {

        @Override
        @Nonnull
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.header("Authorization") != null) {
                return chain.proceed(originalRequest);
            }

            Request authorizedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Api-Key AQVN3-DixE-0v3JyXn6U2jHw_TsgTAU1zTE1HHYg")
                    .build();
            return chain.proceed(authorizedRequest);
        }

    }
}

package com.example.gyatsina.firstapp.network;

import com.example.gyatsina.firstapp.BuildConfig;
import com.example.gyatsina.firstapp.logger.DebugLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by gyatsina on 1/26/2018.
 */

@ApiScope
public class RestClientProvider {

    private static final String TAG = RestClientProvider.class.getSimpleName();

    private String mBaseUrl;

    @Inject
    public RestClientProvider() {
        // empty

    }

    /**
     * @param baseUrl The url for API. For example, "https://some.cool.api/v1/"
     *
     *  Note that if the whole url is "https://some.cool.api/v1/banks?api=123&country=Zanzibar"
     *                the base url is "https://some.cool.api/v1/" (without "banks").
     *                We put "banks" in another place.
     */
    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    /**
     * This is our DAO to communicate with server.
     */
    public Retrofit getRetrofit() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                return response;
            }
        });
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addNetworkInterceptor(getHttpLoggingInterceptor());
        }
        return okHttpClientBuilder.build();
    }

    /**
     * We use it for logging what the requests and responses are.
     */
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                DebugLogger.d(TAG, message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);
    }
}

package com.example.gyatsina.firstapp.network;

import com.example.gyatsina.firstapp.BuildConfig;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gyatsina on 1/26/2018.
 */

public class BasicAuthInterceptor implements Interceptor {

    private String credentials;

    public BasicAuthInterceptor() {
        this.credentials = Credentials.basic(BuildConfig.TOSH_APP_LOGIN, BuildConfig.TOSH_APP_PASS);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }

}



package com.example.gyatsina.firstapp;

import android.app.Application;

import com.example.gyatsina.firstapp.logger.DebugLogger;
import com.example.gyatsina.firstapp.network.ApiComponent;
import com.example.gyatsina.firstapp.network.DaggerApiComponent;
import com.example.gyatsina.firstapp.network.RestClientProvider;
import com.example.gyatsina.firstapp.network.StampApi;

/**
 * Created by gyatsina on 1/26/2018.
 */

public class StampApplication extends Application {

    private final static String API_URL = "http://lots.inspinia.co.ua/";
    private StampApi mStampApi;

    @Override
    public void onCreate() {
        super.onCreate();

        initLogging();
        ApiComponent apiComponent = DaggerApiComponent.builder().build();
        setRestClientBaseUrl(apiComponent.getRestClient());

        mStampApi = apiComponent.getStamplApi();
    }

    private void initLogging() {
        if (BuildConfig.DEBUG) {
            DebugLogger.enableLogging();
        }
    }

    private void setRestClientBaseUrl(RestClientProvider restClientProvider) {
        restClientProvider.setBaseUrl(API_URL);
    }

    public StampApi getStampApi() {
        return mStampApi;
    }
}

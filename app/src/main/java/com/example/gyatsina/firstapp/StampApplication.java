package com.example.gyatsina.firstapp;

import android.app.Application;

import com.example.gyatsina.firstapp.logger.DebugLogger;
import com.example.gyatsina.firstapp.network.ApiComponent;
import com.example.gyatsina.firstapp.network.RestClientProvider;

/**
 * Created by gyatsina on 1/26/2018.
 */

public class StampApplication extends Application {

    private final static String API_URL = "http://lots.inspinia.co.ua/";

    @Override
    public void onCreate() {
        initLogging();
        ApiComponent apiComponent = DaggerFilialApiComponent.builder().build();
        setRestClientBaseUrl(apiComponent.getRestClient());
    }

    private void initLogging() {
        if (BuildConfig.DEBUG) {
            DebugLogger.enableLogging();
        }
    }

    private void setRestClientBaseUrl(RestClientProvider restClientProvider) {
        restClientProvider.setBaseUrl(API_URL);


    }
}

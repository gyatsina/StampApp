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
    private static AppComponent appComponent;

    private final static String API_URL = "http://lots.inspinia.co.ua/";
    private StampApi mStampApi;

    @Override
    public void onCreate() {
        super.onCreate();

        initLogging();
//        appComponent = buildComponent();
        ApiComponent apiComponent = DaggerApiComponent.builder().build();
        setRestClientBaseUrl(apiComponent.getRestClient());

        mStampApi = apiComponent.getStamplApi();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
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

//    protected AppComponent buildComponent() {
//        return DaggerAppComponent.builder()
//                .appModule(new AppModule(this))
//                .build();
//    }
}

package com.example.gyatsina.firstapp;

import android.content.Context;

import io.reactivex.annotations.NonNull;

/**
 * Created by gyatsina on 3/7/2018.
 */
//@Module
public class AppModule {

    private Context appContext;

    public AppModule(@NonNull Context context){
        appContext = context;
    }

//    @Provides
//    @Singleton
    Context provideContext(){
        return appContext;
    }
}

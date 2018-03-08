package com.example.gyatsina.firstapp;

import com.example.gyatsina.firstapp.network.StampObj;

/**
 * Created by gyatsina on 3/7/2018.
 */

//@Singleton
//@Component (modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
    void inject(StampObj stampObj);
}

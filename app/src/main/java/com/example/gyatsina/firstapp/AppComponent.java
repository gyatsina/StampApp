package com.example.gyatsina.firstapp;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by gyatsina on 3/7/2018.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(GridAdapter gridAdapter);
}

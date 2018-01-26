package com.example.gyatsina.firstapp.network;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by gyatsina on 1/26/2018.
 */

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {
    RestClientProvider getRestClient();

    StampApi getStamplApi();
}

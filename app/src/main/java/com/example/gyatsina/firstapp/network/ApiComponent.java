package com.example.gyatsina.firstapp.network;

import dagger.Component;

/**
 * Created by gyatsina on 1/26/2018.
 */

@ApiScope
@Component(modules = {ApiModule.class})
public interface ApiComponent {
    RestClientProvider getRestClient();

    StampApi getStamplApi();
}

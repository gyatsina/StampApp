package com.example.gyatsina.firstapp.network;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gyatsina on 1/26/2018.
 */

@Module
public class ApiModule {
    @Provides
    @ApiScope
    public ServerDAO provideServerDAO(RestClientProvider restClientProvider) {
        return restClientProvider.getRetrofit().create(ServerDAO.class);
    }
}

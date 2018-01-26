package com.example.gyatsina.firstapp.network;

import javax.inject.Inject;

/**
 * Created by gyatsina on 1/26/2018.
 */

public class StampApi {

    private ServerDAO mServerDAO;

    @Inject
    public StampApi(ServerDAO serverDAO) {
        mServerDAO = serverDAO;
    }

    public ObserverState login(){

    }
}

package com.example.gyatsina.firstapp.network.events;

/**
 * Created by gyatsina on 1/30/2018.
 */

public class LoginEvent {

    public final static int SUCCESS = 200;

    private int responseCode;

    public LoginEvent(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}

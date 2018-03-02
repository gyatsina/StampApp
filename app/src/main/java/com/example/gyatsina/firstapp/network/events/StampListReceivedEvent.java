package com.example.gyatsina.firstapp.network.events;

import com.example.gyatsina.firstapp.network.StampObj;

import java.util.List;

import io.reactivex.annotations.NonNull;

/**
 * Created by gyatsina on 1/30/2018.
 */

public class StampListReceivedEvent {

    private List<StampObj> stampObjects;

    public StampListReceivedEvent(List<StampObj> stampObjs) {
        this.stampObjects = stampObjs;
    }

    @NonNull
    public List<StampObj> getStampObjects() {
        return stampObjects;
    }

    public void setStampObjects(List<StampObj> stampObjects) {
        this.stampObjects = stampObjects;
    }
}

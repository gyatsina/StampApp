package com.example.gyatsina.firstapp.network;

import com.example.gyatsina.firstapp.AppModule;

import javax.inject.Inject;

/**
 * Created by gyatsina on 2/7/2018.
 */

public class StampObj {
    @Inject
    AppModule appModule;

    private String id;
    private String title;

    public StampObj() {
//        StampApplication.getAppComponent().inject(this);
    }

    private String image;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public AppModule getContext(){
        return appModule;
    }
}

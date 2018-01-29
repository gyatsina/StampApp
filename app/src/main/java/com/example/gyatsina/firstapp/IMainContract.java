package com.example.gyatsina.firstapp;

import com.example.gyatsina.firstapp.network.StampApi;

/**
 * Created by gyatsina on 1/23/2018.
 */

public interface IMainContract {

    interface MainView{
        void checkCameraPermissions();
        void takePicture();
        void openGallery();
    }

    interface MainPresenter{
        void apiLogin(StampApi api);
    }
}

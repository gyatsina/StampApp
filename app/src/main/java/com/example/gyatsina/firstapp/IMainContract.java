package com.example.gyatsina.firstapp;

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

    }
}

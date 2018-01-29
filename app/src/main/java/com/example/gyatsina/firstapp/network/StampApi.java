package com.example.gyatsina.firstapp.network;

import com.example.gyatsina.firstapp.BuildConfig;
import com.example.gyatsina.firstapp.logger.DebugLogger;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by gyatsina on 1/26/2018.
 */

public class StampApi {
    private static final String TAG = StampApi.class.getSimpleName();

    private ServerDAO mServerDAO;

    @Inject
    public StampApi(ServerDAO serverDAO) {
        mServerDAO = serverDAO;
    }

    public void login(){
        Observable<Response<ResponseBody>> call =  mServerDAO.postAuthRequest(BuildConfig.TOSH_APP_LOGIN, BuildConfig.TOSH_APP_PASS).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                doOnNext(this::onLoginResponse);
    }

    private void onLoginResponse(Response<ResponseBody> response) {
        try {
            DebugLogger.e(TAG,"SUCCESS LOGIN " + response.code());
        } catch (Exception ex) {
            DebugLogger.e(TAG,"Failed to login: " + ex.getMessage());
        }
    }
}


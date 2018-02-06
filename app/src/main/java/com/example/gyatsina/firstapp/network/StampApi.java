package com.example.gyatsina.firstapp.network;

import android.content.Context;

import com.example.gyatsina.firstapp.BuildConfig;
import com.example.gyatsina.firstapp.logger.DebugLogger;
import com.example.gyatsina.firstapp.network.events.LoginEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gyatsina.firstapp.network.events.LoginEvent.SUCCESS;

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

    public void login() {
        // implementation with RX java

//        Disposable call = mServerDAO.postAuthRequest(BuildConfig.TOSH_APP_LOGIN, BuildConfig.TOSH_APP_PASS).
//                subscribeOn(Schedulers.io()).
//        observeOn(AndroidSchedulers.mainThread()).
////                doOnNext(this::onLoginResponse).
////                doOnNext(response -> DebugLogger.e(TAG,"in onLoginResponse " + response.code())).
////                doOnError(response -> DebugLogger.e(TAG,"in onLoginResponse " + response.getMessage()));
//        subscribe(result -> {
//            DebugLogger.e(TAG, "error onLoginResponse " + result.code());


        mServerDAO.postAuthRequest(BuildConfig.TOSH_APP_LOGIN, BuildConfig.TOSH_APP_PASS).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new LoginEvent(SUCCESS));
                    DebugLogger.e(TAG, "in onLoginResponse " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                DebugLogger.e(TAG, "error onLoginResponse " + t.getMessage());
            }
        });
    }

    public void uploadFile(Context context, File file) {
        // create upload service client
//        FileUploadService service =
//                ServiceGenerator.createService(FileUploadService.class);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "file";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = mServerDAO.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                DebugLogger.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DebugLogger.e("Upload error:", t.getMessage());
            }
        });
    }
}


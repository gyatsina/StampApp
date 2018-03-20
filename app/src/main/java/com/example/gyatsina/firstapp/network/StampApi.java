package com.example.gyatsina.firstapp.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.gyatsina.firstapp.BuildConfig;
import com.example.gyatsina.firstapp.logger.DebugLogger;
import com.example.gyatsina.firstapp.network.events.ImageIdSentEvent;
import com.example.gyatsina.firstapp.network.events.LoginEvent;
import com.example.gyatsina.firstapp.network.events.StampListErrorEvent;
import com.example.gyatsina.firstapp.network.events.StampListReceivedEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gyatsina.firstapp.network.events.LoginEvent.SUCCESS;

/**
 * Created by gyatsina on 1/26/2018.
 */

@ApiScope
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
                    DebugLogger.e("onLoginResponse", "success " + response.toString());
                    DebugLogger.e(TAG, "in onLoginResponse " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                DebugLogger.e(TAG, "error onLoginResponse " + t.getMessage());
            }
        });
    }

    public void uploadFile(Context context, String fileUri) {
        DebugLogger.e("================uploadFile fileUri ", fileUri);
        Bitmap bm = BitmapFactory.decodeFile(fileUri.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();

        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        Call<List<StampObj>> call = mServerDAO.uploadBase64(encodedImage);
        call.enqueue(new Callback<List<StampObj>>() {
            @Override
            public void onResponse(Call<List<StampObj>> call,
                                   Response<List<StampObj>> response) {
                List<StampObj> list = response.body();
                DebugLogger.e("Upload", response.body()+"");
//                DebugLogger.e("Upload", "list[0].title " + list.get(0).getImage());
                if ((list != null) && (list.size()>0)) {
                    EventBus.getDefault().post(new StampListReceivedEvent(list));
                } else {
                    EventBus.getDefault().post(new StampListErrorEvent());
                }
            }

            @Override
            public void onFailure(Call<List<StampObj>> call, Throwable t) {
                EventBus.getDefault().post(new StampListErrorEvent());
                DebugLogger.e("Upload error:", t.getMessage());
            }
        });
    }

    public void sendImageId(String id){
       Call<Void> call =  mServerDAO.sendImageId(id);
       call.enqueue(new Callback<Void>() {
           @Override
           public void onResponse(Call<Void> call, Response<Void> response) {
               DebugLogger.e("sendImageId", "onResponse " + response.raw());
                DebugLogger.e("sendImageId", "onResponse " + response.code());
               EventBus.getDefault().post(new ImageIdSentEvent());
           }

           @Override
           public void onFailure(Call<Void> call, Throwable t) {
               DebugLogger.e("sendImageId error:", t.getMessage());
           }
       });
    }

    public void uploadFile(String fileUri) {
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        File file = new File(fileUri);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);

        Call<List<JSONObject>> call = mServerDAO.upload(requestBody);
        call.enqueue(new Callback<List<JSONObject>>() {
            @Override
            public void onResponse(Call<List<JSONObject>> call, Response<List<JSONObject>> response) {

                DebugLogger.e("Upload", "success " + call.toString());
                DebugLogger.e("Upload", "success " + call.getClass().toString());
                DebugLogger.e("Upload", "success " + response.toString());
                DebugLogger.e("Upload", "success " + response.body());
                DebugLogger.e("Upload", "success " + response.raw());
                DebugLogger.e("Upload", "success " + response.raw().toString());
                DebugLogger.e("Upload", "success " + response.body().toString());
            }

            @Override
            public void onFailure(Call<List<JSONObject>> call, Throwable t) {
                DebugLogger.e("Upload error:", t.toString() + "__" + call.toString() + "__" + t);
            }
        });
    }

}


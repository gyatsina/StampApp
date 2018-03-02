package com.example.gyatsina.firstapp.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.gyatsina.firstapp.BuildConfig;
import com.example.gyatsina.firstapp.logger.DebugLogger;
import com.example.gyatsina.firstapp.network.events.LoginEvent;
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
                DebugLogger.e("Upload", "list[0].title " + list.get(0).getImage());
                DebugLogger.e("Upload", "list[1].title " + list.get(10).getImage());
                DebugLogger.e("Upload", "list[2].title " + list.get(15).getImage());

                EventBus.getDefault().post(new StampListReceivedEvent(list));

//                EventBus.getDefault().post(new LoginEvent(SUCCESS));
            }

            @Override
            public void onFailure(Call<List<StampObj>> call, Throwable t) {
                DebugLogger.e("Upload error:", t.getMessage());
            }
        });
    }

    public void uploadFile(String fileUri) {
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        File file = new File(fileUri);
//        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PNG, file);

//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", "file",
//                        RequestBody.create(MEDIA_TYPE_PNG, file))
//                .build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
//        Call<ResponseBody> call = mServerDAO.upload(requestBody);
        Call<List<JSONObject>> call = mServerDAO.upload(requestBody);
        call.enqueue(new Callback<List<JSONObject> >() {
            @Override
            public void onResponse(Call<List<JSONObject> > call, Response<List<JSONObject> > response) {

                DebugLogger.e("Upload", "success " + call.toString());
                DebugLogger.e("Upload", "success " + call.getClass().toString());
                DebugLogger.e("Upload", "success " + response.toString());
                DebugLogger.e("Upload", "success " + response.body());
                DebugLogger.e("Upload", "success " + response.raw());
                DebugLogger.e("Upload", "success " + response.raw().toString());
                DebugLogger.e("Upload", "success " + response.body().toString());
            }

            @Override
            public void onFailure(Call<List<JSONObject> > call, Throwable t) {
                DebugLogger.e("Upload error:", t.toString() + "__" + call.toString() + "__" + t);
            }
        });

//        Call<ResponseBody> call = mServerDAO.upload(requestBody);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                DebugLogger.e("Upload", "success " + response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                DebugLogger.e("Upload error:", t.getMessage());
//            }
//        });
    }

//    public void uploadFile(Context context, File file) {
//        RequestBody requestFile =
//                RequestBody.create(
//                        MediaType.parse("image/*"),
//                        file
//                );
//
//        // MultipartBody.Part is used to send also the actual file name
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//
//        // add another part within the multipart request
//        String descriptionString = "file";
//        RequestBody description =
//                RequestBody.create(
//                        okhttp3.MultipartBody.FORM, descriptionString);
//
//        // finally, execute the request
//        Call<ResponseBody> call = mServerDAO.upload(description, body);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call,
//                                   Response<ResponseBody> response) {
//                DebugLogger.e("Upload", "success " + response.toString());
//                DebugLogger.e("Upload", "success " + response.message());
//                DebugLogger.e("Upload", "success " + response.raw());
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                DebugLogger.e("Upload error:", t.getMessage());
//            }
//        });
//    }

//    File file = new File(imageUri.getPath());
//    RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), file);
//    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), firstNameField.getText().toString());
//    RequestBody id = RequestBody.create(MediaType.parse("text/plain"), AZUtils.getUserId(this));
//    Call<User> call = client.editUser(AZUtils.getToken(this), fbody, name, id);
//        call.enqueue(new Callback<User>() {
//        @Override
//        public void onResponse(retrofit.Response<User> response, Retrofit retrofit) {
//            AZUtils.printObject(response.body());
//        }
//
//        @Override
//        public void onFailure(Throwable t) {
//            t.printStackTrace();
//        }
//    });
}


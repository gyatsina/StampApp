package com.example.gyatsina.firstapp.network;

import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by gyatsina on 1/26/2018.
 */

public interface ServerDAO {
    @POST("login/")
//    Observable<Response<ResponseBody>> postAuthRequest(
            Call<String> postAuthRequest(
            @Query("email") String email,
            @Query("password") String pass
    );

//    @Multipart
//    @POST("matches/")
//    Call<ResponseBody> upload(
//            @Part("description") RequestBody description,
//            @Part MultipartBody.Part file
//    );

    @Multipart
    @POST("matches/")
    Call<List<JSONObject>> upload(
            @Part("file") RequestBody file);

    @FormUrlEncoded
    @POST("matches/")
    Call<List<StampObj>> uploadBase64(
            @Field("file") String file
    );


    @POST("addtoschudle/")
    Call<Void> sendImageId(
            @Query("id") String id
    );

//    @Multipart
//    @POST("matches/")
//    Call<ResponseBody> upload (
//            @Part("file\"; filename=\"pp.png\" ") RequestBody file ,
//            @Part("FirstName") RequestBody fname);
//

}




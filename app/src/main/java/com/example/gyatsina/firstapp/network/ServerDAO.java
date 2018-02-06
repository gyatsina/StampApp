package com.example.gyatsina.firstapp.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
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
//    @POST ("/api/Accounts/editaccount")
//    Call<User> editUser (@Part("file\"; filename=\"pp.png\" ") RequestBody file , @Part("FirstName") RequestBody fname, @Part("Id") RequestBody id);

    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );
}




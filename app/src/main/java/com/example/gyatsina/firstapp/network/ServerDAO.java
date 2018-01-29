package com.example.gyatsina.firstapp.network;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by gyatsina on 1/26/2018.
 */

public interface ServerDAO {
    @POST("login/")
    Observable<Response<ResponseBody>> postAuthRequest(
            @Query("email") String email,
            @Query("password") String pass
    );
}




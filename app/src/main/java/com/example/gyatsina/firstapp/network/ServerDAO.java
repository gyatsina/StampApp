package com.example.gyatsina.firstapp.network;

import io.reactivex.Single;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by gyatsina on 1/26/2018.
 */

public interface ServerDAO {
    @POST("login/")
    Single<String> postAuthRequest(
            @Query("email") String token,
            @Query("password") int apiLimit
    );
}




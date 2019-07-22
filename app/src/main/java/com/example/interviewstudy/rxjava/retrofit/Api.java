package com.example.interviewstudy.rxjava.retrofit;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Author:mihon
 * Time: 2019\5\24 0024.16:24
 * Description:This is Api
 */
public interface Api {
    @POST("/user/login")
    Call<String> login(@Query("username") String userName, @Query("password") String password);
}

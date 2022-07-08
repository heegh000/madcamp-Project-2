package com.example.fivepiratesgame;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("{path}")
    Call<List<UserData>> getUD(@Path("path") String path);


    @FormUrlEncoded
    @POST("signup")
    Call<String> postUD(@Field("user_id") String user_id,
                          @Field("user_pw") String user_pw,
                          @Field("user_name") String user_name);

    @FormUrlEncoded
    @POST("signin")
    Call<String> postIN(@Field("user_id") String user_id,
                        @Field("user_pw") String user_pw);

}
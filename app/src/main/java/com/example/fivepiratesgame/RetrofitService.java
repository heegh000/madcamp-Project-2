package com.example.fivepiratesgame;

import com.example.fivepiratesgame.history.HistoryData;
import com.example.fivepiratesgame.ranking.RankingData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("signup")
    Call<String> postSignUp(@Field("user_id") String user_id,
                            @Field("user_pw") String user_pw,
                            @Field("nickname") String nickname);

    @GET("signin")
    Call<String> getSignIn(@Query("user_id") String user_id,
                            @Query("user_pw") String user_pw);

    @GET("history")
    Call<List<HistoryData>> getHistory(@Query("user_id") String user_id);

    @GET("ranking")
    Call<List<RankingData>> getRanking();

    @GET("userdata")
    Call<UserData> getUserData(@Query("user_id") String user_id);

    @FormUrlEncoded
    @POST("create")
    Call<UserData> postCreateUser(@Field("user_id") String user_id,
                                  @Field("avatar_id") String avatar_id);
}
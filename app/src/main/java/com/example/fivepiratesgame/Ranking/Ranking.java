package com.example.fivepiratesgame.Ranking;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Ranking extends AppCompatActivity {

    private ArrayList<RankingData> rankArrayList;

    private RankingAdapter rankAdapter;
    private RecyclerView rvRanking;

    String BASE_URL = "http://172.10.5.52:443/";
    Retrofit retrofit;
    RetrofitService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initRanking();

        service.getRanking().enqueue((new Callback<List<RankingData>>() {
            @Override
            public void onResponse(Call<List<RankingData>> call, Response<List<RankingData>> response) {
                if(response.isSuccessful()) {
                    try {
                        String nickname, gold;

                        for(int i = 0; i < response.body().size(); i++) {
                            nickname = response.body().get(i).getNickname();
                            gold = response.body().get(i).getGold();
                            rankArrayList.add(new RankingData(nickname, gold));
                        }

                        rankAdapter.notifyDataSetChanged();

                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("Ranking GET Fail", "error = " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<RankingData>> call, Throwable t) {
                Log.d ("Ranking GET on fail", t.getMessage());
            }
        }));
    }

    private void initRanking() {
        rankArrayList = new ArrayList<>();
        rankAdapter = new RankingAdapter(this, rankArrayList);

        rvRanking = (RecyclerView) findViewById(R.id.rvRanking);
        rvRanking.setLayoutManager(new LinearLayoutManager(this));
        rvRanking.setAdapter(rankAdapter);

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        service = retrofit.create(RetrofitService.class);
    }
}
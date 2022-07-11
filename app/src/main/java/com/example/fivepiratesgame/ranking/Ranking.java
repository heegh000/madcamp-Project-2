package com.example.fivepiratesgame.ranking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.RetrofitService;
import com.example.fivepiratesgame.UserData;
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

    private List<UserData> rankingList;
    private RankingAdapter rankAdapter;
    private RecyclerView rvRanking;

    private RetrofitService service;

    private TextView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initRetrofit();
        initRanking();

        getRanking();

        goback = findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void initRetrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        service = retrofit.create(RetrofitService.class);
    }

    private void initRanking() {
        rankingList = new ArrayList<>();
        rankAdapter = new RankingAdapter(this, rankingList);
        rvRanking = (RecyclerView) findViewById(R.id.rvRanking);
        rvRanking.setLayoutManager(new LinearLayoutManager(this));
        rvRanking.setAdapter(rankAdapter);
    }

    private void getRanking() {
        service.getRanking().enqueue((new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                if(response.isSuccessful()) {
                    rankingList.clear();
                    rankingList.addAll(response.body());
                    rankAdapter.notifyDataSetChanged();
                }
                else {
                    Log.d("Ranking GET Fail", "error = " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {
                Log.d ("Ranking GET on fail", t.getMessage());
            }
        }));
    }
}
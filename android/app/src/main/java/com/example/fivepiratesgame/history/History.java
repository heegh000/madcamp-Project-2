package com.example.fivepiratesgame.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class History extends AppCompatActivity {

    private String userID;

    private List<HistoryData> historyList;
    private HistoryAdapter historyAdapter;
    private RecyclerView rvHistory;

    private RetrofitService service;

    private TextView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initRetrofit();
        initHistory();

        getHistory();

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

    private void initHistory() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, (List<HistoryData>) historyList);
        rvHistory = (RecyclerView) findViewById(R.id.rvHistroy);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);
    }

    private void getHistory() {
        service.getHistory(userID).enqueue(new Callback<List<HistoryData>>() {
            @Override
            public void onResponse(Call<List<HistoryData>> call, Response<List<HistoryData>> response) {
                if(response.isSuccessful()) {
                    historyList.clear();
                    historyList.addAll(response.body());
                    historyAdapter.notifyDataSetChanged();
                }
                else {
                    Log.d("History GET Fail", "error = " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<HistoryData>> call, Throwable t) {
                Log.d ("History GET on fail", t.getMessage());
            }
        });
    }
}
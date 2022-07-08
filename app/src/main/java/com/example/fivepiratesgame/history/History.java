package com.example.fivepiratesgame.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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

    private ArrayList<HistoryData> historyArrayList;

    private HistoryAdapter historyAdapter;
    private RecyclerView rvHistory;

    String BASE_URL = "http://172.10.5.52:443/";
    Retrofit retrofit;
    RetrofitService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initHistory();

        //user_id 부분에 in-memory에서 관리중인 user_id를 보내야함
        service.getHistory("aa").enqueue(new Callback<List<HistoryData>>() {
            @Override
            public void onResponse(Call<List<HistoryData>> call, Response<List<HistoryData>> response) {
                if(response.isSuccessful()) {
                    try {
                        String time, result;

                        for(int i = 0; i < response.body().size(); i++) {
                            time = response.body().get(i).getTime();
                            result = response.body().get(i).getResult();
                            historyArrayList.add(new HistoryData(time, result));
                        }
                        historyAdapter.notifyDataSetChanged();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private void initHistory() {
        historyArrayList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, historyArrayList);

        rvHistory = (RecyclerView) findViewById(R.id.rvHistroy);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        service = retrofit.create(RetrofitService.class);
    }
}
package com.example.fivepiratesgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fivepiratesgame.game.GameActivity;
import com.example.fivepiratesgame.history.History;
import com.example.fivepiratesgame.ranking.Ranking;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static HashMap<Integer, Integer> mapAvatar = new HashMap<Integer, Integer>(){{
        put(0, R.drawable.bulbasaur);
        put(1, R.drawable.charmander);
        put(2, R.drawable.egg);
        put(3, R.drawable.jigglypuff);
        put(4, R.drawable.meowth);
        put(5, R.drawable.pikachu);
        put(6, R.drawable.snorlex);
        put(7, R.drawable.squirtle);
    }};

    private String userID;

    private TextView tvName, tvID, history, ranking, tvRank, tvGold;
    private ImageView avatar;
    private AppCompatButton playgame;

    private RetrofitService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRetrofit();

        tvName = findViewById(R.id.tvName);
        tvID = findViewById(R.id.tvID);
        avatar = findViewById(R.id.avatar);
        tvRank = findViewById(R.id.tvRank);
        tvGold = findViewById(R.id.tvGold);

        playgame = findViewById(R.id.playgame);
        history = findViewById(R.id.history);
        ranking = findViewById(R.id.ranking);

        // 아바타, 이름, 골드 등등 정보도 DB에서 가져오기

        Intent intent = getIntent();


        userID = intent.getStringExtra("userID");
        String nickname = intent.getStringExtra("nickname");
        int avatarID = intent.getIntExtra("avatarID", 2);
        String rank = intent.getStringExtra("rank");
        String gold = intent.getStringExtra("gold");


        tvName.setText(nickname);
        tvID.setText(userID);
        avatar.setImageResource(mapAvatar.get(avatarID));
        tvRank.setText(rank);
        tvGold.setText(gold);

        playgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GameActivity.class)
                        .putExtra("userID", userID)
                        .putExtra("userName", nickname)
                        .putExtra("avatarID", avatarID));
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), History.class)
                        .putExtra("userID", userID));
            }
        });

        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Ranking.class));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getUserData(userID);
    }

    private void getUserData(String userID) {

        service.getUserData(userID).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if(response.isSuccessful()) {
                    UserData uData = response.body();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class)
                            .putExtra("userID", userID)
                            .putExtra("nickname", uData.getNickname())
                            .putExtra("avatarID", uData.getAvatarID())
                            .putExtra("gold", uData.getGold())
                            .putExtra("rank", uData.getRank()));
                }
                else {
                    Log.d("Main getUser fail", "error = " + String.valueOf(response.code()) + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.d ("Main getUser on fail", "Fail " + t.getMessage());
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
}
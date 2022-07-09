package com.example.fivepiratesgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fivepiratesgame.game.GameActivity;
import com.example.fivepiratesgame.history.History;
import com.example.fivepiratesgame.login.LoginIntro;
import com.example.fivepiratesgame.ranking.Ranking;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.net.URISyntaxException;
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
    private TextView logout;

    private RetrofitService service;

    private int bringGold = 0;

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
        logout = findViewById(R.id.logout);

        // 아바타, 이름, 골드 등등 정보 가져오기

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

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("비상금을 챙겨가시겠습니까?");
                builder.setMessage("현재 보유 금화 : "+ gold);

                final EditText et_bringGold = new EditText(MainActivity.this);
                builder.setView(et_bringGold);

                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bringGold = Integer.parseInt(et_bringGold.getText().toString());

                                if (Integer.parseInt(gold) < bringGold) {
                                    Toast.makeText(getApplicationContext(), "현재 보유 금화보다 많은 금화를 가져갈 수 없습니다", Toast.LENGTH_LONG).show();
                                    bringGold = 0;
                                }

                                Log.d("bringGold", String.valueOf(bringGold));

                                startActivity(new Intent(getApplicationContext(), GameActivity.class)
                                        .putExtra("userID", userID)
                                        .putExtra("nickname", nickname)
                                        .putExtra("avatarID", avatarID)
                                        .putExtra("gold", bringGold));

//                                finish();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Log out");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), LoginIntro.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
                AlertDialog dialog = builder.create();
                dialog.show();

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

                    tvName.setText(uData.getNickname());
                    tvID.setText(userID);
                    avatar.setImageResource(mapAvatar.get(uData.getAvatarID()));
                    tvRank.setText(uData.getRank());
                    tvGold.setText(uData.getGold());
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
package com.example.fivepiratesgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fivepiratesgame.game.GameActivity;
import com.example.fivepiratesgame.history.History;
import com.example.fivepiratesgame.login.LoginIntro;
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

    private String userID, nickname, gold;

    private TextView tvName, tvID, history, ranking, tvRank, tvGold;
    private ImageView avatar;
    private AppCompatButton playgame;
    private TextView logout;

    private RetrofitService service;

    private int bringGold = 0;
    private int avatarID = 0;

    private final long finishtimeed = 1000;
    private long presstime = 0;

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
        nickname = intent.getStringExtra("nickname");
        avatarID = intent.getIntExtra("avatarID", 2);
        gold = intent.getStringExtra("gold");
        String rank = intent.getStringExtra("rank");


        tvName.setText(nickname);
        tvID.setText(userID);
        avatar.setImageResource(mapAvatar.get(avatarID));
        tvRank.setText(rank);
        tvGold.setText(gold);


        playgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBringGoldDialog(gold);



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

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if (0 <= intervalTime && finishtimeed >= intervalTime)
        {
            finish();
        }
        else
        {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBringGoldDialog(String gold){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.dialog_bring_gold, (LinearLayout)findViewById(R.id.bgDialog));

        TextView tvGold;
        EditText etBringGold;
        AppCompatButton btnConfirm;

        builder.setView(view);

        tvGold = view.findViewById(R.id.myGold);
        tvGold.setText(gold);
        etBringGold= view.findViewById(R.id.bringGold);
        btnConfirm = view.findViewById(R.id.bgConfirm);

        AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bringGold = Integer.parseInt(etBringGold.getText().toString());

                if (Integer.parseInt(gold) < bringGold) {
                    Toast.makeText(getApplicationContext(), "현재 보유 금화보다 많은 금화를 가져갈 수 없습니다", Toast.LENGTH_LONG).show();
                    bringGold = 0;
                }
                dialog.dismiss();

                startActivity(new Intent(getApplicationContext(), GameActivity.class)
                        .putExtra("userID", userID)
                        .putExtra("nickname", nickname)
                        .putExtra("avatarID", avatarID)
                        .putExtra("gold", bringGold));

            }
        });
        // Dialog 형태 지우기
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();

    }
}
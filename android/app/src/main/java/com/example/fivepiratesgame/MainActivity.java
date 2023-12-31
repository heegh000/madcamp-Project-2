package com.example.fivepiratesgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
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
        put(2, R.drawable.squirtle);
        put(3, R.drawable.jigglypuff);
        put(4, R.drawable.meowth);
        put(5, R.drawable.pikachu);
        put(6, R.drawable.snorlex);
        put(7, R.drawable.egg);
    }};

    private String userID, nickname, gold;

    private TextView tvName, tvID, history, ranking, tvRank, tvGold;
    private ImageView avatar;
    private AppCompatButton playgame;
    private TextView logout;

    private RetrofitService service;

    private int bringGold = 0;
    private int avatarID = 0;

    private final long TIMEOUT = 1000;
    private long presstime = 0;

    private long lastClickTime = 0;
    private int click_cnt = 0;
    private final int click_required = 10;
    private int easter = 0;


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

        avatar.setOnClickListener(v->TouchContinuously());

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
                showLogoutDialog();
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

                    gold = uData.getGold();
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

        if (0 <= intervalTime && TIMEOUT >= intervalTime)
        {
            finish();
        }
        else
        {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void TouchContinuously(){

        if(SystemClock.elapsedRealtime() - lastClickTime < TIMEOUT){
            click_cnt ++;
        }else{
            click_cnt = 1;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        if(click_cnt == click_required) {
            easter = 1;
        }
        if(easter == 1) avatar.setImageResource(mapAvatar.get(click_cnt%8));
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
                if(TextUtils.isEmpty(etBringGold.getText())) {
                    bringGold = 0;
                }
                else {
                    bringGold = Integer.parseInt(etBringGold.getText().toString());
                }

                if (Integer.parseInt(gold) < bringGold) {
                    Toast.makeText(getApplicationContext(), "현재 보유 금화보다 많은 금화를 가져갈 수 없습니다", Toast.LENGTH_LONG).show();
                    bringGold = 0;
                }
                if (bringGold > 200) {
                    Toast.makeText(getApplicationContext(), "200금화를 초과하여 가져갈 수 없습니다", Toast.LENGTH_LONG).show();
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

    private void showLogoutDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.dialog_loggout, (LinearLayout)findViewById(R.id.outDialog));

        AppCompatButton btnReject, btnConfirm;

        builder.setView(view);

        btnConfirm = view.findViewById(R.id.outConfirm);
        btnReject= view.findViewById(R.id.outReject);

        AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), LoginIntro.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();
                finish();

                dialog.dismiss();

            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



        // Dialog 형태 지우기
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
    }
}
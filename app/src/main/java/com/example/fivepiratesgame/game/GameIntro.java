package com.example.fivepiratesgame.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.login.LoginIntro;

public class GameIntro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_intro);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent (getApplicationContext(), GameActivity.class);
            startActivity(intent); //인트로 동안 함께 플레이할 참가자들 찾고 game 넘어감. // 게임룰 설명??
            finish();
        },2000); //2초 후 실행
    }
}
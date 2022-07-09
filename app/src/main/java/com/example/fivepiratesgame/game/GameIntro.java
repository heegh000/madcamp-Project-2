package com.example.fivepiratesgame.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.login.LoginIntro;

public class GameIntro extends AppCompatActivity {

    private final long finishtimeed = 1000;
    private long presstime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_intro);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent (getApplicationContext(), GameActivity.class);
            startActivity(intent); //인트로 동안 함께 플레이할 참가자들 찾고 game 넘어감. // 게임룰 설명??
            finish();
        },2000); //2초 후 실행 // 지금은 delay로 해놨는데 참가자 찾을 때까지로 조건 바꿔야함!
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
            Toast.makeText(getApplicationContext(), "한 번 더 누르시면 대기열에서 나갑니다", Toast.LENGTH_SHORT).show();
        }
    }
}
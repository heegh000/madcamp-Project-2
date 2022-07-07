package com.example.fivepiratesgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.fivepiratesgame.login.LoginIntro;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent (getApplicationContext(), LoginIntro.class);
            startActivity(intent); //인트로 실행 후 바로 login 넘어감.
            finish();
        },2000); //2초 후 인트로 실행
    }
}
package com.example.fivepiratesgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvName, tvID, history, ranking;
    private ImageView avatar;
    private AppCompatButton playgame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvName = findViewById(R.id.tvName);
        tvID = findViewById(R.id.tvID);
        avatar = findViewById(R.id.avatar);
        playgame = findViewById(R.id.playgame);
        // 아바타, 이름, 등등 정보도 DB에서 가져오기

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        tvID.setText(userID);




    }
}
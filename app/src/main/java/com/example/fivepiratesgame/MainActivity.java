package com.example.fivepiratesgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fivepiratesgame.game.GameActivity;


import java.util.HashMap;

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
        history = findViewById(R.id.history);
        ranking = findViewById(R.id.ranking);

        // 아바타, 이름, 골드 등등 정보도 DB에서 가져오기

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String userName = intent.getStringExtra("userName");
        int avatarID = intent.getIntExtra("avatarID", 2);

        tvName.setText(userName);
        tvID.setText(userID);
        avatar.setImageResource(mapAvatar.get(avatarID));

        playgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GameActivity.class)
                        .putExtra("userID", userID)
                        .putExtra("userName", userName)
                        .putExtra("avatarID", avatarID));
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.example.fivepiratesgame.History.History.class));
            }
        });

        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Ranking.class));
            }
        });




    }
}
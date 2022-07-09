package com.example.fivepiratesgame.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fivepiratesgame.R;

import static com.example.fivepiratesgame.MainActivity.mapAvatar;

public class GameActivity extends AppCompatActivity {

    private TextView tvName, tvbringGold;
    private ImageView avatar;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        tvbringGold = findViewById(R.id.bringGold);
        avatar = findViewById(R.id.avatar);

        Intent intent = getIntent();

        userID = intent.getStringExtra("userID");
        String nickname = intent.getStringExtra("nickname");
        int avatarID = intent.getIntExtra("avatarID", 2);
        int bringGold = intent.getIntExtra("gold", 0);

        tvName.setText(nickname);
        avatar.setImageResource(mapAvatar.get(avatarID));
        tvbringGold.setText(String.valueOf(bringGold));

    }
}
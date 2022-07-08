package com.example.fivepiratesgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import static com.example.fivepiratesgame.MainActivity.mapAvatar;

public class CustomizeCharacter extends AppCompatActivity {

    private TextView tvName;
    private ImageView avatar;
    private AppCompatButton createBtn;

    int tap = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_character);

        mapAvatar.put(0, R.drawable.bulbasaur);
        mapAvatar.put(1, R.drawable.charmander);
        mapAvatar.put(2, R.drawable.egg);

        tvName = findViewById(R.id.tvName);
        avatar = findViewById(R.id.avatar);
        createBtn = findViewById(R.id.create_account);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String userName = intent.getStringExtra("userName");

        tvName.setText(userName);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tap ++;
                tap %= 3;
                avatar.setImageResource(mapAvatar.get(tap));

            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class)
                        .putExtra("userID", userID)
                        .putExtra("userName", userName)
                        .putExtra("avatarID", tap));

            }
        });
    }
}
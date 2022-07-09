package com.example.fivepiratesgame.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fivepiratesgame.R;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameActivity extends AppCompatActivity {

    ImageView player1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        player1 = findViewById(R.id.player1);

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");

        try {
            socket = IO.socket("http://192.249.18.135:443");

            socket.on("join", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "AAAAAAA", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            socket.on("offer", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), args[0].toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            socket.connect();
            socket.emit("test", nickname);
            socket.emit("join", "join test");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        player1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("offer", "offer test");
            }
        });
    }
}
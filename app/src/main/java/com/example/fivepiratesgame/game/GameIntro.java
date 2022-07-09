package com.example.fivepiratesgame.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fivepiratesgame.Global;
import com.example.fivepiratesgame.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class GameIntro extends AppCompatActivity {

    private final long finishtimeed = 1000;
    private long presstime = 0;

    String userID;
    String nickname;
    int avatarID;
    int bringGold;

    TextView tvUserNum;

    int userNum;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_intro);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        nickname = intent.getStringExtra("nickname");
        avatarID = intent.getIntExtra("avatarID", 0);
        bringGold = intent.getIntExtra("gold", 0);

        tvUserNum = findViewById(R.id.tvUserNum);

        socket();

    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if (0 <= intervalTime && finishtimeed >= intervalTime)
        {
            Global.socket.disconnect();
            finish();
        }
        else
        {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한 번 더 누르시면 대기열에서 나갑니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void socket() {
        Global.socket.connect();

        JSONObject data = new JSONObject();
        try {
            data.put("user_id", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Global.socket.emit("join", data);
        Global.socket.on("join", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    userNum = (int) data.get("count");

                    tvUserNum.setText(Integer.toString(userNum) + " / 5");

                    if(userNum == 5) {
                        startActivity(new Intent(getApplicationContext(), GameActivity.class)
                                .putExtra("userID", userID)
                                .putExtra("nickname", nickname)
                                .putExtra("avatarID", avatarID)
                                .putExtra("gold", bringGold));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
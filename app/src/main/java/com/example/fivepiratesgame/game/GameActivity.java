package com.example.fivepiratesgame.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fivepiratesgame.Global;
import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameActivity extends AppCompatActivity {

    private List<PlayerData> playerList;
    private PlayerAdapter playerAdapter;
    private RecyclerView rvPlayer;

    private TextView tvName, tvbringGold, reject, accept;
    private ImageView avatar;

    private PlayerData me;

    private String userID;
    private String nickname;
    private int avatarID;
    private int bringGold;
    private int roomID;

    ConstraintLayout introGame;
    ConstraintLayout inGame;
    TextView tvUserNum;
    int userNum;

    private final long finishtimeed = 1000;
    private long presstime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initGame();
        socket();

        //반대버튼 이벤트 설정
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(me.getVote() != -1) {
                    me.setVote(0);
                    Global.socket.emit("vote", roomID, 0, false);
                }
                //투표를 했을때 다시 버튼을 누르면 어떻게 할지 정해야함
                else {
                    //만약 같은 버튼을 또 누르면 무시
                    //다른 버튼을 누르는 상황은 처리해야함
                }
            }
        });

        //찬성버튼 이벤트 설정
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(me.getVote() != -1) {
                    me.setVote(1);
                    Global.socket.emit("vote", roomID, 1, false);
                }
                //투표를 했을때 다시 버튼을 누르면 어떻게 할지 정해야함
                else {
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        Global.socket.disconnect();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if (0 <= intervalTime && finishtimeed >= intervalTime)
        {
            Global.socket.emit("disconnect_req", roomID, userID);
        }
        else
        {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한 번 더 누르시면 대기열에서 나갑니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void initGame() {
        playerList = new ArrayList<>();
        playerAdapter = new PlayerAdapter(this, playerList);
        rvPlayer = (RecyclerView) findViewById(R.id.rvPlayer);
        rvPlayer.setLayoutManager(new LinearLayoutManager(this));
        rvPlayer.setAdapter(playerAdapter);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        nickname = intent.getStringExtra("nickname");
        avatarID = intent.getIntExtra("avatarID", 2);
        bringGold = intent.getIntExtra("gold", 0);
        roomID = intent.getIntExtra("roomID", 0);

        introGame = findViewById(R.id.introGame);
        inGame = findViewById(R.id.inGame);
        tvUserNum = findViewById(R.id.tvUserNum);
        reject = findViewById(R.id.reject);
        accept = findViewById(R.id.accept);

        introGame.setVisibility(View.VISIBLE);
        inGame.setVisibility(View.GONE);

    }

    private void socket() {

        //종료 요청
        Global.socket.on("disconnect_req", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //모든 이벤트 off해야함
                Global.socket.off("join");
                Global.socket.disconnect();
                finish();
            }
        });

        //현재 방에 들어와있는 사람의 수를 받아서 화면에 표시, 5명이면 게임 화면으로 전환
        Global.socket.on("join", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                userNum = (int) args[0];
                roomID = (int) args[1];

                tvUserNum.setText(Integer.toString(userNum) + " / 5");

                if (userNum == 5) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            introGame.setVisibility(View.GONE);
                            inGame.setVisibility(View.VISIBLE);
                        }
                    });
                }

            }
        });

        //방에 5명이 들어와서 서버로 부터 받은 데이터를 RV에 표시
        Global.socket.on("game_start", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data ;

                    //이름 리팩토링 해야함
                    String tempUID;
                    String tempName;
                    int tempAID;
                    int tempOrder;

                    JSONArray dataArr = (JSONArray) ((JSONObject) args[0]).get("data");
                    playerList.clear();

                    for (int i = 0; i < dataArr.length(); i++) {
                        data = (JSONObject) dataArr.get(i);
                        tempUID = (String) data.get("user_id");
                        tempName = (String) data.get("nickname");
                        tempAID = (int) data.get("avatar_id");
                        tempOrder = (int) data.get("order");

                        PlayerData player = new PlayerData(tempUID, tempName, tempAID, tempOrder);
                        if (userID.equals(tempUID)) {
                            me = player;
                            me.setBringGold(bringGold);
                        }
                        playerList.add(player);
                    }

                    Comparator<PlayerData> comparator = new Comparator<PlayerData>() {
                        @Override
                        public int compare(PlayerData a, PlayerData b) {
                            return b.getOrder() - a.getOrder();
                        }
                    };

                    Collections.sort(playerList, comparator);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playerAdapter.notifyDataSetChanged();

                            if(me.getOrder() == (int) args[1]) {
                                sendOffer((int) args[1]);
                            }
                        }
                    });
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //gold 배분 제안이 끝나서 자신의 gold만 화면에 표시
        Global.socket.on("offer_end", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int myGold = (int) args[me.getOrder()+1];
                me.setGold(myGold);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

        //과반 초과 찬성시 게임 종료 -> 서버에게 현재 bringGold를 줌
        Global.socket.on("offer_accept", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Global.socket.emit("game_end", roomID, userID, bringGold);
            }
       });

        // 게임 종료 -> 소켓 disconnect
        Global.socket.on("game_end", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Global.socket.emit("disconnect_req", roomID, userID);

            }
        });

        Global.socket.connect();

        Global.socket.emit("join", userID, bringGold);

    }

    private void sendOffer(int num) {

        //현재 자신의 order만큼 입력을 받고 배열로 만들어서 넘겨줘야함
        //사용자한테 입력 받아야함
        int gold1 = 100;
        int gold2 = 100;
        int gold3 = 200;
        int gold4 = 250;
        int gold5 = 350;

        Global.socket.emit("offer", roomID, gold1, gold2, gold3, gold4, gold5);
    }
}
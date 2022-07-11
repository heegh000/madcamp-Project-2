package com.example.fivepiratesgame.game;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fivepiratesgame.Global;
import com.example.fivepiratesgame.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.socket.emitter.Emitter;

public class GameActivity extends AppCompatActivity {

    private List<PlayerData> playerList;
    private PlayerAdapter playerAdapter;
    private RecyclerView rvPlayer;

    private LinearLayout voteLayout;

    private TextView tvName, tvbringGold, reject, accept;
    private ImageView avatar, refresh;

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

    boolean isEnd = false;


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
                if(me.getVote() == -1) {
                    me.setVote(0);
                    Global.socket.emit("vote", roomID, 0);
                    reject.setBackgroundResource(R.drawable.game_reject);
                }
                //투표를 했을때 다시 버튼을 누르면 어떻게 할지 정해야함
                //3초 후 refresh
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
                if(me.getVote() == -1) {
                    me.setVote(1);
                    Global.socket.emit("vote", roomID, 1);
                    accept.setBackgroundResource(R.drawable.game_accept);
                }
                //투표를 했을때 다시 버튼을 누르면 어떻게 할지 정해야함
                else {
                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(me.getVote() != -1) {
                    Global.socket.emit("refresh", roomID, me.getVote());
                    me.setVote(-1);

                    accept.setBackgroundResource(R.drawable.game_textbox);
                    reject.setBackgroundResource(R.drawable.game_textbox);
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
        voteLayout = findViewById(R.id.voteLayout);
        reject = findViewById(R.id.reject);
        accept = findViewById(R.id.accept);
        refresh = findViewById(R.id.refresh);
        tvbringGold = findViewById(R.id.bringGold);


        introGame.setVisibility(View.VISIBLE);
        inGame.setVisibility(View.GONE);

        voteLayout.setVisibility(View.INVISIBLE);

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
                            tvbringGold.setText(Integer.toString(me.getBringGold()));
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
                JSONArray arr = (JSONArray) args[0];
                int myGold = 0;
                try {
                    myGold = (int) arr.get(me.getOrder());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                me.setGold(myGold);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (me.getState() == 1) { voteLayout.setVisibility(View.VISIBLE); }
                        else {
                            PlayerData player;
                            for(int i = 0; i < playerList.size(); i++) {
                                player = playerList.get(i);
                                if(player.getState() == 1) {
                                    try {
                                        player.setGold((int) arr.get(player.getOrder()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                        playerAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

        //과반 초과 찬성시 게임 종료 -> 서버에게 현재 bringGold를 줌
        Global.socket.on("offer_accept", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(me.getState() == 0) {

                }
                else  {
                    Global.socket.emit("game_end", roomID, userID, bringGold);
                }
            }
       });

        Global.socket.on("dead", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                me.setState(0);
                Global.socket.emit("dead", roomID, userID);
            }
        });

        Global.socket.on("offer_reject", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(int i = 0; i < playerList.size(); i++) {
                    if(playerList.get(i).getOrder() == (int) args[0] + 1) {
                        playerList.get(i).setState(0);
                    }
                }

                me.setVote(-1);

                accept.setBackgroundResource(R.drawable.game_textbox);
                reject.setBackgroundResource(R.drawable.game_textbox);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(me.getOrder() == (int)args[0]) {
                            sendOffer((int)args[0]);
                        }
                    }
                });
            }
        });

        // 게임 종료 -> 소켓 disconnect
        Global.socket.on("game_end", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Global.socket.emit("disconnect_req", roomID, userID);
                // 종료 화면?
            }
        });

        Global.socket.connect();

        Global.socket.emit("join", userID, bringGold);

    }

    private void sendOffer(int num) {

        //현재 자신의 order만큼 입력을 받고 배열로 만들어서 넘겨줘야함
        //사용자한테 입력 받아야함

        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        View view = LayoutInflater.from(GameActivity.this).inflate(
                R.layout.dialog_gold_distribution, (LinearLayout)findViewById(R.id.gdDialog));

//        TextView tvName5, tvName4, tvName3, tvName2, tvName1;
        TextView tvleftCoin;
        EditText etG5, etG4,etG3, etG2, etG1;
        AppCompatButton btnConfirm;

        int leftCoin = 1000;
        builder.setView(view);

        tvleftCoin = view.findViewById(R.id.leftCoin);
        tvleftCoin.setText(String.valueOf(leftCoin));

        etG5 = view.findViewById(R.id.o5_gold);
        etG4 = view.findViewById(R.id.o4_gold);
        etG3 = view.findViewById(R.id.o3_gold);
        etG2 = view.findViewById(R.id.o2_gold);
        etG1 = view.findViewById(R.id.o1_gold);
        btnConfirm = view.findViewById(R.id.gdConfirm);

        if(num<4) {
            ((LinearLayout)view.findViewById(R.id.o4_layout)).setVisibility(View.GONE);
        }
        if(num<5) {
            ((LinearLayout)view.findViewById(R.id.o5_layout)).setVisibility(View.GONE);
        }

        AlertDialog dialog = builder.create();

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                int gold5, gold4, gold3, gold2, gold1;

                if(TextUtils.isEmpty(etG5.getText())) { gold5 = 0; }
                else gold5 = Integer.parseInt(etG5.getText().toString());

                if(TextUtils.isEmpty(etG4.getText())) { gold4 = 0; }
                else gold4 = Integer.parseInt(etG4.getText().toString());

                if(TextUtils.isEmpty(etG3.getText())) { gold3 = 0; }
                else gold3 = Integer.parseInt(etG3.getText().toString());

                if(TextUtils.isEmpty(etG2.getText())) { gold2 = 0; }
                else gold2 = Integer.parseInt(etG2.getText().toString());

                if(TextUtils.isEmpty(etG1.getText())) { gold1 = 0; }
                else gold1 = Integer.parseInt(etG1.getText().toString());

                int total = gold5 + gold4 + gold3 + gold2 + gold1;
                tvleftCoin.setText(String.valueOf(1000-total));

            }
        };
        etG5.addTextChangedListener(afterTextChangedListener);
        etG4.addTextChangedListener(afterTextChangedListener);
        etG3.addTextChangedListener(afterTextChangedListener);
        etG2.addTextChangedListener(afterTextChangedListener);
        etG1.addTextChangedListener(afterTextChangedListener);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int gold5, gold4, gold3, gold2, gold1;

                if(etG5.getText().equals("")) { gold5 = 0; }
                else gold5 = Integer.parseInt(etG5.getText().toString());

                if(etG4.getText().equals("")) { gold4 = 0; }
                else gold4 = Integer.parseInt(etG4.getText().toString());

                if(etG3.getText().equals("")) { gold3 = 0; }
                else gold3 = Integer.parseInt(etG3.getText().toString());

                if(etG2.getText().equals("")) { gold2 = 0; }
                else gold2 = Integer.parseInt(etG2.getText().toString());

                if(etG1.getText().equals("")) { gold1 = 0; }
                else gold1 = Integer.parseInt(etG1.getText().toString());

                int total = gold5 + gold4 + gold3 + gold2 + gold1;

                if (total != 1000){
                    Toast.makeText(getApplicationContext(), "입력한 금화의 합이 1000개가 아닙니다", Toast.LENGTH_LONG).show();
                }
                else {
                    switch (num){
                        case 3:
                            Global.socket.emit("offer", roomID, gold1, gold2, gold3);
                            break;
                        case 4:
                            Global.socket.emit("offer", roomID, gold1, gold2, gold3, gold4);
                            break;
                        case 5:
                            Global.socket.emit("offer", roomID, gold1, gold2, gold3, gold4, gold5);
                            break;
                    }
                    dialog.dismiss();
                }
            }
        });

        // Dialog 형태 지우기
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        if (!GameActivity.this.isFinishing()) {
            dialog.show();
        }

    }
}
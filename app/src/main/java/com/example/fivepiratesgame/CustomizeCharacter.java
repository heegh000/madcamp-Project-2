package com.example.fivepiratesgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import static com.example.fivepiratesgame.MainActivity.mapAvatar;

import com.example.fivepiratesgame.login.LoginIntro;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CustomizeCharacter extends AppCompatActivity {


    private TextView tvName;
    private ImageView avatar;
    private AppCompatButton createBtn;

    private Retrofit retrofit;
    private RetrofitService service;

    int tap = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_character);

        initRetrofit();

        tvName = findViewById(R.id.tvName);
        avatar = findViewById(R.id.avatar);
        createBtn = findViewById(R.id.create_account);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String nickname = intent.getStringExtra("nickname");

        tvName.setText(nickname);

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
                postCreateUser(userID, tap);
            }
        });
    }

    private void initRetrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        service = retrofit.create(RetrofitService.class);
    }

    private void postCreateUser(String userID, int tap) {
        service.postCreateUser(userID, Integer.toString(tap)).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if(response.isSuccessful()) {

                    UserData uData = response.body();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("userID", userID)
                            .putExtra("nickname", uData.getNickname())
                            .putExtra("avatarID", uData.getAvatarID())
                            .putExtra("gold", uData.getGold())
                            .putExtra("rank", uData.getRank());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                else {
                    Log.d("postCreateUser fail", "error = " + String.valueOf(response.code()) + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.d ("postCreateUser on fail", "Fail " + t.getMessage());
            }
        });
    }
}
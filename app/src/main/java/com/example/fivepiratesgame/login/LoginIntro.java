package com.example.fivepiratesgame.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fivepiratesgame.R;

public class LoginIntro extends AppCompatActivity implements View.OnClickListener{

    private final long finishtimeed = 1000;
    private long presstime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppCompatButton googlelogin, login_local, newaccount;

        googlelogin = findViewById(R.id.googlelogin);
        login_local = findViewById(R.id.login);
        newaccount = findViewById(R.id.newaccount);

//        googlelogin.setOnClickListener(this);
        login_local.setOnClickListener(this);
        newaccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id==R.id.login) {
//            startActivity(new Intent(getApplicationContext(),
//                    LoginLocal.class));
            startActivity(new Intent(getApplicationContext(),
                    LoginLocal.class));
        }
        else if (id==R.id.newaccount){
            startActivity(new Intent(getApplicationContext(),
                    Register.class));

        }

    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if (0 <= intervalTime && finishtimeed >= intervalTime)
        {
            finish();
        }
        else
        {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }
}
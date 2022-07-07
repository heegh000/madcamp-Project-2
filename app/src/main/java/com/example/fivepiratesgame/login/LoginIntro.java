package com.example.fivepiratesgame.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.fivepiratesgame.R;

public class LoginIntro extends AppCompatActivity implements View.OnClickListener{

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
            startActivity(new Intent(getApplicationContext(),
                    LoginLocal.class));

        }
        else if (id==R.id.newaccount){
            startActivity(new Intent(getApplicationContext(),
                    Register.class));

        }


    }
}
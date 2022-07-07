package com.example.fivepiratesgame.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.fivepiratesgame.CustomizeCharacter;
import com.example.fivepiratesgame.MainActivity;
import com.example.fivepiratesgame.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {
    private EditText et_id, et_pw;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText에 현재 입력되어 있는 값 가져오기
                String userID = et_id.getText().toString();
                String userPW = et_pw.getText().toString();
                // ID PW 제한 조건 추가하기

                //
                Intent intent = new Intent(Register.this, CustomizeCharacter.class);
                intent.putExtra("userID", userID);
                startActivity(intent);

//                Response.Listener<String> responseListener = new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) { // 회원가입 요청을 한 후 결과값을 json object로 받음
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            boolean success = jsonObject.getBoolean("success"); // 서버통신성공했음?
//                            if(success) {
//                                Toast.makeText(getApplicationContext(), "Register Succeeded", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(Register.this, LoginLocal.class);
//                                startActivity(intent);
//                            }
//                            else {
//                                Toast.makeText(getApplicationContext(), "Register Failed", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                };
//
//                RegisterRequest registerRequest = new RegisterRequest(userID, userPW, responseListener);
//                RequestQueue queue = Volley.newRequestQueue(Register.this);
//                queue.add(registerRequest);


            }
        });
    }
}
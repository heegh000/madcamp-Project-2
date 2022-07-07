package com.example.fivepiratesgame.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.fivepiratesgame.MainActivity;
import com.example.fivepiratesgame.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginLocal extends AppCompatActivity {

    private EditText et_id, et_pw;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_local);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        loginBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = et_id.getText().toString();
                String userPW = et_pw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { // 회원가입 요청을 한 후 결과값을 json object로 받음
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success"); // 서버통신성공했음?
                            if(success) {
                                String userID = jsonObject.getString("userID");
                                String userPW = jsonObject.getString("userPW");
                                Toast.makeText(getApplicationContext(), "Log in Succeeded", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginLocal.this, MainActivity.class);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPW", userPW);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Log in Failed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                LoginRequest loginRequest = new LoginRequest(userID, userPW, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginLocal.this);
                queue.add(loginRequest);

            }
        });
    }
}
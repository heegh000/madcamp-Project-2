package com.example.fivepiratesgame.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.fivepiratesgame.CustomizeCharacter;
import com.example.fivepiratesgame.MainActivity;
import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.RetrofitService;
import com.example.fivepiratesgame.UserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {

    String BASE_URL = "http://172.10.5.52:443/";
    Retrofit retrofit;
    RetrofitService service;

    private EditText et_id, et_pw;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        service = retrofit.create(RetrofitService.class);

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
                Log.d("click", "register");

                Call<String> call_post = service.postUD("username", userID, userPW);
                call_post.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()){
                            try {
                                String result = response.body().toString();
                                Log.d("POST", result);
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        else {
                            Log.d("POST", "error = " + String.valueOf(response.code()) + response.errorBody());
                        }
                    }



                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    Log.d ("POST", "Fail " + t.getMessage());

                    }
                });

                //
//                Intent intent = new Intent(Register.this, CustomizeCharacter.class);
//                intent.putExtra("userID", userID);
//                startActivity(intent);

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
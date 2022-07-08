package com.example.fivepiratesgame.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.toolbox.Volley;
import com.example.fivepiratesgame.MainActivity;
import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginLocal extends AppCompatActivity {

    String BASE_URL = "http://172.10.5.52:443/";
    Retrofit retrofit;
    RetrofitService service;

    private EditText et_id, et_pw;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_local);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        service = retrofit.create(RetrofitService.class);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText에 현재 입력되어 있는 값 가져오기
                String userID = et_id.getText().toString();
                String userPW = et_pw.getText().toString();

                Log.d("click", "login");
                Call<String> call_post = service.postIN(userID, userPW);
                call_post.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {// 로그인 요청을 한 후 결과값 받음
                        if(response.isSuccessful()){ // 서버통신성공했음?
                            try {
                                String result = response.body().toString();
                                Log.d("POST success", result);
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        else {
                            Log.d("POST fail", "error = " + String.valueOf(response.code()) + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d ("POST on fail", "Fail " + t.getMessage());

                    }
                });


            }
        });
    }
}
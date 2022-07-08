package com.example.fivepiratesgame.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    private EditText et_id, et_pw, et_name;
    private AppCompatButton registerBtn;

    private LoginViewModel loginViewModel;

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

        loginViewModel = new LoginViewModel();

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        et_name = findViewById(R.id.et_name);
        registerBtn = findViewById(R.id.registerBtn);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                registerBtn.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    et_id.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    et_pw.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

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
                loginViewModel.loginDataChanged(et_id.getText().toString(),
                        et_pw.getText().toString());
            }
        };
        et_id.addTextChangedListener(afterTextChangedListener);
        et_pw.addTextChangedListener(afterTextChangedListener);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = et_id.getText().toString();
                String userPW = et_pw.getText().toString();
                String userName = et_name.getText().toString();

                Log.d("click", "register");
                Call<String> call_post = service.postUD(userID, userPW, userName);
                call_post.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {// 회원가입 요청을 한 후 결과값 받음
                        if(response.isSuccessful()){ // 서버통신성공했음?
                            try {
                                String result = response.body().toString();
                                Log.d("POST success", result);
                                loginViewModel.setLoginResult(new LoginResult(new LoggedInUserView(userID)));

                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                                startActivity(new Intent(getApplicationContext(),
                                        MainActivity.class).putExtra("userID", userID));
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        else {
                            loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                            Log.d("POST fail", "error = " + String.valueOf(response.code()) + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                        Log.d ("POST on fail", "Fail " + t.getMessage());

                    }
                });

            }
        });
    }
}
package com.example.fivepiratesgame.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fivepiratesgame.CustomizeCharacter;
import com.example.fivepiratesgame.MainActivity;
import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Register extends AppCompatActivity {

    private RetrofitService service;

    private EditText et_id, et_pw, et_name;
    private AppCompatButton registerBtn;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initRetrofit();

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
                String nickname = et_name.getText().toString();

                Log.d("click", "register");
                postSignUp(userID, userPW, nickname);
            }
        });
    }

    private void initRetrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        service = retrofit.create(RetrofitService.class);
    }

    private void postSignUp(String userID, String userPW, String nickname) {
        service.postSignUp(userID, userPW, nickname).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {// 회원가입 요청을 한 후 결과값 받음
                if(response.isSuccessful()){ // 서버통신성공했음?
                    try {
                        String result = response.body().toString();
                        Log.d("Sign Up success", result);
                        loginViewModel.setLoginResult(new LoginResult(new LoggedInUserView(userID)));

                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                        startActivity(new Intent(getApplicationContext(), CustomizeCharacter.class)
                                .putExtra("userID", userID)
                                .putExtra("nickname", nickname));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                    Log.d("Sign Up fail", "error = " + String.valueOf(response.code()) + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                Log.d ("Sign Up on fail", "Fail " + t.getMessage());

            }
        });
    }
}
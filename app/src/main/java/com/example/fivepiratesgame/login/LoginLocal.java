package com.example.fivepiratesgame.login;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private AppCompatButton loginBtn;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_local);

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        service = retrofit.create(RetrofitService.class);

        loginViewModel = new LoginViewModel();

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        loginBtn = findViewById(R.id.loginBtn);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginBtn.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    et_id.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    et_pw.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = et_id.getText().toString();
                String userPW = et_pw.getText().toString();

                Log.d("click", "login");
                Call<String> signIn = service.getSignIn(userID, userPW);
                signIn.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {// 로그인 요청을 한 후 결과값 받음
                        if(response.isSuccessful()){ // 서버통신성공했음?
                            try {
                                String result = response.body().toString();
                                Log.d("POST success", result);
                                loginViewModel.setLoginResult(new LoginResult(new LoggedInUserView(userID)));

                                //DB에서 ID로 username, avatarID, gold, ranking 받아오기!!!!
                                String userName = "John Doe";
                                int avatarID = 2;

                                startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                        .putExtra("userID", userID)
                                        .putExtra("userName", userName)
                                        .putExtra("avatarID", avatarID));
                                Log.d("Sign In success", result);

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        else {
                            loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                            Log.d("Sign In fail", "error = " + String.valueOf(response.code()) + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                        Log.d ("Sign In on fail", t.getMessage());

                    }
                });
            }
        });
    }
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

}
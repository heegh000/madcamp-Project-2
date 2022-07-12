package com.example.fivepiratesgame.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fivepiratesgame.MainActivity;
import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.RetrofitService;
import com.example.fivepiratesgame.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginIntro extends AppCompatActivity implements View.OnClickListener{

    private final long TIMEOUT = 1000;
    private long presstime = 0;
    private static int SIGN_IN = 100;

    private RetrofitService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initRetrofit();

        AppCompatButton googlelogin, login_local, newaccount;

        googlelogin = findViewById(R.id.googlelogin);
        login_local = findViewById(R.id.login);
        newaccount = findViewById(R.id.newaccount);

        googlelogin.setOnClickListener(this);
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
        else if(id == R.id.googlelogin) {
            GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(LoginIntro.this, gso);
            Intent intent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(intent, SIGN_IN);
        }

    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if (0 <= intervalTime && TIMEOUT >= intervalTime)
        {
            finish();
        }
        else
        {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void initRetrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        service = retrofit.create(RetrofitService.class);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String authCode = account.getServerAuthCode();

                String[] temp = account.getEmail().split("@");
                String userID = temp[0];
                Log.d("Google Id ", "BBBBBBBBBBBBBBBBBBBBBB");

                service.postGoogle(userID, account.getEmail(), account.getDisplayName()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()) {
                            Log.d("Google Id ", "AAAAAAAAAAAAAAAAAAAAAAAAAA");
                            try {
                                String result = response.body().toString();

                                if(result.equals("signup")) {
                                    startActivity(new Intent(getApplicationContext(), CustomizeCharacter.class)
                                            .putExtra("userID", userID)
                                            .putExtra("nickname", account.getDisplayName()));
                                }
                                else if(result.equals("success")) {


                                    service.getUserData(userID).enqueue(new Callback<UserData>() {
                                        @Override
                                        public void onResponse(Call<UserData> call, Response<UserData> response) {
                                            if(response.isSuccessful()) {
                                                //DB에서 ID로 username, avatarID, gold, ranking 받아오기!!!!
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
                                                Log.d("Sign In getUser fail", "error = " + String.valueOf(response.code()) + response.errorBody());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UserData> call, Throwable t) {
                                            Log.d ("Sign In getUser on fail", t.getMessage());
                                        }
                                    });
                                }
                                //실패시 토스트?
                                else if(result.equals("fail")) {
                                }

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Log.d("Google fail", "error = " + String.valueOf(response.code()) + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d ("Sign In on fail", t.getMessage());
                    }
                });

            }
            catch (Exception e) {

            }
        }


    }
}
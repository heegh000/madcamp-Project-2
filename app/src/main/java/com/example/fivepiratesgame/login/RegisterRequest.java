//package com.example.fivepiratesgame.login;
//
//import androidx.annotation.Nullable;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Response;
//import com.android.volley.toolbox.StringRequest;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class RegisterRequest extends StringRequest{
//    //서버 URL 설정 (PHP 파일 연동)
//    final static private String URL = "http://";
//    private Map<String, String> map;
//
//    public RegisterRequest(String userID, String userPW, Response.Listener<String> listener) {
//        super(Method.POST, URL, listener, null);
//
//        map = new HashMap<>();
//        map.put("userID", userID);
//        map.put("userPW", userPW);
//    }
//
//    @Nullable
//    @Override
//    protected Map<String, String> getPostParams() throws AuthFailureError {
//        return map;
//    }
//}

package com.example.fivepiratesgame;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("user_pw")
    private String user_pw;

    // @SerializedName으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함

    @SerializedName("nickname")
    private String nickname;

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함


    @Override
    public String toString() {

        return "UserData{" +
                "user_id='" + user_id + '\'' +
                ", user_pw='" + user_pw + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }

}

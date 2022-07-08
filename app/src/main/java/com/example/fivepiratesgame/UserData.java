package com.example.fivepiratesgame;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("user_pw")
    private String user_pw;

    // @SerializedName으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
    @SerializedName("user_name")
    private String user_name;

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "UserData {" +
                "name=" + user_name +
                ", id=" + user_id +
                ", pw=" + user_pw +
                "}";
    }


}

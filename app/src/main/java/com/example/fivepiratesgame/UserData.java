package com.example.fivepiratesgame;

import com.google.gson.annotations.SerializedName;

public class UserData {
    // @SerializedName으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    @SerializedName("pw")
    private String pw;
    public UserData(String name, String id, String pw) {
        this.name = name;
        this.id = id;
        this.pw = pw;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "UserData {" +
                "name=" + name +
                ", id=" + id +
                ", pw=" + pw +
                "}";
    }
}

package com.example.fivepiratesgame;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("userID")
    private String userID;

    @SerializedName("userPW")
    private String userPW;

    // @SerializedName으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
    @SerializedName("userName")
    private String userName;

    @SerializedName("userAvatar")
    private int userAvatar;

    @SerializedName("userCash")
    private int userCash;

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "UserData {" +
                "name=" + userName +
                ", id=" + userID +
                ", pw=" + userPW +
                "}";
    }
    public UserData(String userID, String userPW, String userName, int AvatarID, int cash){
        this.userID = userID;
        this.userPW = userPW;
        this.userName = userName;
        this.userAvatar = AvatarID;
        this.userCash = cash;
    }



}

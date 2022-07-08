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
    public int getAvatar(int AvatarID){
        switch (AvatarID) {
            case 0: return R.drawable.bulbasaur;
            case 1: return R.drawable.charmander;
            case 2: return R.drawable.egg;
            case 3: return R.drawable.jigglypuff;
            case 4: return R.drawable.meowth;
            case 5: return R.drawable.pikachu;
            case 6: return R.drawable.snorlex;
            default: return R.drawable.squirtle;
        }

    }


}

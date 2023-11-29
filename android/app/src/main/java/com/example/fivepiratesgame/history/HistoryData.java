package com.example.fivepiratesgame.history;

import com.google.gson.annotations.SerializedName;

public class HistoryData {

    @SerializedName("avatar")
    private int avatar;

    @SerializedName("time")
    private String time;

    @SerializedName("result")
    private String result;

    public HistoryData(int avatar, String date, String result) {
        this.avatar = avatar;
        this.time = date;
        this.result = result;
    }

    public int getAvatar() {return avatar;}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "HistoryData{" +
                "time='" + time + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}

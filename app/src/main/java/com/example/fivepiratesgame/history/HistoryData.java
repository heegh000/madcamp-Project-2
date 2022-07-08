package com.example.fivepiratesgame.history;

import com.google.gson.annotations.SerializedName;

public class HistoryData {

    @SerializedName("time")
    private String time;

    @SerializedName("result")
    private String result;

    public HistoryData(String date, String result) {
        this.time = date;
        this.result = result;
    }

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
        return "{" +
                "date: " + time +
                ", result: " + result +
                "}";
    }
}

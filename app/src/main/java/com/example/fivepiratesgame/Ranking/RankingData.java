package com.example.fivepiratesgame.Ranking;

import com.google.gson.annotations.SerializedName;

public class RankingData {
    @SerializedName("nickname")
    private String nickname;

    @SerializedName("gold")
    private String gold;

    public RankingData(String nickname, String gold) {
        this.nickname = nickname;
        this.gold = gold;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    @Override
    public String toString() {
        return "RankingData{" +
                "nickname='" + nickname + '\'' +
                ", gold='" + gold + '\'' +
                '}';
    }
}

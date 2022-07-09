package com.example.fivepiratesgame;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("avatar_id")
    private String avatarID;

    @SerializedName("gold")
    private String gold;

    @SerializedName("rank")
    private String rank;

    public String getRank() {
        return rank;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getAvatarID() {
        return Integer.parseInt(avatarID);
    }

    public String getGold() {
        return gold;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "nickname='" + nickname + '\'' +
                ", avatarID='" + avatarID + '\'' +
                ", gold='" + gold + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }
}

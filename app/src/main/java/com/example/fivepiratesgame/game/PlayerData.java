package com.example.fivepiratesgame.game;

public class PlayerData {

    private String userID;
    private int roomId;
    private String nickname;
    private int avatarID;
    private int order;
    private int gold = -1; // gold가 -1이면 화면에 표시되지 않음
    private int bringGold = 0;
    private int state = 1; // 0이면 죽음, 1이면 alive
    private int vote = -1; // -1이면 아직 투표 안함, 0이면 반대, 1이면 찬성

    public String getUserID() {
        return userID;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getNickname() {
        return nickname;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public int getOrder() {
        return order;
    }

    public int getGold() {
        return gold;
    }

    public int getBringGold() {
        return bringGold;
    }

    public int getState() {
        return state;
    }

    public int getVote() {
        return vote;
    }

    public void setBringGold(int bringGold) {
        this.bringGold = bringGold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public PlayerData(String userID, String nickname, int avatarID, int order) {
        this.userID = userID;
        this.nickname = nickname;
        this.avatarID = avatarID;
        this.order = order;
    }
}

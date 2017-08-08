package com.example.rashmitiwari.we_sync.model;

/**
 * Created by Rashmi on 6/24/2017.
 */

public class SyncBoard {
    private int skipTime;
    private String userId;
    private String roomId;

    public SyncBoard() {
    }

    public SyncBoard(int skipTime) {
        this.skipTime = skipTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getSkipTime() {
        return skipTime;
    }

    public void setSkipTime(int skipTime) {
        this.skipTime = skipTime;
    }
}

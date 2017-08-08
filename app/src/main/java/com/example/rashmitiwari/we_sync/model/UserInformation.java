package com.example.rashmitiwari.we_sync.model;

/**
 * Created by Rashmi on 7/13/2017.
 */

public class UserInformation {
    String userId;
    String email;
    String name;
    String roomId;

    public UserInformation() {

    }

    public UserInformation(String roomId) {
        this.roomId = roomId;
    }

    public UserInformation(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public UserInformation(String userId, String email, String name, String roomId) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}

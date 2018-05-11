package com.example.mohamed.roshita;

public class UserModel {

    private String userName ,email ,device_token;
    private boolean online;

    public UserModel(){}

    public String getUserName() {
        return userName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
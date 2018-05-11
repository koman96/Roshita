package com.example.mohamed.roshita;

public class OrderModel {

    private String medName ,senderId ,senderName ,senderAdd ,date;

    public OrderModel(String medName ,String senderId ,String senderName ,String senderAdd ,String date){
        this.medName = medName;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAdd = senderAdd;
        this.date = date;
    }

    public String getMedName() {
        return medName;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getDate() {
        return date;
    }

    public String getSenderAdd() {  return senderAdd;   }
}
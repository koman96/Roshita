package com.example.mohamed.roshita;

public class MyOrderModel {
    private String medName ,date;

    public MyOrderModel(String medName ,String date){
        this.medName = medName;
        this.date = date;
    }

    public String getMedName() {
        return medName;
    }

    public String getDate() {
        return date;
    }
}
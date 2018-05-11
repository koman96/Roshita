package com.example.mohamed.roshita;

public class MedInfo {
    String medName;
    String medPrice;

    public MedInfo(){}

    public MedInfo (String medName ,String medPrice){
        this.medName = medName;
        this.medPrice = medPrice;
    }

    public String getMedName() {
        return medName;
    }

    public String getMedPrice() {
        return medPrice;
    }
}
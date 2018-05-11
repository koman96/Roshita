package com.example.mohamed.roshita;

public class SearchObject {

    private String pharmName;
    private String pharmOwner;
    private String medPrice;

    public SearchObject(String pharmName ,String pharmOwner ,String medPrice){
        this.pharmName = pharmName;
        this.pharmOwner = pharmOwner;
        this.medPrice = medPrice;
    }

    public String getPharmName() {
        return pharmName;
    }

    public String getPharmOwner() {
        return pharmOwner;
    }

    public String getMedPrice() {
        return medPrice;
    }

    public void setPharmName(String pharmName) {
        this.pharmName = pharmName;
    }
}
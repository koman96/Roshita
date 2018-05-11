package com.example.mohamed.roshita;

public class SearchPharmModel {

    private String ownerId ,pharmName;
    private float distance;
    private double addLongitude ,addLatitude;

    public SearchPharmModel(String ownerId ,String pharmName ,double addLatitude ,double addLongitude ,float distance){
        this.ownerId = ownerId;
        this.pharmName = pharmName;
        this.addLatitude = addLatitude;
        this.addLongitude = addLongitude;
        this.distance = distance;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getPharmName() {
        return pharmName;
    }

    public double getAddLatitude() {
        return addLatitude;
    }

    public double getAddLongitude() {
        return addLongitude;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
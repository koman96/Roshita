package com.example.mohamed.roshita;

public class PharmModel {
    private String pharmName ,pharmPhone;
    private Boolean homeDelivery;
    private double addLongitude ,addLatitude;

    public PharmModel(){}

    public PharmModel (String pharmName ,String pharmPhone ,double addLongitude ,double addLatitude ,boolean homeDelivery){
        this.pharmName = pharmName;
        this.pharmPhone = pharmPhone;
        this.addLongitude = addLongitude;
        this.addLatitude = addLatitude;
        this.homeDelivery = homeDelivery;
    }

    public String getPharmName() {
        return pharmName;
    }

    public String getPharmPhone() {
        return pharmPhone;
    }

    public Boolean getHomeDelivery() {
        return homeDelivery;
    }

    public double getAddLongitude() {
        return addLongitude;
    }

    public double getAddLatitude() {
        return addLatitude;
    }
}
package com.sturrd.sturrd;

public class LatLngObject {

    public String name;
    public String profileImageUrl;
    public double Latitude;
    public double Longitude;

    public LatLngObject(){

    }

    public LatLngObject(String name, String profileImageUrl, double Latitude, double Longitude){
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }

}

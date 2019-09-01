package com.sturrd.sturrd.LocationRequest;

public class LocRequestObject {

    public String image = "";
    public String name = "";
    public String job = "";
    public String userId = "";

    public LocRequestObject() {

    }

    public LocRequestObject(String image, String name, String job, String userId) {

        this.image = image;
        this.name = name;
        this.job = job;
        this.userId = userId;

    }

    public String getJob() {
        return job;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }
}

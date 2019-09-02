package com.sturrd.sturrd.LocationRequest;

public class LocRequestObject {

    public String image = "";
    public String name = "";
    public String job = "";
    public String age = "";
    public String userId = "";

    public LocRequestObject() {

    }

    public LocRequestObject(String image, String name, String job, String age, String userId) {

        this.image = image;
        this.name = name;
        this.job = job;
        this.age = age;
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

    public String getAge() {
        return age;
    }

    public String getUserId() {
        return userId;
    }
}

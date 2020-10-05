package com.example.application.Retrieving_Data;

public class ProfileUserData {
    private String address, email, name, phone, position, profile_Url;
    private int distance;
    private boolean online;

    public ProfileUserData(){};

    public ProfileUserData(String address,int distance , String email, String name, boolean online, String phone, String position, String profile_Url) {
        this.address = address;
        this.distance = distance;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.position = position;
        this.online = online;
        this.profile_Url = profile_Url;
    }

    //      GET     //
    public String getProfile_Url() {
        return profile_Url;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPosition() {
        return position;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isOnline() {
        return online;
    }

    //      SET     //
    public void setProfile_Url(String profile_Url) {
        this.profile_Url = profile_Url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "ProfileUserData{" +
                "profile_Url='" + profile_Url + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", position='" + position + '\'' +
                ", distance=" + distance +
                ", online=" + online +
                '}';
    }
}

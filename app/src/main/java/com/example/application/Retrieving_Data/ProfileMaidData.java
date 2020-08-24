package com.example.application.Retrieving_Data;
public class ProfileMaidData {
    private String name, email, phone, position, address, number_identity, profile_Url, id_card_Url;
    private double latitude, longitude;
    private boolean verify_identity;

    public ProfileMaidData(){}

    public ProfileMaidData(String name, String email, String phone, String position, String address, String number_identity, String profile_Url, String id_card_Url, double latitude, double longitude, boolean verify_identity){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.address = address;
        this.number_identity = number_identity;
        this.profile_Url = profile_Url;
        this.id_card_Url = id_card_Url;
        this.latitude = latitude;
        this.longitude = longitude;
        this.verify_identity = verify_identity;
    }

    public String getName() {
        return name;
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

    public String getAddress() {
        return address;
    }

    public String getNumber_identity() {
        return number_identity;
    }

    public String getProfile_Url() {
        return profile_Url;
    }

    public String getId_card_Url() {
        return id_card_Url;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isVerify_identity() {
        return verify_identity;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNumber_identity(String number_identity) {
        this.number_identity = number_identity;
    }

    public void setProfile_Url(String profile_Url) {
        this.profile_Url = profile_Url;
    }

    public void setId_card_Url(String id_card_Url) {
        this.id_card_Url = id_card_Url;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setVerify_identity(boolean verify_identity) {
        this.verify_identity = verify_identity;
    }
}

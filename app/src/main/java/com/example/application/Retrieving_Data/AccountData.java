package com.example.application.Retrieving_Data;

public class AccountData {
    private String address, email, name, phone, position, profile_Url;

    public AccountData(){}

    public AccountData(String address, String email, String name, String phone, String position, String profile_Url) {
        this.address = address;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.position = position;
        this.profile_Url = profile_Url;
    }
    //          GET         //
    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPosition() {
        return position;
    }

    public String getProfile_Url() {
        return profile_Url;
    }

    //          SET         //
    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setProfile_Url(String profile_Url) {
        this.profile_Url = profile_Url;
    }

    @Override
    public String toString() {
        return "AccountData{" +
                "address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", position='" + position + '\'' +
                ", profile_Url='" + profile_Url + '\'' +
                '}';
    }
}

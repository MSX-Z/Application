package com.example.application.Retrieving_Data;

public class ProfileUserData {
    private String prfile_Url, name, address, email, phone;

    public ProfileUserData(){};

    public ProfileUserData(String prfile_Url, String name, String address, String email, String phone) {
        this.prfile_Url = prfile_Url;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }
    //      GET     //
    public String getPrfile_Url() {
        return prfile_Url;
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
    //      SET     //
    public void setPrfile_Url(String prfile_Url) {
        this.prfile_Url = prfile_Url;
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

    @Override
    public String toString() {
        return "ProfileUserData{" +
                "prfile_Url='" + prfile_Url + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

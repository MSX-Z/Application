package com.example.application.Retrieving_Data;

public class Post{
    private String desc, distance, email, name, post_Url, price,profile_Url, timestamp, title, uID;

    public Post (){ }

    public Post(String desc, String distance, String email, String name, String post_Url, String price, String profile_Url, String timestamp, String title, String uID) {
        this.desc = desc;
        this.distance = distance;
        this.email = email;
        this.name = name;
        this.post_Url = post_Url;
        this.price = price;
        this.profile_Url = profile_Url;
        this.timestamp = timestamp;
        this.title = title;
        this.uID = uID;
    }

    //      GET     //
    public String getDesc() {
        return desc;
    }

    public String getDistance() {
        return distance;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPost_Url() {
        return post_Url;
    }

    public String getPrice() {
        return price;
    }

    public String getProfile_Url() {
        return profile_Url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getuID() {
        return uID;
    }

    //      SET     //
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPost_Url(String post_Url) {
        this.post_Url = post_Url;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setProfile_Url(String profile_Url) {
        this.profile_Url = profile_Url;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    @Override
    public String toString() {
        return "Post{" +
                "desc='" + desc + '\'' +
                ", distance='" + distance + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", post_Url='" + post_Url + '\'' +
                ", price='" + price + '\'' +
                ", profile_Url='" + profile_Url + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", title='" + title + '\'' +
                ", uID='" + uID + '\'' +
                '}';
    }
}

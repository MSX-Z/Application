package com.example.application.Retrieving_Data;

public class Locations {
    private double latitude;
    private double longitude;

    public Locations(){};

    public Locations(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Locations{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

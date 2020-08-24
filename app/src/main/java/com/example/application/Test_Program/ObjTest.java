package com.example.application.Test_Program;

public class ObjTest {
    private String fname,lname;
    private double distance;
    private int age;
    private boolean status;

    public ObjTest(){};

    public ObjTest(String fname, String lname, int age, boolean status) {
        this.fname = fname;
        this.lname = lname;
        this.age = age;
        this.status = status;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public double getDistance() {
        return distance;
    }

    public int getAge() {
        return age;
    }

    public boolean isStatus() {
        return status;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

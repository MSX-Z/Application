package com.example.application.Retrieving_Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {
    private String address,name,phone,key;
    private boolean usability;

    public Address() {
    }

    public Address(String address, String name, String phone, String key, boolean usability) {
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.key = key;
        this.usability = usability;
    }

    protected Address(Parcel in) {
        address = in.readString();
        name = in.readString();
        phone = in.readString();
        key = in.readString();
        usability = in.readByte() != 0;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    //      GET     //
    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getKey() {
        return key;
    }

    public boolean isUsability() {
        return usability;
    }

    //      SET     //
    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUsability(boolean usability) {
        this.usability = usability;
    }

    @Override
    public String toString() {
        return "Address{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", key=" + key + '\'' +
                ", usability=" + usability +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(key);
        dest.writeByte((byte) (usability ? 1 : 0));
    }
}

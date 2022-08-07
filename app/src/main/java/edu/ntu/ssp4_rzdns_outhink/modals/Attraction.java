package edu.ntu.ssp4_rzdns_outhink.modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

public class Attraction implements Serializable, Parcelable {

    public String id;
    public String att_name;
    public String att_desc;
    public Double att_rating;
    public String att_tags;
    public HashMap<String,String> att_admin_rate;
    public HashMap<String,String> att_op_hr;
    public String att_address;
    public String att_url;
    public Double att_lat;
    public Double att_lng;
    public String photo_url;
    public double distance = 0;

    public Attraction(){};

    protected Attraction(Parcel in) {
        id = in.readString();
        att_name = in.readString();
        att_desc = in.readString();
        if (in.readByte() == 0) {
            att_rating = null;
        } else {
            att_rating = in.readDouble();
        }
        att_tags = in.readString();
        att_address = in.readString();
        att_url = in.readString();
        if (in.readByte() == 0) {
            att_lat = null;
        } else {
            att_lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            att_lng = null;
        } else {
            att_lng = in.readDouble();
        }
        photo_url = in.readString();
        distance = in.readDouble();
    }

    public static final Creator<Attraction> CREATOR = new Creator<Attraction>() {
        @Override
        public Attraction createFromParcel(Parcel in) {
            return new Attraction(in);
        }

        @Override
        public Attraction[] newArray(int size) {
            return new Attraction[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(att_name);
        parcel.writeString(att_desc);
        if (att_rating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(att_rating);
        }
        parcel.writeString(att_tags);
        parcel.writeString(att_address);
        parcel.writeString(att_url);
        if (att_lat == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(att_lat);
        }
        if (att_lng == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(att_lng);
        }
        parcel.writeString(photo_url);
        parcel.writeDouble(distance);
    }
}

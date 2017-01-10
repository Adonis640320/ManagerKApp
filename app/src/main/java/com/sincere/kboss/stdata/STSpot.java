package com.sincere.kboss.stdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 11/7/2016.
 */
public class STSpot implements Parcelable {
    public int f_id;
    public String f_name;
    public String f_address;
    public int f_progress;
    public String f_mainbuilding;
    public String f_buildcompany;
    public String f_contractdate;
    public String f_startdate;
    public String f_enddate;
    public int f_valid;
    public int f_connected;

    public STSpot() {}

    public STSpot(Parcel in) {
        f_id = in.readInt();
        f_name = in.readString();
        f_address = in.readString();
        f_progress = in.readInt();
        f_mainbuilding = in.readString();
        f_buildcompany = in.readString();
        f_contractdate = in.readString();
        f_startdate = in.readString();
        f_enddate = in.readString();
        f_valid = in.readInt();
        f_connected = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(f_id);
        dest.writeString(f_name);
        dest.writeString(f_address);
        dest.writeInt(f_progress);
        dest.writeString(f_mainbuilding);
        dest.writeString(f_buildcompany);
        dest.writeString(f_contractdate);
        dest.writeString(f_startdate);
        dest.writeString(f_enddate);
        dest.writeInt(f_valid);
        dest.writeInt(f_connected);
    }

    public static final Parcelable.Creator<STSpot> CREATOR
            = new Parcelable.Creator<STSpot>() {
        public STSpot createFromParcel(Parcel in) {
            return new STSpot(in);
        }

        public STSpot[] newArray(int size) {
            return new STSpot[size];
        }
    };
}

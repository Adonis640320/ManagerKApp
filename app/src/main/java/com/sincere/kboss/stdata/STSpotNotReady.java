package com.sincere.kboss.stdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 11/7/2016.
 */
public class STSpotNotReady implements Parcelable {
    public int f_id;
    public String f_name;
    public String f_address;
    public String f_manager;
    public String f_manager_phone;
    public int f_checked;
    public String f_feedback;
    public String f_feedback_date;
    public String f_regtime;

    public STSpotNotReady() {}

    public STSpotNotReady(Parcel in) {
        f_id = in.readInt();
        f_name = in.readString();
        f_address = in.readString();
        f_manager = in.readString();
        f_manager_phone = in.readString();
        f_checked = in.readInt();
        f_feedback = in.readString();
        f_feedback_date = in.readString();
        f_regtime = in.readString();
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
        dest.writeString(f_manager);
        dest.writeString(f_manager_phone);
        dest.writeInt(f_checked);
        dest.writeString(f_feedback);
        dest.writeString(f_feedback_date);
        dest.writeString(f_regtime);
    }

    public static final Creator<STSpotNotReady> CREATOR
            = new Creator<STSpotNotReady>() {
        public STSpotNotReady createFromParcel(Parcel in) {
            return new STSpotNotReady(in);
        }

        public STSpotNotReady[] newArray(int size) {
            return new STSpotNotReady[size];
        }
    };
}

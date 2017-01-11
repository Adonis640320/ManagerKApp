package com.sincere.kboss.stdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 2016.11.02.
 */
public class STJob implements Parcelable {
    public int f_id;
    public String f_detail;
    public String f_regtime;
    public int f_skill;
    public String f_workdate;
    public String f_worktime_start;
    public String f_worktime_end;
    public String f_address;
    public double f_longitude;
    public double f_latitude;
    public int f_payment;
    public int f_worker_count;
    public int f_spot_id;
    public int f_owner_id;
    public String f_owner_name;
    public int f_stat;
    public int f_automatch;

    //added by Adonis
    public String f_title;

    public STJob() {}

    public STJob(Parcel in) {
        f_id = in.readInt();
        f_detail = in.readString();
        f_regtime = in.readString();
        f_skill = in.readInt();
        f_workdate = in.readString();
        f_worktime_start = in.readString();
        f_worktime_end = in.readString();
        f_address = in.readString();
        f_longitude = in.readDouble();
        f_latitude = in.readDouble();
        f_payment = in.readInt();
        f_worker_count = in.readInt();
        f_spot_id = in.readInt();
        f_owner_id = in.readInt();
        f_owner_name = in.readString();
        f_stat = in.readInt();
        f_automatch = in.readInt();
        //added by Adonis
        f_title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(f_id);
        dest.writeString(f_detail);
        dest.writeString(f_regtime);
        dest.writeInt(f_skill);
        dest.writeString(f_workdate);
        dest.writeString(f_worktime_start);
        dest.writeString(f_worktime_end);
        dest.writeString(f_address);
        dest.writeDouble(f_longitude);
        dest.writeDouble(f_latitude);
        dest.writeInt(f_payment);
        dest.writeInt(f_worker_count);
        dest.writeInt(f_spot_id);
        dest.writeInt(f_owner_id);
        dest.writeString(f_owner_name);
        dest.writeInt(f_stat);
        dest.writeInt(f_automatch);
        //added by Adonis
        dest.writeString(f_title);
    }

    public static final Parcelable.Creator<STJob> CREATOR
            = new Parcelable.Creator<STJob>() {
        public STJob createFromParcel(Parcel in) {
            return new STJob(in);
        }

        public STJob[] newArray(int size) {
            return new STJob[size];
        }
    };
}

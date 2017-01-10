package com.sincere.kboss.stdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 11/13/2016.
 */
public class STJobManager implements Parcelable {
    public STJob job = new STJob();

    public int f_support_count;
    public int f_support_check_count;
    public int f_support_cancel_count;

    public STJobManager() {}

    public STJobManager(Parcel in) {
        job.f_id = in.readInt();
        job.f_detail = in.readString();
        job.f_regtime = in.readString();
        job.f_skill = in.readInt();
        job.f_workdate = in.readString();
        job.f_worktime_start = in.readString();
        job.f_worktime_end = in.readString();
        job.f_address = in.readString();
        job.f_longitude = in.readDouble();
        job.f_latitude = in.readDouble();
        job.f_payment = in.readInt();
        job.f_worker_count = in.readInt();
        job.f_spot_id = in.readInt();
        job.f_owner_id = in.readInt();
        job.f_stat = in.readInt();
        job.f_automatch = in.readInt();

        f_support_count = in.readInt();
        f_support_check_count = in.readInt();
        f_support_cancel_count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(job.f_id);
        dest.writeString(job.f_detail);
        dest.writeString(job.f_regtime);
        dest.writeInt(job.f_skill);
        dest.writeString(job.f_workdate);
        dest.writeString(job.f_worktime_start);
        dest.writeString(job.f_worktime_end);
        dest.writeString(job.f_address);
        dest.writeDouble(job.f_longitude);
        dest.writeDouble(job.f_latitude);
        dest.writeInt(job.f_payment);
        dest.writeInt(job.f_worker_count);
        dest.writeInt(job.f_spot_id);
        dest.writeInt(job.f_owner_id);
        dest.writeInt(job.f_stat);
        dest.writeInt(job.f_automatch);

        dest.writeInt(f_support_count);
        dest.writeInt(f_support_check_count);
        dest.writeInt(f_support_cancel_count);
    }

    public static final Parcelable.Creator<STJobManager> CREATOR
            = new Parcelable.Creator<STJobManager>() {
        public STJobManager createFromParcel(Parcel in) {
            return new STJobManager(in);
        }

        public STJobManager[] newArray(int size) {
            return new STJobManager[size];
        }
    };
}

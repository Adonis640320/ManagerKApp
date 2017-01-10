package com.sincere.kboss.stdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 2016.11.02.
 */
public class STSelectWorker implements Parcelable {
    public int f_spot_id;
    public int f_worker_id;
    public int f_job_id;
    public String f_photo;
    public String f_name;
    public int f_age;
    public int f_skills_year;
    public int f_ratings;
    public int f_support_check;
    public int f_support_cancel;
    public String f_signin_time;
    public int f_signin_check;
    public int f_signin_checked;
    public int f_signin_cancel;
    public String f_signout_time;
    public int f_signout_check;

    public String f_workdate;
    public int f_payment;

    public int f_elegancy_id;
    public int f_elegancy_checked;

    public int f_workamount_id;
    public int f_workamount_checked;

    public int f_worker_count;

    public String f_mphone;
    public int f_skill;

    public int f_is_favorite;
    public String f_together_workdate;

    public STSelectWorker() {}

    public String getRatingsString() {
        int count = f_ratings / 20;
        String ret = "";

        for (int i=0; i<count; i++) {
            ret += "*";
        }

        return ret;
    }

    public String getYearsString() {
        return String.format("만 %d세 | 경력 %d년", f_age, f_skills_year);
    }

    public STSelectWorker(Parcel in) {
        f_spot_id = in.readInt();
        f_worker_id = in.readInt();
        f_job_id = in.readInt();
        f_photo = in.readString();
        f_name = in.readString();
        f_age = in.readInt();
        f_skills_year = in.readInt();
        f_ratings = in.readInt();

        f_support_check = in.readInt();
        f_support_cancel = in.readInt();
        f_signin_time = in.readString();
        f_signin_check = in.readInt();
        f_signin_cancel = in.readInt();
        f_signin_checked = in.readInt();
        f_signout_time = in.readString();
        f_signout_check = in.readInt();

        f_workdate = in.readString();
        f_payment = in.readInt();

        f_elegancy_id = in.readInt();
        f_elegancy_checked = in.readInt();

        f_workamount_id = in.readInt();
        f_workamount_checked = in.readInt();

        f_worker_count = in.readInt();
        f_mphone = in.readString();
        f_skill = in.readInt();

        f_is_favorite = in.readInt();
        f_together_workdate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(f_spot_id);
        dest.writeInt(f_worker_id);
        dest.writeInt(f_job_id);
        dest.writeString(f_photo);
        dest.writeString(f_name);
        dest.writeInt(f_age);
        dest.writeInt(f_skills_year);
        dest.writeInt(f_ratings);

        dest.writeInt(f_support_check);
        dest.writeInt(f_support_cancel);
        dest.writeString(f_signin_time);
        dest.writeInt(f_signin_check);
        dest.writeInt(f_signin_cancel);
        dest.writeInt(f_signin_checked);
        dest.writeString(f_signout_time);
        dest.writeInt(f_signout_check);

        dest.writeString(f_workdate);
        dest.writeInt(f_payment);

        dest.writeInt(f_elegancy_id);
        dest.writeInt(f_elegancy_checked);

        dest.writeInt(f_workamount_id);
        dest.writeInt(f_workamount_checked);

        dest.writeInt(f_worker_count);
        dest.writeString(f_mphone);
        dest.writeInt(f_skill);

        dest.writeInt(f_is_favorite);
        dest.writeString(f_together_workdate);
    }

    public static final Parcelable.Creator<STSelectWorker> CREATOR
            = new Parcelable.Creator<STSelectWorker>() {
        public STSelectWorker createFromParcel(Parcel in) {
            return new STSelectWorker(in);
        }

        public STSelectWorker[] newArray(int size) {
            return new STSelectWorker[size];
        }
    };
}

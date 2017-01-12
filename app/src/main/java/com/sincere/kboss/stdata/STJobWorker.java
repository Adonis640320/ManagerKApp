package com.sincere.kboss.stdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 11/13/2016.
 */
public class STJobWorker implements Parcelable {
    public STJob job = new STJob();

    public int f_worker_id;
    public String f_spot_name;
    public String f_support_time;
    public int f_support_check;
    public int f_support_cancel;
    public String f_signin_time;
    public int f_signin_check;
    public int f_signin_cancel;
    public String f_signout_time;
    public int f_signout_check;
    public int f_workamount_checked;

    public String f_manager;
    public String f_mainbuilding;
    public String f_buildcompany;

    // added by Adonis
    public String f_spot_content;
    public String f_worker_name;
    public String f_worker_mphone;
    public String f_worker_citizen;
    public String f_owner_name;
    public String f_owner_mphone;
    public String f_worker_address;
    // public int f_stat; // 1: 모집중, 2:
    public String f_job_status; // 100: 모집완료 0:기타
    public STJobWorker() {}

    public STJobWorker(Parcel in) {
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

        f_worker_id = in.readInt();
        f_spot_name = in.readString();
        f_support_time = in.readString();
        f_support_check = in.readInt();
        f_support_cancel = in.readInt();
        f_signin_time = in.readString();
        f_signin_check = in.readInt();
        f_signin_cancel = in.readInt();
        f_signout_time = in.readString();
        f_signout_check = in.readInt();
        f_workamount_checked = in.readInt();

        f_manager = in.readString();
        f_mainbuilding = in.readString();
        f_buildcompany = in.readString();
        // added by Adonis
        f_spot_content = in.readString();
        f_worker_name = in.readString();
        f_worker_mphone = in.readString();
        f_worker_citizen = in.readString();
        f_owner_mphone = in.readString();
        f_worker_address = in.readString();
        f_job_status = in.readString();
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

        dest.writeInt(f_worker_id);
        dest.writeString(f_spot_name);
        dest.writeString(f_support_time);
        dest.writeInt(f_support_check);
        dest.writeInt(f_support_cancel);
        dest.writeString(f_signin_time);
        dest.writeInt(f_signin_check);
        dest.writeInt(f_signin_cancel);
        dest.writeString(f_signout_time);
        dest.writeInt(f_signout_check);
        dest.writeInt(f_workamount_checked);

        dest.writeString(f_manager);
        dest.writeString(f_mainbuilding);
        dest.writeString(f_buildcompany);
        // added by Adonis
        dest.writeString(f_spot_content);
        dest.writeString(f_worker_name);
        dest.writeString(f_worker_mphone);
        dest.writeString(f_worker_citizen);
        dest.writeString(f_owner_mphone);
        dest.writeString(f_worker_address);
        dest.writeString(f_job_status);
    }

    public static final Parcelable.Creator<STJobWorker> CREATOR
            = new Parcelable.Creator<STJobWorker>() {
        public STJobWorker createFromParcel(Parcel in) {
            return new STJobWorker(in);
        }

        public STJobWorker[] newArray(int size) {
            return new STJobWorker[size];
        }
    };
}

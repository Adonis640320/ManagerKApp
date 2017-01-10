package com.sincere.kboss.stdata;

import com.sincere.kboss.KbossApplication;

/**
 * Created by Michael on 10/27/2016.
 */
public class STUserInfo {
    public int f_id = 0;
    public String f_mphone = "";
    public String f_regtime = "0000-00-00 00:00:00";
    public String f_photo = "";
    public String f_name = "";
    public String f_birthday = "0000-00-00 00:00:00";
    public String f_email = "";
    public String f_rec_phone = "";
    public int f_gender = 1;
    public int f_level = 1;
    public String f_jobs = "";
    public int f_jobnotify = 0;
    public String f_cert_front = "";
    public String f_cert_front_date = "0000-00-00 00:00:00";
    public String f_cert_back = "";
    public String f_cert_back_date = "0000-00-00 00:00:00";
    public int f_pay_type = 1;
    public String f_bank_owner = "";
    public int f_bank_type = 1;
    public String f_bank_acct = "";
    public String f_bank_regdate = "0000-00-00 00:00:00";
    public String f_prefer_zone = "0";
    public String f_prefer_time = "0";
    public String f_intro = "";
    public String f_skills = "0";
    public int f_skills_year = 0;
    public String f_basicsec_cert = "";
    public String f_basicsec_date = "0000-00-00 00:00:00";
    public int f_valid = 1;
    public String f_logout_date = "0000-00-00 00:00:00";
    public int f_logout_reason = 0;
    public String f_authkey = "";

    public boolean minimumRequirement() {
        if (f_prefer_zone.equals("-1") || f_prefer_zone.isEmpty()) {
            return false;
        }

        if (f_prefer_time.equals("-1") || f_prefer_time.isEmpty()) {
            return false;
        }

        if (f_skills.isEmpty() || f_skills.equals("0")) {
            return false;
        }

        if (f_skills_year == 0) {
            return false;
        }

        if (f_cert_front_date.isEmpty() || f_cert_front_date.equals("0000-00-00 00:00:00")) {
            return false;
        }

        if (f_cert_back_date.isEmpty() || f_cert_back_date.equals("0000-00-00 00:00:00")) {
            return false;
        }

        if (f_basicsec_date.isEmpty() || f_basicsec_date.equals("0000-00-00 00:00:00")) {
            return false;
        }

        if (KbossApplication.g_userinfo.f_bank_acct.isEmpty()) {
            return false;
        }

        return true;
    }
}

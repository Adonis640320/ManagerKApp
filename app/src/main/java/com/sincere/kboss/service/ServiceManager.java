package com.sincere.kboss.service;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.manager.SelectWorkerActivity;
import com.sincere.kboss.stdata.STAppHistory;
import com.sincere.kboss.stdata.STBank;
import com.sincere.kboss.stdata.STBankType;
import com.sincere.kboss.stdata.STCert;
import com.sincere.kboss.stdata.STCompany;
import com.sincere.kboss.stdata.STContact;
import com.sincere.kboss.stdata.STFavoriteWorker;
import com.sincere.kboss.stdata.STJob;
import com.sincere.kboss.stdata.STJobHistory;
import com.sincere.kboss.stdata.STJobManager;
import com.sincere.kboss.stdata.STJobWorker;
import com.sincere.kboss.stdata.STLogoutReason;
import com.sincere.kboss.stdata.STNotice;
import com.sincere.kboss.stdata.STOwner;
import com.sincere.kboss.stdata.STPayType;
import com.sincere.kboss.stdata.STPoint;
import com.sincere.kboss.stdata.STSelectWorker;
import com.sincere.kboss.stdata.STSkill;
import com.sincere.kboss.stdata.STSpot;
import com.sincere.kboss.stdata.STSpotManager;
import com.sincere.kboss.stdata.STSpotNotReady;
import com.sincere.kboss.stdata.STSysParams;
import com.sincere.kboss.stdata.STUserInfo;
import com.sincere.kboss.stdata.STWeekday;
import com.sincere.kboss.stdata.STWorkAmount;
import com.sincere.kboss.stdata.STWorkHistory;
import com.sincere.kboss.stdata.STWorkingArea;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Michael on 2016.09.08.
 */
public class ServiceManager {
    public static ServiceManager inst = new ServiceManager();

    String addParam(String url, int param) {
        return (url + "/" + String.valueOf(param));
    }

    String addParam(String url, String param) {
        return (url + "/" + param);
    }

    public void getSysparams(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        // RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_SYSPARAMS;

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.get(url, null, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetSysParams(JSONObject jsonObject, ArrayList<STSysParams> sysparams)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STSysParams anItem = new STSysParams();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_splash_img = getSafeString(dataObject, ServiceParams.F_SPLASH_IMG);
                    anItem.f_splash_slogan = getSafeString(dataObject, ServiceParams.F_SPLASH_SLOGAN);
                    anItem.f_splash_delay = getSafeInt(dataObject, ServiceParams.F_SPLASH_DELAY);
                    anItem.f_authcode_time = getSafeInt(dataObject, ServiceParams.F_AUTHCODE_TIME);
                    anItem.f_license = getSafeString(dataObject, ServiceParams.F_LICENSE);
                    anItem.f_privacy = getSafeString(dataObject, ServiceParams.F_PRIVACY);
                    anItem.f_baseurl = getSafeString(dataObject, ServiceParams.F_BASEURL);
                    anItem.f_link_count = getSafeInt(dataObject, ServiceParams.F_LINK_COUNT);

                    sysparams.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void sendAuthReq(String phone, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SEND_AUTHREQ;
            param.add(ServiceParams.F_MPHONE, phone);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(String phone, String authcode, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_LOGIN;
            param.add(ServiceParams.F_MPHONE, phone);
            param.add(ServiceParams.F_AUTHCODE, authcode);
            param.add(ServiceParams.PARAM_TOKEN,KbossApplication.getToken());

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginsec(int memberid, String authkey, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_LOGIN_SEC;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(memberid));
            param.add(ServiceParams.F_AUTHKEY, authkey);
            param.add(ServiceParams.PARAM_TOKEN,KbossApplication.getToken());
            Log.e("test","loginsec token:"+KbossApplication.getToken());
            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseLogin(JSONObject jsonObject, ArrayList<STUserInfo> users)
    {
        RetVal retval = new RetVal();
        Log.e("test",jsonObject.toString());
        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STUserInfo anItem = new STUserInfo();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_mphone = getSafeString(dataObject, ServiceParams.F_MPHONE);
                    anItem.f_regtime = getSafeString(dataObject, ServiceParams.F_REGTIME);
                    anItem.f_photo = getSafeString(dataObject, ServiceParams.F_PHOTO);
                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);
                    anItem.f_birthday = getSafeString(dataObject, ServiceParams.F_BIRTHDAY) + " 00:00:00";
                    anItem.f_email = getSafeString(dataObject, ServiceParams.F_EMAIL);
                    anItem.f_rec_phone = getSafeString(dataObject, ServiceParams.F_REC_PHONE);
                    anItem.f_gender = getSafeInt(dataObject, ServiceParams.F_GENDER);
                    anItem.f_level = getSafeInt(dataObject, ServiceParams.F_LEVEL);
                    anItem.f_jobs = getSafeString(dataObject, ServiceParams.F_JOBS);
                    anItem.f_jobnotify = getSafeInt(dataObject, ServiceParams.F_JOBNOTIFY);
                    anItem.f_cert_front = getSafeString(dataObject, ServiceParams.F_CERT_FRONT);
                    anItem.f_cert_front_date = getSafeString(dataObject, ServiceParams.F_CERT_FRONT_DATE);
                    anItem.f_cert_back = getSafeString(dataObject, ServiceParams.F_CERT_BACK);
                    anItem.f_cert_back_date = getSafeString(dataObject, ServiceParams.F_CERT_BACK_DATE);
                    anItem.f_pay_type = getSafeInt(dataObject, ServiceParams.F_PAY_TYPE);
                    anItem.f_bank_owner = getSafeString(dataObject, ServiceParams.F_BANK_OWNER);
                    anItem.f_bank_type = getSafeInt(dataObject, ServiceParams.F_BANK_TYPE);
                    anItem.f_bank_acct = getSafeString(dataObject, ServiceParams.F_BANK_ACCT);
                    anItem.f_bank_regdate = getSafeString(dataObject, ServiceParams.F_BANK_REGDATE);
                    anItem.f_prefer_zone = getSafeString(dataObject, ServiceParams.F_PREFER_ZONE);
                    anItem.f_prefer_time = getSafeString(dataObject, ServiceParams.F_PREFER_TIME);
                    anItem.f_intro = getSafeString(dataObject, ServiceParams.F_INTRO);
                    anItem.f_skills = getSafeString(dataObject, ServiceParams.F_SKILLS);
                    anItem.f_skills_year = getSafeInt(dataObject, ServiceParams.F_SKILLS_YEAR);
                    anItem.f_basicsec_cert = getSafeString(dataObject, ServiceParams.F_BASICSEC_CERT);
                    anItem.f_basicsec_date = getSafeString(dataObject, ServiceParams.F_BASICSEC_DATE);
                    anItem.f_valid = getSafeInt(dataObject, ServiceParams.F_VALID);
                    anItem.f_logout_date = getSafeString(dataObject, ServiceParams.F_LOGOUT_DATE);
                    anItem.f_logout_reason = getSafeInt(dataObject, ServiceParams.F_LOGOUT_REASON);
                    anItem.f_authkey = getSafeString(dataObject, ServiceParams.F_AUTHKEY);

                    users.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void signup(String phone, String name, String email, String birthday, String rec_phone, int gender, String photoPath, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SIGNUP;
            param.add(ServiceParams.F_MPHONE, phone);
            param.add(ServiceParams.F_NAME, name);
            param.add(ServiceParams.F_EMAIL, email);
            param.add(ServiceParams.F_BIRTHDAY, birthday);
            param.add(ServiceParams.F_REC_PHONE, rec_phone);
            param.add(ServiceParams.F_GENDER, String.valueOf(gender));
            param.add(ServiceParams.PARAM_TOKEN, KbossApplication.getToken());

            if (! photoPath.isEmpty()) {
                File file = new File(photoPath);
                param.put(ServiceParams.F_PHOTO, file);
                param.add(ServiceParams.F_PHOTO, "photoexist");
            } else {
                param.add(ServiceParams.F_PHOTO, "nophoto");
            }

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseSignup(JSONObject jsonObject)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            KbossApplication.g_userinfo = new STUserInfo();

            String memberidstr = jsonObject.getString(ServiceParams.F_MEMBER_ID);
            if (memberidstr==null || memberidstr.isEmpty()) {
                KbossApplication.g_userinfo.f_id = 0;
            } else {
                KbossApplication.g_userinfo.f_id = Integer.parseInt(memberidstr);
            }

            KbossApplication.g_userinfo.f_authkey = jsonObject.getString(ServiceParams.F_AUTHKEY);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public RetVal parseNoData(JSONObject jsonObject)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void getWorkingArea(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_WORKING_AREA;

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetWorkingArea(JSONObject jsonObject, ArrayList<STWorkingArea> areas)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STWorkingArea anItem = new STWorkingArea();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_area = getSafeString(dataObject, ServiceParams.F_AREA);

                    areas.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void setWorkingArea(String area, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_WORKING_AREA;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_PREFER_ZONE, area);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWeekday(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_WEEKDAY;

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetWeekday(JSONObject jsonObject, ArrayList<STWeekday> weekdays)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STWeekday anItem = new STWeekday();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_weekday = getSafeString(dataObject, ServiceParams.F_WEEKDAY);

                    weekdays.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void setWeekday(String weekday, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_WEEKDAY;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_PREFER_TIME, weekday);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSkills(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_SKILLS;

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetSkills(JSONObject jsonObject, ArrayList<STSkill> skills)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STSkill anItem = new STSkill();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_title = getSafeString(dataObject, ServiceParams.F_TITLE);
                    anItem.f_level = getSafeInt(dataObject, ServiceParams.F_LEVEL);

                    skills.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void setSkills(String skills, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_SKILLS;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_SKILLS, skills);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWorkAmounts(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_WORKAMOUNTS;

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetWorkAmounts(JSONObject jsonObject, ArrayList<STWorkAmount> workamounts)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STWorkAmount anItem = new STWorkAmount();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_workamount = dataObject.getDouble(ServiceParams.F_WORKAMOUNT);

                    workamounts.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void setSkillsYear(int skillsyear, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_SKILLSYEAR;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_SKILLS_YEAR, String.valueOf(skillsyear));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteBasicSec(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_DELETE_BASICSEC;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBasicSec(String date, String photoPath, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_BASICSEC;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_BASICSEC_DATE, date);

            if (! photoPath.isEmpty()) {
                File file = new File(photoPath);
                param.put(ServiceParams.F_BASICSEC_CERT, file);
                param.add(ServiceParams.F_BASICSEC_CERT, "photoexist");
            } else {
                param.add(ServiceParams.F_BASICSEC_CERT, "nophoto");
            }

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCerts(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_CERT_REGHISTORY;

            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetCerts(JSONObject jsonObject, ArrayList<STCert> certs)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);
                    STCert anItem = new STCert();

                    anItem.f_cert = getSafeString(dataObject, ServiceParams.F_CERT);
                    anItem.f_cert_date = getSafeString(dataObject, ServiceParams.F_CERT_DATE);
                    anItem.f_cert_type = getSafeInt(dataObject, ServiceParams.F_CERT_TYPE);
                    certs.add(anItem);
                }

                if (certs.isEmpty()) {
                    retval.code = ServiceParams.ERR_EXCEPTION;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void deleteCertFront(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_DELETE_CERTFRONT;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCertFront(String photoPath, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_CERTFRONT;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            if (! photoPath.isEmpty()) {
                File file = new File(photoPath);
                param.put(ServiceParams.F_CERT_FRONT, file);
                param.add(ServiceParams.F_CERT_FRONT, "photoexist");
            } else {
                param.add(ServiceParams.F_CERT_FRONT, "nophoto");
            }

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCertBack(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_DELETE_CERTBACK;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCertBack(String photoPath, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_CERTBACK;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            if (! photoPath.isEmpty()) {
                File file = new File(photoPath);
                param.put(ServiceParams.F_CERT_BACK, file);
                param.add(ServiceParams.F_CERT_BACK, "photoexist");
            } else {
                param.add(ServiceParams.F_CERT_BACK, "nophoto");
            }

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPayTypes(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_PAYTYPES;

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetPayTypes(JSONObject jsonObject, ArrayList<STPayType> payTypes)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STPayType anItem = new STPayType();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);
                    anItem.f_subname = getSafeString(dataObject, ServiceParams.F_SUBNAME);
                    anItem.f_detail = getSafeString(dataObject, ServiceParams.F_DETAIL);
                    anItem.f_cancelled_percent = dataObject.getDouble(ServiceParams.F_CANCELLED_PERCENT);

                    payTypes.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void setPayType(String payType, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_PAYTYPE;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_PAY_TYPE, payType);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBanks(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_BANK_REGHISTORY;

            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetBanks(JSONObject jsonObject, ArrayList<STBank> banks)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);
                    STBank anItem = new STBank();
                    anItem.f_bank_type = getSafeInt(dataObject, ServiceParams.F_CERT_TYPE);
                    anItem.f_bank_owner = getSafeString(dataObject, ServiceParams.F_BANK_OWNER);
                    anItem.f_bank_acct = getSafeString(dataObject, ServiceParams.F_BANK_ACCT);
                    anItem.f_bank_regdate = getSafeString(dataObject, ServiceParams.F_BANK_REGDATE);
                    anItem.f_bank_history_type= getSafeInt(dataObject, ServiceParams.F_BANK_HISTORY_TYPE);
                    banks.add(anItem);
                }

                if (banks.isEmpty()) {
                    retval.code = ServiceParams.ERR_EXCEPTION;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void getBankTypes(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_BANKTYPES;

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetBankTypes(JSONObject jsonObject, ArrayList<STBankType> bankTypes)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STBankType anItem = new STBankType();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);

                    bankTypes.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void setBank(String owner, String banktype, String account, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_BANK;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_BANK_OWNER, owner);
            param.add(ServiceParams.F_BANK_TYPE, banktype);
            param.add(ServiceParams.F_BANK_ACCT, account);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIntro(String intro, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_INTRO;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_INTRO, intro);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editAccount(String phone, String name, String email, String birthday, int gender, String photoPath, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_EDIT_ACCOUNT;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_MPHONE, phone);
            param.add(ServiceParams.F_NAME, name);
            param.add(ServiceParams.F_EMAIL, email);
            param.add(ServiceParams.F_BIRTHDAY, birthday);
            param.add(ServiceParams.F_GENDER, String.valueOf(gender));

            if (! photoPath.isEmpty()) {
                File file = new File(photoPath);
                param.put(ServiceParams.F_PHOTO, file);
                param.add(ServiceParams.F_PHOTO, "photoexist");
            } else {
                param.add(ServiceParams.F_PHOTO, "nophoto");
            }

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLogoutReasons(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_LOGOUT_REASONS;

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetLogoutReasons(JSONObject jsonObject, ArrayList<STLogoutReason> reasons)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STLogoutReason anItem = new STLogoutReason();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_reason = getSafeString(dataObject, ServiceParams.F_REASON);

                    reasons.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void logout(String reason, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_LOGOUT;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_REASON, reason);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getJobs(String jobdate, int spot_id, int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_JOBS;

            param.add(ServiceParams.F_WORKDATE, jobdate);
            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            Log.e("test", "WorkDate:" + jobdate + "spotId:"+spot_id+" OwnerId:"+String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetJobs(JSONObject jsonObject, ArrayList<STJobManager> jobs)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STJobManager anItem = new STJobManager();

                    anItem.job.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.job.f_detail = getSafeString(dataObject, ServiceParams.F_DETAIL);
                    anItem.job.f_regtime = getSafeString(dataObject, ServiceParams.F_REGTIME);
                    anItem.job.f_skill = getSafeInt(dataObject, ServiceParams.F_SKILL);
                    anItem.job.f_workdate = getSafeString(dataObject, ServiceParams.F_WORKDATE);
                    anItem.job.f_worktime_start = getSafeString(dataObject, ServiceParams.F_WORKTIME_START);
                    anItem.job.f_worktime_end = getSafeString(dataObject, ServiceParams.F_WORKTIME_END);
                    anItem.job.f_address = getSafeString(dataObject, ServiceParams.F_ADDRESS);
                    anItem.job.f_longitude = dataObject.getDouble(ServiceParams.F_LONGITUDE);
                    anItem.job.f_latitude = dataObject.getDouble(ServiceParams.F_LATITUDE);
                    anItem.job.f_payment = getSafeInt(dataObject, ServiceParams.F_PAYMENT);
                    anItem.job.f_worker_count = getSafeInt(dataObject, ServiceParams.F_WORKER_COUNT);
                    anItem.job.f_owner_id = getSafeInt(dataObject, ServiceParams.F_OWNER_ID);
                    anItem.job.f_spot_id = getSafeInt(dataObject, ServiceParams.F_SPOT_ID);
                    anItem.job.f_stat = getSafeInt(dataObject, ServiceParams.F_STAT);
                    anItem.job.f_automatch = getSafeInt(dataObject, ServiceParams.F_AUTOMATCH);

                    anItem.f_support_count = getSafeInt(dataObject, ServiceParams.F_SUPPORT_COUNT);
                    anItem.f_support_check_count = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CHECK_COUNT);
                    anItem.f_support_cancel_count = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CANCEL_COUNT);

                    jobs.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void getJobsWorker(int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_JOBS_WORKER;

            param.add(ServiceParams.F_WORKER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSafeString(JSONObject jsonObject, String key) {
        try {
            String tmp = jsonObject.getString(key);
            if (tmp.equals("null")) {
                return "";
            }

            return tmp;
        } catch (Exception e) {
            return "";
        }
    }

    public int getSafeInt(JSONObject jsonObject, String key) {
        try {
            String tmp = jsonObject.getString(key);
            if (tmp.isEmpty() || tmp.equals("null")) {
                return 0;
            }

            return Integer.parseInt(tmp);
        } catch (Exception e) {
            return 0;
        }
    }

    public RetVal parseGetJobsWorker(JSONObject jsonObject, ArrayList<STJobWorker> workerjobs)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STJobWorker anItem = new STJobWorker();

                    anItem.job.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.job.f_detail = getSafeString(dataObject, ServiceParams.F_DETAIL);
                    anItem.job.f_regtime = getSafeString(dataObject, ServiceParams.F_REGTIME);
                    anItem.job.f_skill = getSafeInt(dataObject, ServiceParams.F_SKILL);
                    anItem.job.f_workdate = getSafeString(dataObject, ServiceParams.F_WORKDATE);
                    anItem.job.f_worktime_start = getSafeString(dataObject, ServiceParams.F_WORKTIME_START);
                    anItem.job.f_worktime_end = getSafeString(dataObject, ServiceParams.F_WORKTIME_END);
                    anItem.job.f_address = getSafeString(dataObject, ServiceParams.F_ADDRESS);
                    anItem.job.f_longitude = dataObject.getDouble(ServiceParams.F_LONGITUDE);
                    anItem.job.f_latitude = dataObject.getDouble(ServiceParams.F_LATITUDE);
                    anItem.job.f_payment = getSafeInt(dataObject, ServiceParams.F_PAYMENT);
                    anItem.job.f_worker_count = getSafeInt(dataObject, ServiceParams.F_WORKER_COUNT);
                    anItem.job.f_owner_id = getSafeInt(dataObject, ServiceParams.F_OWNER_ID);
                    anItem.job.f_owner_name = getSafeString(dataObject, ServiceParams.F_OWNER_NAME);
                    anItem.job.f_spot_id = getSafeInt(dataObject, ServiceParams.F_SPOT_ID);
                    anItem.job.f_stat = getSafeInt(dataObject, ServiceParams.F_STAT);
                    anItem.job.f_automatch = getSafeInt(dataObject, ServiceParams.F_AUTOMATCH);

                    anItem.f_worker_id = getSafeInt(dataObject, ServiceParams.F_WORKER_ID);
                    anItem.f_spot_name = getSafeString(dataObject, ServiceParams.F_SPOT_NAME);
                    anItem.f_support_time = getSafeString(dataObject, ServiceParams.F_SUPPORT_TIME);
                    anItem.f_support_check = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CHECK);
                    anItem.f_support_cancel = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CANCEL);
                    anItem.f_signin_time = getSafeString(dataObject, ServiceParams.F_SIGNIN_TIME);
                    anItem.f_signin_check = getSafeInt(dataObject, ServiceParams.F_SIGNIN_CHECK);
                    anItem.f_signin_cancel = getSafeInt(dataObject, ServiceParams.F_SIGNIN_CANCEL);
                    anItem.f_signout_time = getSafeString(dataObject, ServiceParams.F_SIGNOUT_TIME);
                    anItem.f_signout_check = getSafeInt(dataObject, ServiceParams.F_SIGNOUT_CHECK);

                    anItem.f_manager = getSafeString(dataObject, ServiceParams.F_MANAGER);
                    anItem.f_mainbuilding = getSafeString(dataObject, ServiceParams.F_MAINBUILDING);
                    anItem.f_buildcompany = getSafeString(dataObject, ServiceParams.F_BUILDCOMPANY);
                    anItem.f_spot_content = getSafeString(dataObject, ServiceParams.F_CONTENT);//added by Adonis
                    workerjobs.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }
/////////////////////////
public void workhistoryForJob(int jobid, AsyncHttpResponseHandler handler)
{
    String url = "";
    AsyncHttpClient client = new AsyncHttpClient();
    RequestParams param = new RequestParams();

    try {
        // make service url
        url = ServiceParams.API_GET_WORK_HISTORY_JOB;
        param.add(ServiceParams.F_WORKER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
        param.add(ServiceParams.F_JOB_ID, String.valueOf(jobid));

        // call get service
        client.setTimeout(ServiceParams.connectTimeout);
        client.post(url, param, handler);

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public void getRecommendOwner(int spotid,int memberid, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_RECOMMEND_OWNERS;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(memberid));
            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spotid));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parsegetRecommendOwner(JSONObject jsonObject, ArrayList<STSpotManager> histories)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STSpotManager anItem = new STSpotManager();

                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);
                    anItem.f_regTime = getSafeString(dataObject, ServiceParams.F_REGTIME);

                    histories.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }
    ////////////////////////////////////////////////
    public void workForJob(int jobid, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_WORK_FOR_JOB;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(jobid));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void getJobHistory(int jobid, int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_JOB_HISTORY;

            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(jobid));
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetJobHistory(JSONObject jsonObject, ArrayList<STJobWorker> histories)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STJobWorker anItem = new STJobWorker();

                    anItem.job.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.job.f_detail = getSafeString(dataObject, ServiceParams.F_DETAIL);
                    anItem.job.f_regtime = getSafeString(dataObject, ServiceParams.F_REGTIME);
                    anItem.job.f_skill = getSafeInt(dataObject, ServiceParams.F_SKILL);
                    anItem.job.f_workdate = getSafeString(dataObject, ServiceParams.F_WORKDATE);
                    anItem.job.f_worktime_start = getSafeString(dataObject, ServiceParams.F_WORKTIME_START);
                    anItem.job.f_worktime_end = getSafeString(dataObject, ServiceParams.F_WORKTIME_END);
                    anItem.job.f_address = getSafeString(dataObject, ServiceParams.F_ADDRESS);
                    anItem.job.f_longitude = dataObject.getDouble(ServiceParams.F_LONGITUDE);
                    anItem.job.f_latitude = dataObject.getDouble(ServiceParams.F_LATITUDE);
                    anItem.job.f_payment = getSafeInt(dataObject, ServiceParams.F_PAYMENT);
                    anItem.job.f_worker_count = getSafeInt(dataObject, ServiceParams.F_WORKER_COUNT);
                    anItem.job.f_owner_id = getSafeInt(dataObject, ServiceParams.F_OWNER_ID);
                    anItem.job.f_spot_id = getSafeInt(dataObject, ServiceParams.F_SPOT_ID);
                    anItem.job.f_stat = getSafeInt(dataObject, ServiceParams.F_STAT);
                    anItem.job.f_automatch = getSafeInt(dataObject, ServiceParams.F_AUTOMATCH);

                    anItem.f_worker_id = getSafeInt(dataObject, ServiceParams.F_WORKER_ID);
                    anItem.f_spot_name = getSafeString(dataObject, ServiceParams.F_SPOT_NAME);
                    anItem.f_support_time = getSafeString(dataObject, ServiceParams.F_SUPPORT_TIME);
                    anItem.f_support_check = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CHECK);
                    anItem.f_support_cancel = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CANCEL);
                    anItem.f_signin_time = getSafeString(dataObject, ServiceParams.F_SIGNIN_TIME);
                    anItem.f_signin_check = getSafeInt(dataObject, ServiceParams.F_SIGNIN_CHECK);
                    anItem.f_signin_cancel = getSafeInt(dataObject, ServiceParams.F_SIGNIN_CANCEL);
                    anItem.f_signout_time = getSafeString(dataObject, ServiceParams.F_SIGNOUT_TIME);
                    anItem.f_signout_check = getSafeInt(dataObject, ServiceParams.F_SIGNOUT_CHECK);

                    histories.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }*/

    public void getWorkHistory(int history_year, int history_month, int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_WORK_HISTORY;

            param.add(ServiceParams.F_WORKER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_HISTORY_YEAR, String.valueOf(history_year));
            param.add(ServiceParams.F_HISTORY_MONTH, String.valueOf(history_month));
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetWorkHistory(JSONObject jsonObject, ArrayList<STJobWorker> histories)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {

                //modified by Adonis

                JSONObject result = jsonObject.getJSONObject(ServiceParams.SVCC_DATA);
                JSONArray dataWorker = result.getJSONArray("worker");
                JSONObject dataWorkerInfo = dataWorker.getJSONObject(0);
                JSONArray dataOwner = result.getJSONArray("owner");
                for (int i=0; i<dataOwner.length(); i++) {
                    JSONObject dataObject = dataOwner.getJSONObject(i);

                    STJobWorker anItem = new STJobWorker();

                    anItem.job.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.job.f_detail = getSafeString(dataObject, ServiceParams.F_DETAIL);
                    anItem.job.f_regtime = getSafeString(dataObject, ServiceParams.F_REGTIME);
                    anItem.job.f_skill = getSafeInt(dataObject, ServiceParams.F_SKILL);
                    anItem.job.f_workdate = getSafeString(dataObject, ServiceParams.F_WORKDATE);
                    anItem.job.f_worktime_start = getSafeString(dataObject, ServiceParams.F_WORKTIME_START);
                    anItem.job.f_worktime_end = getSafeString(dataObject, ServiceParams.F_WORKTIME_END);
                    anItem.job.f_address = getSafeString(dataObject, ServiceParams.F_ADDRESS);
                    anItem.job.f_longitude = dataObject.getDouble(ServiceParams.F_LONGITUDE);
                    anItem.job.f_latitude = dataObject.getDouble(ServiceParams.F_LATITUDE);
                    anItem.job.f_payment = getSafeInt(dataObject, ServiceParams.F_PAYMENT);
                    anItem.job.f_worker_count = getSafeInt(dataObject, ServiceParams.F_WORKER_COUNT);
                    anItem.job.f_owner_id = getSafeInt(dataObject, ServiceParams.F_OWNER_ID);
                    anItem.job.f_owner_name = getSafeString(dataObject, ServiceParams.F_OWNER_NAME);
                    anItem.job.f_spot_id = getSafeInt(dataObject, ServiceParams.F_SPOT_ID);
                    anItem.job.f_stat = getSafeInt(dataObject, ServiceParams.F_STAT);
                    anItem.job.f_automatch = getSafeInt(dataObject, ServiceParams.F_AUTOMATCH);

                    anItem.f_worker_id = getSafeInt(dataObject, ServiceParams.F_WORKER_ID);
                    anItem.f_spot_name = getSafeString(dataObject, ServiceParams.F_SPOT_NAME);
                    anItem.f_support_time = getSafeString(dataObject, ServiceParams.F_SUPPORT_TIME);
                    anItem.f_support_check = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CHECK);
                    anItem.f_support_cancel = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CANCEL);
                    anItem.f_signin_time = getSafeString(dataObject, ServiceParams.F_SIGNIN_TIME);
                    anItem.f_signin_check = getSafeInt(dataObject, ServiceParams.F_SIGNIN_CHECK);
                    anItem.f_signin_cancel = getSafeInt(dataObject, ServiceParams.F_SIGNIN_CANCEL);
                    anItem.f_signout_time = getSafeString(dataObject, ServiceParams.F_SIGNOUT_TIME);
                    anItem.f_signout_check = getSafeInt(dataObject, ServiceParams.F_SIGNOUT_CHECK);
                    anItem.f_workamount_checked = getSafeInt(dataObject, ServiceParams.F_WORKAMOUNT_CHECKED);

                    anItem.f_manager = getSafeString(dataObject, ServiceParams.F_MANAGER);
                    anItem.f_mainbuilding = getSafeString(dataObject, ServiceParams.F_MAINBUILDING);
                    anItem.f_buildcompany = getSafeString(dataObject, ServiceParams.F_BUILDCOMPANY);

                    // added by Adonis
                    anItem.f_worker_name = getSafeString(dataWorkerInfo, "f_worker_name");
                    anItem.f_worker_mphone = getSafeString(dataWorkerInfo, "f_worker_mphone");
                    anItem.f_worker_citizen = getSafeString(dataWorkerInfo, "f_worker_citizen_id");
                    anItem.job.f_title = getSafeString(dataObject, "f_title");
                    anItem.f_owner_mphone = getSafeString(dataObject, "f_owner_phone");
                    anItem.f_worker_address = getSafeString(dataWorkerInfo, "f_worker_address");

                    histories.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void getNotPaidWorkHistoryCount(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_NOTPAID_WORK_HISTORY_COUNT;

            param.add(ServiceParams.F_WORKER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWorkHistoryCount(int history_year, int history_month, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_WORK_HISTORY_COUNT;

            param.add(ServiceParams.F_WORKER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_HISTORY_YEAR, String.valueOf(history_year));
            param.add(ServiceParams.F_HISTORY_MONTH, String.valueOf(history_month));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetWorkHistoryCount(JSONObject jsonObject)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                retval.intData = jsonObject.getInt(ServiceParams.SVCC_DATA);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void confirmSignIn(int jobid, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CONFIRM_SIGNIN;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(jobid));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelSignIn(int jobid, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CANCEL_SIGNIN;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(jobid));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getManagername(int owner_id, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_MANAGER;
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(owner_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void confirmSignOut(int jobid, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CONFIRM_SIGNOUT;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(jobid));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void confirmWork(int jobid, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CONFIRM_WORK;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(jobid));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void cancelWork(int jobid, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CANCEL_WORK;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(jobid));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelSupport(int jobid, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CANCEL_SUPPORT;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(jobid));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPoints(int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_POINTS;

            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetPoints(JSONObject jsonObject, ArrayList<STPoint> points)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STPoint anItem = new STPoint();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_member_id = getSafeInt(dataObject, ServiceParams.F_MEMBER_ID);
                    anItem.f_point_id = getSafeInt(dataObject, ServiceParams.F_POINT_ID);
                    anItem.f_title = getSafeString(dataObject, ServiceParams.F_TITLE);
                    anItem.f_datetime = getSafeString(dataObject, ServiceParams.F_DATETIME);
                    anItem.f_point = getSafeInt(dataObject, ServiceParams.F_POINT);

                    points.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void getNotices(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_NOTICES;

            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_LEVEL, String.valueOf(KbossApplication.g_userinfo.f_level));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendmeJobNotify(int jobnotify, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SENDME_JOBNOTIFY;

            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_JOBNOTIFY, String.valueOf(jobnotify));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAppHistory(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_APP_HISTORY;

            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetAppHistory(JSONObject jsonObject, ArrayList<STAppHistory> histories)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STAppHistory anItem = new STAppHistory();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);

                    histories.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void getPointSum(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_POINTSUM;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetPointSum(JSONObject jsonObject)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                try {
                    retval.intData = jsonObject.getInt(ServiceParams.F_POINTSUM);
                }catch(Exception e){}
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void getSpotsNotReady(int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_SPOTS_NOTREADY;

            param.add(ServiceParams.F_MPHONE, KbossApplication.g_userinfo.f_mphone);
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetSpotsNotReady(JSONObject jsonObject, ArrayList<STSpotNotReady> spots)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STSpotNotReady anItem = new STSpotNotReady();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);
                    anItem.f_address = getSafeString(dataObject, ServiceParams.F_ADDRESS);
                    anItem.f_manager = getSafeString(dataObject, ServiceParams.F_MANAGER);
                    anItem.f_manager_phone = getSafeString(dataObject, ServiceParams.F_MANAGER_PHONE);
                    anItem.f_checked = getSafeInt(dataObject, ServiceParams.F_CHECKED);
                    anItem.f_feedback = getSafeString(dataObject, ServiceParams.F_FEEDBACK);
                    anItem.f_feedback_date = getSafeString(dataObject, ServiceParams.F_FEEDBACK_DATE);
                    anItem.f_regtime = getSafeString(dataObject, ServiceParams.F_REGTIME);

                    spots.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void addSpotNotReady(String name, String address, String manager, String manager_phone, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_ADD_SPOT_NOTREADY;

            param.add(ServiceParams.F_NAME, name);
            param.add(ServiceParams.F_ADDRESS, address);
            param.add(ServiceParams.F_MANAGER, manager);
            param.add(ServiceParams.F_MANAGER_PHONE, manager_phone);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCompaniesNotReady(int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_COMPANIES_NOTREADY;

            param.add(ServiceParams.F_MPHONE, KbossApplication.g_userinfo.f_mphone);
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetCompaniesNotReady(JSONObject jsonObject, ArrayList<STCompany> companies)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STCompany anItem = new STCompany();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);
                    anItem.f_manager = getSafeString(dataObject, ServiceParams.F_MANAGER);
                    anItem.f_manager_phone = getSafeString(dataObject, ServiceParams.F_MANAGER_PHONE);
                    anItem.f_checked = getSafeInt(dataObject, ServiceParams.F_CHECKED);
                    anItem.f_feedback = getSafeString(dataObject, ServiceParams.F_FEEDBACK);
                    anItem.f_feedback_date = getSafeString(dataObject, ServiceParams.F_FEEDBACK_DATE);
                    anItem.f_regtime = getSafeString(dataObject, ServiceParams.F_REGTIME);

                    companies.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void addCompanyNotReady(String name, String manager, String manager_phone, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_ADD_COMPANY_NOTREADY;

            param.add(ServiceParams.F_NAME, name);
            param.add(ServiceParams.F_MANAGER, manager);
            param.add(ServiceParams.F_MANAGER_PHONE, manager_phone);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFavoriteWorkers(int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_FAVORITE_WORKERS;

            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFavoriteWorkersForAddManager(int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_FAVORITE_WORKERS_FOR_ADDMANAGER;

            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetFavoriteWorkers(JSONObject jsonObject, ArrayList<STFavoriteWorker> favoriteWorkers)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STFavoriteWorker anItem = new STFavoriteWorker();

                    anItem.f_worker_id = getSafeInt(dataObject, ServiceParams.F_WORKER_ID);
                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);
                    anItem.f_photo = getSafeString(dataObject, ServiceParams.F_PHOTO);
                    anItem.f_mphone = getSafeString(dataObject, ServiceParams.F_MPHONE);
                    anItem.f_level = getSafeInt(dataObject, ServiceParams.F_LEVEL);
                    anItem.f_regtime = getSafeString(dataObject, ServiceParams.F_REGTIME);
                    anItem.f_spot =  getSafeString(dataObject, ServiceParams.F_SPOT_NAME);
                    anItem.f_spot_id =  getSafeInt(dataObject, ServiceParams.F_SPOT_ID);
                    anItem.f_is_manager = getSafeInt(dataObject, ServiceParams.F_IS_MANAGER);
                    favoriteWorkers.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void isFavoriteWorker(int spot_id, int worker_id, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_IS_FAVORITE_WORKER;

            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_WORKER_ID, String.valueOf(worker_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFavoriteWorker(int spot_id,int worker_id, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_ADD_FAVORITE_WORKER;

            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_WORKER_ID, String.valueOf(worker_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFavoriteWorker(int spot_id,int worker_id, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_REMOVE_FAVORITE_WORKER;

            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_WORKER_ID, String.valueOf(worker_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modifyWorkInfo(int job_id, int worker_id, int elegancy_id,int workamount_id, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_MODIFY_WORK_INFO;

            param.add(ServiceParams.F_JOB_ID, String.valueOf(job_id));
            param.add(ServiceParams.F_WORKER_ID, String.valueOf(worker_id));
            param.add(ServiceParams.F_ELEGANCY_ID, String.valueOf(elegancy_id));
            param.add(ServiceParams.F_WORKAMOUNT_ID, String.valueOf(workamount_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recommendKboss(String phone, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_RECOMMEND_KBOSS;

            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_NAME, KbossApplication.g_userinfo.f_name);
            param.add(ServiceParams.F_MPHONE, phone);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkKbossMember(ArrayList<STContact> contacts, AsyncHttpResponseHandler handler)
    {
        String url = "";
        String mphone_group = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CHECK_KBOSS_MEMBER;

            for (int i=0; i<contacts.size(); i++) {
                mphone_group += (i==0?"":",") + contacts.get(i).f_phone;
            }

            param.add(ServiceParams.F_MPHONE_GROUP, mphone_group);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseCheckKbossMember(JSONObject jsonObject, ArrayList<STContact> contacts)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    String mphone = dataArray.getString(i);

                    for (int k=0; k<contacts.size(); k++) {
                        if (contacts.get(k).f_phone.equals(mphone)) {
                            contacts.get(k).f_is_member = true;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            try {
                JSONObject dataObject = jsonObject.getJSONObject(ServiceParams.SVCC_DATA);

                String mphone = getSafeString(dataObject, "1");

                for (int k=0; k<contacts.size(); k++) {
                    if (contacts.get(k).f_phone.equals(mphone)) {
                        contacts.get(k).f_is_member = true;
                    }
                }

            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        }

        return retval;
    }

    public void checkOwner(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CHECK_OWNER;

            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseCheckOwner(JSONObject jsonObject, ArrayList<STSpot> spots)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataList = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                STSpot anItem = new STSpot();
                JSONObject dataObject = dataList.getJSONObject(0);

                anItem.f_id = getSafeInt(dataObject, ServiceParams.F_SPOT_ID);
                anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);

                spots.add(anItem);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void addOwner(String phone, int spot_id, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_ADD_OWNER;

            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_MPHONE, phone);
            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSpots(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_SPOTS;

            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetSpots(JSONObject jsonObject, ArrayList<STSpot> spots, boolean filterConnected)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    int connected = getSafeInt(dataObject, ServiceParams.F_CONNECTED);
                    int valid = getSafeInt(dataObject, ServiceParams.F_VALID);
                    if (filterConnected && (connected==0 || valid==0)) {
                        continue;
                    }

                    STSpot anItem = new STSpot();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);
                    anItem.f_address = getSafeString(dataObject, ServiceParams.F_ADDRESS);
                    anItem.f_progress = getSafeInt(dataObject, ServiceParams.F_PROGRESS);
                    anItem.f_mainbuilding = getSafeString(dataObject, ServiceParams.F_MAINBUILDING);
                    anItem.f_buildcompany = getSafeString(dataObject, ServiceParams.F_BUILDCOMPANY);
                    anItem.f_contractdate = getSafeString(dataObject, ServiceParams.F_CONTRACTDATE);
                    anItem.f_startdate = getSafeString(dataObject, ServiceParams.F_STARTDATE);
                    anItem.f_enddate = getSafeString(dataObject, ServiceParams.F_ENDDATE);
                    anItem.f_valid = valid;
                    anItem.f_connected = connected;

                    spots.add(anItem);
                }

                if (spots.isEmpty()) {
                    retval.code = ServiceParams.ERR_EXCEPTION;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void connectSpot(int spot_id, int connected, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CONNECT_SPOT;

            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_CONNECTED, String.valueOf(connected));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downToWorker(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_DOWN_TO_WORKER;

            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getOwners(int spot_id, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_OWNERS;

            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetOwners(JSONObject jsonObject, ArrayList<STOwner> owners)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STOwner anItem = new STOwner();

                    anItem.f_id = getSafeInt(dataObject, ServiceParams.F_ID);
                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);
                    anItem.f_mphone = getSafeString(dataObject, ServiceParams.F_MPHONE);
                    anItem.f_photo = getSafeString(dataObject, ServiceParams.F_PHOTO);
                    anItem.f_spot_id = getSafeInt(dataObject, ServiceParams.F_SPOT_ID);
                    anItem.f_connected = getSafeInt(dataObject, ServiceParams.F_CONNECTED);
                    anItem.f_recommend_id = getSafeInt(dataObject, ServiceParams.F_RECOMMEND_ID);

                    owners.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public void addJob(int spot_id, String jobdate, String worktime_start, String worktime_end,
                       int skillid, String jobdetail, String workercount, String payment,
                       int automatch, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_ADD_JOB;

            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_WORKDATE, jobdate);
            param.add(ServiceParams.F_WORKTIME_START, worktime_start);
            param.add(ServiceParams.F_WORKTIME_END, worktime_end);
            param.add(ServiceParams.F_SKILL, String.valueOf(skillid));
            param.add(ServiceParams.F_DETAIL, jobdetail);
            param.add(ServiceParams.F_WORKER_COUNT, workercount);
            param.add(ServiceParams.F_PAYMENT, payment);
            param.add(ServiceParams.F_AUTOMATCH, String.valueOf(automatch));
            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editJob(int spot_id, int job_id, String jobdate, String worktime_start, String worktime_end,
                       int skillid, String jobdetail, String workercount, String payment,
                       int automatch, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_EDIT_JOB;

            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(job_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_WORKDATE, jobdate);
            param.add(ServiceParams.F_WORKTIME_START, worktime_start);
            param.add(ServiceParams.F_WORKTIME_END, worktime_end);
            param.add(ServiceParams.F_SKILL, String.valueOf(skillid));
            param.add(ServiceParams.F_DETAIL, jobdetail);
            param.add(ServiceParams.F_WORKER_COUNT, workercount);
            param.add(ServiceParams.F_PAYMENT, payment);
            param.add(ServiceParams.F_AUTOMATCH, String.valueOf(automatch));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelJob(int spot_id, int job_id, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CANCEL_JOB;

            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_JOB_ID, String.valueOf(job_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWorkerCandidates(int job_id, int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_WORKER_CANDIDATES;

            param.add(ServiceParams.F_JOB_ID, String.valueOf(job_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWorkerHistories(int spot_id, String workdate, int pageno, int pagesize, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_GET_WORKER_HISTORIES;

            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_OWNER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.F_WORKDATE, workdate);
            param.add(ServiceParams.F_PAGE_NO, String.valueOf(pageno));
            param.add(ServiceParams.F_PAGE_SIZE, String.valueOf(pagesize));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetVal parseGetNotices(JSONObject jsonObject, ArrayList<STNotice> notices)
    {
        RetVal retval = new RetVal();
        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);
                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);
                    STNotice notice = new STNotice();
                    notice.f_title = getSafeString(dataObject, ServiceParams.F_TITLE);
                    notice.f_date = getSafeString(dataObject, "f_date");
                    notice.f_content = getSafeString(dataObject, "f_content");
                    notices.add(notice);
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return retval;
    }

    public RetVal parseGetWorkerHistories(JSONObject jsonObject, ArrayList<STSelectWorker> workers)
    {
        return parseGetWorkerCandidates(jsonObject, workers);
    }

    public RetVal parseGetWorkerCandidates(JSONObject jsonObject, ArrayList<STSelectWorker> workers)
    {
        RetVal retval = new RetVal();

        try {
            retval.code = jsonObject.getInt(ServiceParams.SVCC_RET);
            retval.msg = jsonObject.getString(ServiceParams.SVCC_RETMSG);

            if (retval.code == ServiceParams.ERR_NONE) {
                JSONArray dataArray = jsonObject.getJSONArray(ServiceParams.SVCC_DATA);

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    STSelectWorker anItem = new STSelectWorker();

                    anItem.f_worker_id = getSafeInt(dataObject, ServiceParams.F_WORKER_ID);
                    anItem.f_job_id = getSafeInt(dataObject, ServiceParams.F_JOB_ID);
                    anItem.f_worker_count = getSafeInt(dataObject, ServiceParams.F_WORKER_COUNT);
                    anItem.f_name = getSafeString(dataObject, ServiceParams.F_NAME);
                    anItem.f_photo = getSafeString(dataObject, ServiceParams.F_PHOTO);
                    anItem.f_age = getSafeInt(dataObject, ServiceParams.F_AGE);
                    anItem.f_mphone = getSafeString(dataObject, ServiceParams.F_MPHONE);
                    anItem.f_skill = getSafeInt(dataObject, ServiceParams.F_SKILL);
                    anItem.f_skills_year = getSafeInt(dataObject, ServiceParams.F_SKILLS_YEAR);
                    anItem.f_ratings = getSafeInt(dataObject, ServiceParams.F_RATINGS);
                    anItem.f_support_check = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CHECK);
                    anItem.f_support_cancel = getSafeInt(dataObject, ServiceParams.F_SUPPORT_CANCEL);
                    anItem.f_signin_time = getSafeString(dataObject, ServiceParams.F_SIGNIN_TIME);
                    anItem.f_signin_check = getSafeInt(dataObject, ServiceParams.F_SIGNIN_CHECK);
                    anItem.f_signin_checked = getSafeInt(dataObject, ServiceParams.F_SIGNIN_CHECKED);
                    anItem.f_signin_cancel = getSafeInt(dataObject, ServiceParams.F_SIGNIN_CANCEL);
                    anItem.f_signout_time = getSafeString(dataObject, ServiceParams.F_SIGNOUT_TIME);
                    anItem.f_signout_check = getSafeInt(dataObject, ServiceParams.F_SIGNOUT_CHECK);
                    anItem.f_elegancy_id = getSafeInt(dataObject, ServiceParams.F_ELEGANCY_ID);
                    anItem.f_elegancy_checked = getSafeInt(dataObject, ServiceParams.F_ELEGANCY_CHECKED);
                    anItem.f_workamount_id = getSafeInt(dataObject, ServiceParams.F_WORKAMOUNT_ID);
                    anItem.f_workamount_checked = getSafeInt(dataObject, ServiceParams.F_WORKAMOUNT_CHECKED);

                    anItem.f_workdate = getSafeString(dataObject, ServiceParams.F_WORKDATE);
                    anItem.f_payment = getSafeInt(dataObject, ServiceParams.F_PAYMENT);

                    anItem.f_is_favorite = getSafeInt(dataObject, ServiceParams.F_IS_FAVORITE);
                    anItem.f_together_workdate = getSafeString(dataObject, ServiceParams.F_TOGETHER_WORKDATE);

                    workers.add(anItem);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }



    public void selectWorkers(int job_id, String worker_ids, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SELECT_WORKERS;

            param.add(ServiceParams.F_JOB_ID, String.valueOf(job_id));
            param.add(ServiceParams.F_WORKER_IDS, worker_ids);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void checkWorkersSignin(int spot_id,String job_ids, String worker_ids, String stats, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_CHECK_WORKERS_SIGNIN;

            param.add(ServiceParams.F_SPOT_ID, String.valueOf(spot_id));
            param.add(ServiceParams.F_JOB_IDS, job_ids);
            param.add(ServiceParams.F_WORKER_IDS, worker_ids);
            param.add(ServiceParams.F_SIGNIN_STATS, stats);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWorkersElegancies(String job_ids, String worker_ids, String elegancies, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_WORKERS_ELEGANCIES;

            param.add(ServiceParams.F_JOB_IDS, job_ids);
            param.add(ServiceParams.F_WORKER_IDS, worker_ids);
            param.add(ServiceParams.F_ELEGANCY_IDS, elegancies);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWorkAmounts(String job_ids, String worker_ids, String workamounts, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_WORKAMOUNTS;

            param.add(ServiceParams.F_JOB_IDS, job_ids);
            param.add(ServiceParams.F_WORKER_IDS, worker_ids);
            param.add(ServiceParams.F_WORKAMOUNT_IDS, workamounts);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setToken(String token, AsyncHttpResponseHandler handler)
    {
        String url = "";
        SyncHttpClient client = new SyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_SET_TOKEN;
            param.add(ServiceParams.F_MEMBER_ID, String.valueOf(KbossApplication.g_userinfo.f_id));
            param.add(ServiceParams.PARAM_TOKEN, token);

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);
            Log.e("test","setToken memberid"+String.valueOf(KbossApplication.g_userinfo.f_id)+" token:");
        } catch (Exception e) {
            Log.e("test","setToken Exception:"+e.getLocalizedMessage());
           // e.printStackTrace();
        }
    }
    public void requestNextdayWork(int job_id, int worker_id, AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            // make service url
            url = ServiceParams.API_REQUEST_NEXTDAYDAY_WORK;

            param.add(ServiceParams.F_JOB_ID, String.valueOf(job_id));
            param.add(ServiceParams.F_WORKER_ID, String.valueOf(worker_id));

            // call get service
            client.setTimeout(ServiceParams.connectTimeout);
            client.post(url, param, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

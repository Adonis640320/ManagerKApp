package com.sincere.kboss;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sincere.kboss.stdata.STBankType;
import com.sincere.kboss.stdata.STLogoutReason;
import com.sincere.kboss.stdata.STPayType;
import com.sincere.kboss.stdata.STSkill;
import com.sincere.kboss.stdata.STSysParams;
import com.sincere.kboss.stdata.STUserInfo;
import com.sincere.kboss.stdata.STWeekday;
import com.sincere.kboss.stdata.STWorkAmount;
import com.sincere.kboss.stdata.STWorkingArea;

import java.util.ArrayList;


/**
 * Created by Michael on 2016.10.25.
 */
public class KbossApplication extends Application {
    private static KbossApplication appInstance;

    public static boolean UI_TEST = false;

    public static STSysParams g_sysparams = null;
    public static STUserInfo g_userinfo = null;
    public static ArrayList<STWorkingArea> g_areas = new ArrayList<>();
    public static ArrayList<STWeekday> g_weekdays = new ArrayList<>();
    public static ArrayList<STSkill> g_skills = new ArrayList<>();
    public static ArrayList<STPayType> g_paytypes = new ArrayList<>();
    public static ArrayList<STBankType> g_banktypes = new ArrayList<>();
    public static ArrayList<STLogoutReason> g_reasons = new ArrayList<>();
    public static ArrayList<STWorkAmount> g_workamounts = new ArrayList<>();

    public static String g_mphone = "";

    public static Bitmap g_user_photo = null;
    public static Bitmap g_basicsec_img = null;
    public static Bitmap g_certfront_img = null;
    public static Bitmap g_certback_img = null;

    public static boolean g_usersignout=false;
    public static ImageLoader g_imageLoader = ImageLoader.getInstance();
    public static DisplayImageOptions g_imageLoaderOptions;

    @Override
    public void onCreate() {
        super.onCreate();

        g_imageLoaderOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.photo)
                .showImageOnFail(R.drawable.photo)
                .showImageOnLoading(R.drawable.photo)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        appInstance = this;
    }

    public static KbossApplication getInstance(){
        return appInstance;
    }

    public static String getToken()
    {
        return KbossApplication.getInstance().getSharedPreferencesData("fcm_token","");
    }

    public static void setToken(String token)
    {
        if(token != null && token != "") {
            KbossApplication.getInstance().setSharedPreferencesData("fcm_token", token);

        }
    }


    /**
     * @brief sharedpreperence 데이터
     */
    public void setSharedPreferencesData(String key, boolean value) {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setSharedPreferencesData(String key, String value) {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setSharedPreferencesData(String key, int value) {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public boolean getSharedPreferencesData(String key, boolean defaultKey){
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        return prefs.getBoolean(key, defaultKey);
    }

    public String getSharedPreferencesData(String key, String defaultKey){
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        return prefs.getString(key, defaultKey);
    }

    public int getSharedPreferencesData(String key, int defaultKey){
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        return prefs.getInt(key, defaultKey);
    }
}

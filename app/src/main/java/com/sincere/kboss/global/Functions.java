package com.sincere.kboss.global;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.stdata.STBankType;
import com.sincere.kboss.stdata.STContact;
import com.sincere.kboss.stdata.STPayType;
import com.sincere.kboss.stdata.STSkill;
import com.sincere.kboss.stdata.STUserInfo;
import com.sincere.kboss.stdata.STWeekday;
import com.sincere.kboss.stdata.STWorkingArea;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Exchanger;
import java.util.regex.Pattern;

/**
 * Created by Michael on 2016.10.25.
 */
public class Functions {
    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static Toast gToast = null;
    public static void showToast(Context ctx, String message) {
        if (gToast == null) {
            gToast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
        }
        if (!gToast.getView().isShown()) {
            gToast.setText(message);
            gToast.show();
        }
    }

    public static void showToast(Context ctx, int res) {
        showToast(ctx, ctx.getString(res));
    }

    public static void showToastLong(Context ctx, String message) {
        if (gToast == null) {
            gToast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
        }
        if (!gToast.getView().isShown()) {
            gToast.setText(message);
            gToast.show();
        }
    }

    public static void showToastLong(Context ctx, int res) {
        showToastLong(ctx, ctx.getString(res));
    }

    public static String getAreaDisplayString(String dbString, Context ctx) {
        String[] areas = dbString.split(",");
        String ret = "";
        for (int i=0; i<areas.length; i++) {
            int areaid = Integer.parseInt(areas[i]);

            if (areaid == -1) {
                ret = ctx.getString(R.string.personalinfosetting_noexisting);
                break;
            }
            else if ( areaid==0 )
            {
                ret="전국";
                break;
            }

            for (int j=0; j<KbossApplication.g_areas.size(); j++) {
                STWorkingArea areast = KbossApplication.g_areas.get(j);

                if (areast.f_id == areaid) {
                    ret += areast.f_area + ", ";
                    break;
                }
            }
        }

        if (!ret.isEmpty() && ret.charAt(ret.length()-1)==' ') {
            ret = ret.substring(0, ret.length()-2);
        }

        return ret;
    }

    public static String getWeekdayDisplayString(String dbString, Context ctx) {
        String[] weekdays = dbString.split(",");
        String ret = "";
        for (int i=0; i<weekdays.length; i++) {
            int weekdayid = Integer.parseInt(weekdays[i]);

            if (weekdayid == -1) {
                ret = ctx.getString(R.string.personalinfosetting_noexisting);
                break;
            }
            if (weekdayid == 0) {
                ret = "모든 요일";
                break;
            }

            for (int j=0; j<KbossApplication.g_weekdays.size(); j++) {
                STWeekday weekdayst = KbossApplication.g_weekdays.get(j);

                if (weekdayst.f_id == weekdayid) {
                    ret += weekdayst.f_weekday + ", ";
                    break;
                }
            }
        }

        if (!ret.isEmpty() && ret.charAt(ret.length()-1)==' ') {
            ret = ret.substring(0, ret.length()-2);
        }

        return ret;
    }

    public static String getJobsString(String dbString, Context ctx) {
        if (dbString.isEmpty()) {
            dbString = "0";
        }

        String[] jobs = dbString.split(",");
        String ret = "";
        for (int i=0; i<jobs.length; i++) {
            int jobid = Integer.parseInt(jobs[i]);

            if (jobid == 0) {
                ret = ctx.getString(R.string.personalinfosetting_noexisting);
                break;
            }
            for (int j=0; j<KbossApplication.g_skills.size(); j++) {
                STSkill skillst = KbossApplication.g_skills.get(j);

                if (skillst.f_id == jobid) {
                    ret += skillst.f_title + ", ";
                    break;
                }
            }
        }

        if (!ret.isEmpty() && ret.charAt(ret.length()-1)==' ') {
            ret = ret.substring(0, ret.length()-2);
        }

        return ret;
    }

//    public static String getJobsString(String dbString, Context ctx) {
//        if (dbString.isEmpty()) {
//            dbString = "0";
//        }
//
//        String[] jobs = dbString.split(",");
//        String ret = "";
//        ArrayList<STSkill> tstrarea=new ArrayList<>();
//        for (int i=0; i<jobs.length; i++) {
//            int jobid = Integer.parseInt(jobs[i]);
//
//            if (jobid == 0) {
//                ret = ctx.getString(R.string.personalinfosetting_noexisting);
//                break;
//            }
//
//            for (int j=0; j<KbossApplication.g_skills.size(); j++) {
//                STSkill skillst = KbossApplication.g_skills.get(j);
//
//                if ( skillst.f_id==jobid )
//                {
//                    STSkill val=new STSkill();
//                    val.f_title=skillst.f_title;
//                    tstrarea.add(val);
//                    break;
//                }
///*                if (skillst.f_id == jobid) {
//                    ret += skillst.f_title + ", ";
//                    break;
//                }
// */           }
//        }
//
//        for ( int j=0;j<tstrarea.size()-1;j++ )
//        {
//            for (int k=j+1;k<tstrarea.size();k++ )
//            {
//                if ( tstrarea.get(j).f_title.compareTo(tstrarea.get(k).f_title)>0 )
//                {
//                    STSkill tmp=new STSkill();
//                    tmp.f_title=tstrarea.get(j).f_title;
//                    tstrarea.get(j).f_title=tstrarea.get(k).f_title;
//                    tstrarea.get(k).f_title=tmp.f_title;
//                }
//            }
//        }
//        for (int j=0; j<tstrarea.size(); j++) {
//            STSkill areast = tstrarea.get(j);
//            ret += areast.f_title + ", ";
//            break;
//        }
//
//        if (!ret.isEmpty() && ret.charAt(ret.length()-1)==' ') {
//            ret = ret.substring(0, ret.length()-2);
//        }
//
//        return ret;
//    }


    public static String getDateString(String dbstr) {
        if (dbstr.equals("0000-00-00 00:00:00")) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dbstr);
            return String.format("%d년 %d월 %d일", date.getYear()+1900, date.getMonth() + 1, date.getDate());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String changeDateTimeForm1(String origindate) {
        if (origindate.equals("0000-00-00 00:00:00")) {
            return "";
        }

        String date = changeDateString(origindate);
        String str[] = origindate.split(" ");
        String changeData = date + " " + changeTimeForm(str[1],"HH:mm:ss");

        return changeData;
    }

    /**
     * 날짜 시간 폼 변경하기
     * @return
     */
    public static String changeDateTimeForm(String origindate) {
        if (origindate.equals("0000-00-00 00:00:00")) {
            return "";
        }

        String date = changeDateString(origindate);
        String str[] = origindate.split(" ");
        String changeData = date + " " + changeTimeForm(str[1],"HH:mm");

        return changeData;
    }

    /**
     * 시간 폼 변경하기
     * @return
     */
    public static String changeTimeForm(String origintime, String changeform) {

        String changeData = "";

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat( "HH:mm:ss", Locale.KOREA );

        //발생시간 +1시간 계산
        try{
            Date dateHappen = mSimpleDateFormat.parse(origintime);

            SimpleDateFormat changeformat = new SimpleDateFormat( changeform, Locale.KOREA );
            changeData = changeformat.format(dateHappen);

        }catch (Exception e){
            e.printStackTrace();
        }

        return changeData;
    }

    public static String changeDateString(String dbstr) {
        if (dbstr.equals("0000-00-00 00:00:00")) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dbstr);
            return String.format("%d.%d.%d", date.getYear()+1900, date.getMonth() + 1, date.getDate());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String getDateStringFmt(String dbstr, String fmt) {
        if (dbstr.equals("0000-00-00 00:00:00")) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dbstr);
            return String.format(fmt, date.getYear()+1900, date.getMonth() + 1, date.getDate());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDateStringWeekday(long pos) {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("yy.MM.dd(E)", Locale.KOREAN);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        long time = date.getTime();
        date.setTime(time + pos*24*3600*1000);
        weekDay = dayFormat.format(date);

        return weekDay;
    }

    public static String getDateStringWeekday_3(String dbstr) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREAN);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dbstr);
            return dayFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDateStringWeekdayFromDate(Date date)
    {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREAN);
        weekDay = dayFormat.format(date);

        return weekDay;

    }

    public static String getDateStringWeekday_2(long pos) {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREAN);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        long time = date.getTime();
        date.setTime(time + pos*24*3600*1000);
        weekDay = dayFormat.format(date);

        return weekDay;
    }

    public static String getDateTimeStringFromToday(long pos) {
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            long time = date.getTime();
            date.setTime(time + pos*24*3600*1000);

            return String.format("%d-%02d-%02d %02d:%02d:%02d", date.getYear()+1900, date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDateTimeStringFmt(String dbstr, String fmt) {
        if (dbstr.equals("0000-00-00 00:00:00")) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dbstr);
            return String.format(fmt, date.getYear()+1900, date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDateString2(String dbstr) {
        if (dbstr.equals("0000-00-00 00:00:00")) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dbstr);
            return String.format("%d년 %d월 %d일", date.getYear()+1900, date.getMonth() + 1, date.getDate());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getElapsedHoursFromToday(String endtime){
        long longElapsedMinTime = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            long today = date.getTime();
            Log.e("Function today:",String.format("%s:%d",date.toString(),today));

            Date dateHappen1 = formatter.parse(endtime);

            Calendar calHappen1 = Calendar.getInstance();
            calHappen1.setTime(dateHappen1);
            //시간차이 계산
            long prev= calHappen1.getTimeInMillis();
            Log.e("Function prev:",String.format("%s:%d",endtime,prev));

            longElapsedMinTime = ((today-prev-84430000)/1000)/60/60;
        }catch (Exception e){
            e.printStackTrace();
        }
        return longElapsedMinTime;
    }

    public static long getElapsedDate(String starttime,String endtime){
        long longElapsedMinTime = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date dateHappen = formatter.parse(starttime);

            Calendar calHappen = Calendar.getInstance();
            calHappen.setTime(dateHappen);

            Date dateHappen1 = formatter.parse(endtime);

            Calendar calHappen1 = Calendar.getInstance();
            calHappen1.setTime(dateHappen1);
            //시간차이 계산
            longElapsedMinTime = ((calHappen1.getTimeInMillis() - calHappen.getTimeInMillis())/1000)/60/60/24;
        }catch (Exception e){
            e.printStackTrace();
        }
        return longElapsedMinTime;
    }

    public static void hideVirtualKeyboard(Activity activity) {
        // hide soft keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) activity. getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = activity.getCurrentFocus();
        if (v != null) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static String getPayTypeString(int paytype) {
        for (int j=0; j<KbossApplication.g_paytypes.size(); j++) {
            STPayType paytypest = KbossApplication.g_paytypes.get(j);

            if (paytypest.f_id == paytype) {
                return paytypest.f_name;
            }
        }

        return "";
    }

    public static void initImageLoader(Context ctx) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        KbossApplication.g_imageLoader.init(config);
    }

    public static String getBankName(int banktype) {
        for (int i=0; i<KbossApplication.g_banktypes.size(); i++) {
            STBankType bankTypeSt = KbossApplication.g_banktypes.get(i);

            if (bankTypeSt.f_id == banktype) {
                return bankTypeSt.f_name;
            }
        }

        return "";
    }

    public static boolean isValidEmail(String strEmail)
    {
        return EMAIL_ADDRESS_PATTERN.matcher(strEmail).matches();
    }

    // For auto login
    public static void saveUserInfo(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("KBOSS_PRIVACY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("MEMBER_ID", KbossApplication.g_userinfo.f_id);
        editor.putString("AUTH_KEY", KbossApplication.g_userinfo.f_authkey);

        editor.commit();
    }

    public static boolean loadUserInfo(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("KBOSS_PRIVACY", Context.MODE_PRIVATE);

        KbossApplication.g_userinfo = new STUserInfo();
        KbossApplication.g_userinfo.f_id = pref.getInt("MEMBER_ID", 0);
        if (KbossApplication.g_userinfo.f_id == 0) {
            return false;
        }

        KbossApplication.g_userinfo.f_authkey = pref.getString("AUTH_KEY", "");
        if (KbossApplication.g_userinfo.f_authkey.isEmpty()) {
            return false;
        }

        return true;
    }

    public static String getLocaleNumberString(int intData, String postfix) {
        String str = NumberFormat.getNumberInstance(java.util.Locale.US).format(intData);

        return str + postfix;
    }

    // getContacts(this.getContentResolver());
    public static void getContacts(ContentResolver cr, ArrayList<STContact> contacts, String filter) {
        contacts.clear();

        Cursor phones = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, "display_name ASC"); // ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        while (phones.moveToNext()) {
            String name = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phoneNumber = phoneNumber.replace('-', ' ').replace('(', ' ').replace(')', ' ').replaceAll(" ", "");
            if (filter.isEmpty() == false) {
                if (!name.contains(filter) && !phoneNumber.contains(filter)) {
                    continue;
                }
            }

            STContact anItem = new STContact();
            anItem.f_name = name;
            anItem.f_phone = phoneNumber;
            anItem.f_is_member = false;

            contacts.add(anItem);
        }
        phones.close();// close cursor
    }
}

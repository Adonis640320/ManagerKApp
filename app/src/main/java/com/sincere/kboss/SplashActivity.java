package com.sincere.kboss;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STSysParams;
import com.sincere.kboss.stdata.STUserInfo;

import org.apache.http.Header;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends ActivityTempl {
    TextView lbl01, lbl02, lblLinkCount;
    RelativeLayout rlLinkCount;

    int workend = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        workend = KbossApplication.getInstance().getSharedPreferencesData("workend",0);

        String token = FirebaseInstanceId.getInstance().getToken();
        KbossApplication.setToken(token);
        Log.e("test","Token:"+token);
        Functions.initImageLoader(getApplicationContext());

        callApiGetSysparams();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                this.cancel();
            }
        };
        timer.schedule(task, 2000, 1);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
            }
        });

        lbl01 = (TextView) findViewById(R.id.lbl01);
        lbl02 = (TextView) findViewById(R.id.lbl02);
        lblLinkCount = (TextView) findViewById(R.id.lblLinkCount);
        rlLinkCount = (RelativeLayout) findViewById(R.id.rlLinkCount);

        Typeface tfGodoB = Typeface.createFromAsset(getAssets(), "fonts/GodoB.ttf");
        lbl01.setTypeface(tfGodoB);
        lbl02.setTypeface(tfGodoB);
        lblLinkCount.setTypeface(tfGodoB);


    }


    void gotoLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("workend",workend);
        startActivity(i);

        // Activity transition effect
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        finish();
    }

    void callApiGetSysparams () {
        handler = new JsonHttpResponseHandler()
        {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<STSysParams> ret = new ArrayList<>();

                retVal = ServiceManager.inst.parseGetSysParams(response, ret);
                if (retVal.code == ServiceParams.ERR_NONE) {
                    KbossApplication.g_sysparams = ret.get(0);
                    // added by Adonis
                    // 확인 버튼을 누르면 앱스토에로 이동
                    OnClickListener clickListener = new OnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialog, View view) {
                            switch (view.getId()) {
                                case R.id.btnGoStore:

                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }

                                    break;
                            }
                        }
                    } ;
                    if(!ServiceParams.appVersionString.equals( KbossApplication.g_sysparams.f_app_version)){
                        Holder holder = new ViewHolder(R.layout.dialog_appversion);
                        SimpleAdapter adapter = new SimpleAdapter(SplashActivity.this, false);
                        DialogPlus dialog = new DialogPlus.Builder(SplashActivity.this)
                                .setContentHolder(holder)
                                .setCancelable(true)
                                .setGravity(DialogPlus.Gravity.CENTER)
                                .setAdapter(adapter)
                                .setOnClickListener(clickListener)
                                .create();

                        dialog.show();
                    }
                    else{

                        ServiceParams.baseUrl = KbossApplication.g_sysparams.f_baseurl;
                        ServiceParams.svcBaseUrl = KbossApplication.g_sysparams.f_baseurl + "/svc/";
                        ServiceParams.assetsBaseUrl = KbossApplication.g_sysparams.f_baseurl + "/assets/";

                        if (KbossApplication.g_sysparams.f_link_count > 10000) {
                            rlLinkCount.setVisibility(View.VISIBLE);
                            lblLinkCount.setText(Functions.getLocaleNumberString(KbossApplication.g_sysparams.f_link_count,""));
                        }

                        callApiGetWorkingArea();
                    }
                    // added

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                    Functions.showToastLong(SplashActivity.this, "네트웍연결이 끊어졌습니다. 네트웍상태를 다시 확인해주세요.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Functions.showToastLong(SplashActivity.this, "네트웍연결이 끊어졌습니다. 네트웍상태를 다시 확인해주세요.");

            }
        };

        ServiceManager.inst.getSysparams(handler);
    }

    void callApiGetWorkingArea() {
        KbossApplication.g_areas.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseGetWorkingArea(response, KbossApplication.g_areas);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(SplashActivity.this, retVal.msg);
                } else {
                    callApiGetWeekday();
                }
            }
        };

        ServiceManager.inst.getWorkingArea(handler);
    }

    void callApiGetWeekday() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                KbossApplication.g_weekdays.clear();
                retVal = ServiceManager.inst.parseGetWeekday(response, KbossApplication.g_weekdays);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(SplashActivity.this, retVal.msg);
                } else {
                    callApiGetSkills();
                }
            }
        };

        ServiceManager.inst.getWeekday(handler);
    }

    void callApiGetSkills() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                KbossApplication.g_skills.clear();
                retVal = ServiceManager.inst.parseGetSkills(response, KbossApplication.g_skills);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(SplashActivity.this, retVal.msg);
                } else {
                    callApiGetPayTypes();
                }
            }
        };

        ServiceManager.inst.getSkills(handler);
    }

    void callApiGetPayTypes() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                KbossApplication.g_paytypes.clear();
                retVal = ServiceManager.inst.parseGetPayTypes(response, KbossApplication.g_paytypes);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(SplashActivity.this, retVal.msg);
                } else {
                    callApiGetBankTypes();
                }
            }
        };

        ServiceManager.inst.getPayTypes(handler);
    }

    void callApiGetBankTypes() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                KbossApplication.g_banktypes.clear();
                retVal = ServiceManager.inst.parseGetBankTypes(response, KbossApplication.g_banktypes);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(SplashActivity.this, retVal.msg);
                } else {
                    callApiGetLogoutReasons();
                }
            }
        };

        ServiceManager.inst.getBankTypes(handler);
    }

    void callApiGetLogoutReasons() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                KbossApplication.g_reasons.clear();
                retVal = ServiceManager.inst.parseGetLogoutReasons(response, KbossApplication.g_reasons);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(SplashActivity.this, retVal.msg);

                } else {
                    callApiGetWorkAmounts();
                }
            }
        };

        ServiceManager.inst.getLogoutReasons(handler);
    }

    void callApiGetWorkAmounts() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                KbossApplication.g_workamounts.clear();
                retVal = ServiceManager.inst.parseGetWorkAmounts(response, KbossApplication.g_workamounts);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(SplashActivity.this, retVal.msg);
                } else {
                    gotoLoginActivity();
                    /*
                    if (Functions.loadUserInfo(getApplicationContext())) {
                        callApiLoginSec();
                    } else {
                        gotoLoginActivity();
                    }
                    */
                }
            }
        };

        ServiceManager.inst.getWorkAmounts(handler);
    }

    public void callApiLoginSec() {
        int memberid = KbossApplication.g_userinfo.f_id;
        String authkey = KbossApplication.g_userinfo.f_authkey;
        Log.e("test","callApiLoginSec "+memberid+" "+authkey);
        handler = new JsonHttpResponseHandler()
        {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<STUserInfo> users = new ArrayList<>();
                retVal = ServiceManager.inst.parseLogin(response, users);
                if (retVal.code == ServiceParams.ERR_NONE) {
                    KbossApplication.g_userinfo = users.get(0);

                    if (KbossApplication.g_userinfo.f_level == ServiceParams.LV_MANAGER) {
                        Intent i = new Intent(SplashActivity.this, com.sincere.kboss.manager.MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        finish();
                    } else { // worker is default
                        Intent i = new Intent(SplashActivity.this, com.sincere.kboss.worker.MainActivity.class);
                        i.putExtra("workend",workend);
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        finish();
                    }

                } else {
                    gotoLoginActivity();
                }
            }
        };

        ServiceManager.inst.loginsec(memberid, authkey, handler);
    }
}

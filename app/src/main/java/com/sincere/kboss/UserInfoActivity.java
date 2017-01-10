package com.sincere.kboss;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Michael on 2016.10.26.
 */
public class UserInfoActivity extends ActivityTempl {
    TextView lblWorkingArea;
    TextView lblWeekday;
    TextView lblHistoryYear;
    TextView lblJobs;
    TextView lblPayType;
    TextView lblBank;
    EditText edtIntro;
    LinearLayout llIntroNext;
    TextView lblBasicSecDate;
    TextView lblCert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUI();
    }

    void initUI() {

        ImageView btnBack = (ImageView)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //callApiSetIntro();
                returnBack(null);
            }
        });

        TextView btnConfirm = (TextView)findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnBack(null);
            }
        });

        lblWorkingArea = (TextView) findViewById(R.id.lblWorkingArea);
        lblWeekday = (TextView) findViewById(R.id.lblWeekday);
        lblHistoryYear = (TextView) findViewById(R.id.lblHistoryYear);
        lblJobs = (TextView) findViewById(R.id.lblJobs);
        edtIntro = (EditText) findViewById(R.id.edtIntro);
        llIntroNext = (LinearLayout) findViewById(R.id.llIntroNext);
        llIntroNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callApiSetIntro();
            }
        });
        lblBasicSecDate = (TextView) findViewById(R.id.lblBasicSecDate);
        lblPayType = (TextView) findViewById(R.id.lblPayType);
        lblCert = (TextView) findViewById(R.id.lblCert);
        lblBank = (TextView) findViewById(R.id.lblBank);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    void updateUI() {
        // set workingarea
        if (KbossApplication.g_userinfo == null || KbossApplication.g_userinfo.f_id == 0) {
            return;
        }

        String areastr = KbossApplication.g_userinfo.f_prefer_zone;

        if (areastr.isEmpty()) {
            areastr = "-1"; // FIXME
        }
        lblWorkingArea.setText(Functions.getAreaDisplayString(areastr, UserInfoActivity.this));

        // set weekday
        String weekdaystr = KbossApplication.g_userinfo.f_prefer_time;
        Log.e("test","prefer time:"+KbossApplication.g_userinfo.f_prefer_time+" prefer zone:"+KbossApplication.g_userinfo.f_prefer_zone);
        if (weekdaystr.isEmpty()) {
            weekdaystr = "-1"; // FIXME
        }

        lblWeekday.setText(Functions.getWeekdayDisplayString(weekdaystr, UserInfoActivity.this));

        // set history year
        String historyyearstr = String.valueOf(KbossApplication.g_userinfo.f_skills_year);
        if (historyyearstr.equals("0")) {
            lblHistoryYear.setText(getString(R.string.personalinfosetting_noexisting));
        } else {
            lblHistoryYear.setText(String.format(getString(R.string.personalinfosetting_oneyear), historyyearstr));
        }

        // set jobs
        String jobsstr = String.valueOf(KbossApplication.g_userinfo.f_skills);
        Log.e("test",jobsstr);
        //lblJobs.setText(Functions.getJobsString(jobsstr, UserInfoActivity.this));

        String rjobsstr=Functions.getJobsString(jobsstr, UserInfoActivity.this);
        String temp[]=rjobsstr.split(", ");
        Arrays.sort(temp);
        String ret="";
        for (int j=0; j<temp.length; j++) {
            if ( temp.length==1 )
            {
                ret=temp[0];break;
            }
            else if ( temp.length==2 )
            {
                if ( j==1 )
                {ret+=temp[j];break;}
            }
            else if ( temp.length>=3 )
            {
                if ( j==1 )
                {
                    ret+=temp[j]+"...";
                    break;
                }
            }
            ret += temp[j] + ", ";

        }
        lblJobs.setText(ret);


        //set intro
        edtIntro.setText(KbossApplication.g_userinfo.f_intro);

        //set basic sec date
        String basicsecdate = Functions.getDateString(KbossApplication.g_userinfo.f_basicsec_date);
        if (basicsecdate.isEmpty()) {
            lblBasicSecDate.setText(getString(R.string.personalinfosetting_noexisting));
        } else {
            lblBasicSecDate.setText(basicsecdate);
        }

        //set cert date
        String certfrontdate = Functions.getDateString(KbossApplication.g_userinfo.f_cert_front_date);
        String certbackdate = Functions.getDateString(KbossApplication.g_userinfo.f_cert_back_date);
        if (certfrontdate.isEmpty() || certfrontdate.equals("0000-00-00 00:00:00")) {
            if (certbackdate.isEmpty() || certbackdate.equals("0000-00-00 00:00:00")) {
                lblCert.setText(getString(R.string.personalinfosetting_noexisting));
            } else {
                lblCert.setText(getString(R.string.cert_back));
            }
        } else {
            if (certbackdate.isEmpty() || certbackdate.equals("0000-00-00 00:00:00")) {
                lblCert.setText(getString(R.string.cert_front));
            } else {
                lblCert.setText(getString(R.string.cert_frontback));
            }
        }

        // paytype
        String paytypestr = Functions.getPayTypeString(KbossApplication.g_userinfo.f_pay_type);
        if (paytypestr.isEmpty()) {
            lblPayType.setText(getString(R.string.personalinfosetting_noexisting));
        } else {
            lblPayType.setText(paytypestr);
        }

        String bank_acct = KbossApplication.g_userinfo.f_bank_acct;
        if (bank_acct.isEmpty()) {
            lblBank.setText(getString(R.string.personalinfosetting_noexisting));
        } else {
            lblBank.setText(Functions.getBankName(KbossApplication.g_userinfo.f_bank_type));
        }
    }

    public void gotoDesiredWorkAreaActivity(View v) {
        Intent i = new Intent(this, DesiredWorkAreaActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoDesiredWorkDayActivity(View v) {
        Intent i = new Intent(this, DesiredWorkDayActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoJobTypeActivity(View v) {
        Intent i = new Intent(this, JobTypeActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoHistoryActivity(View v) {
        Intent i = new Intent(this, HistoryActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoBasicSecurityActivity(View v) {
        Intent i = new Intent(this, BasicSecurityActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoCertActivity(View v) {
        Intent i = new Intent(this, CertActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoPayTypeActivity(View v) {
        Intent i = new Intent(this, PayTypeActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoBankAccountActivity(View v) {
        Intent i = new Intent(this, BankAccountActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoEditAccountActivity(View v) {
        if (KbossApplication.g_userinfo.f_level == ServiceParams.LV_MANAGER) {
            Intent i = new Intent(this, com.sincere.kboss.manager.EditAccountActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            Intent i = new Intent(this, com.sincere.kboss.worker.EditAccountActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    public void gotoLicenseActivity(View v) {
        Intent i = new Intent(this, LicenseActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        //
    }

    public void gotoPrivacyActivity(View v) {
        Intent i = new Intent(this, PrivacyActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        //
    }

    public void gotoLogoutActivity(View v) {
        Intent i = new Intent(this, LogoutActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void callApiSetIntro() {
        final String intro = edtIntro.getText().toString();

        if (intro.isEmpty()) {
            Functions.showToast(UserInfoActivity.this, R.string.personalinfosetting_introducingcomment);
            return;
        }

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(UserInfoActivity.this, retVal.msg);

                } else {
                    KbossApplication.g_userinfo.f_intro = intro;
                }
                returnBack(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
            }
        };

        showProgress();

        ServiceManager.inst.setIntro(intro, handler);
    }

    @Override
    public void onBackPressed() {
        //callApiSetIntro();
        returnBack(null);
    }

}

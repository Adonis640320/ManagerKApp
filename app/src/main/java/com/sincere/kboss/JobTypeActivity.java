package com.sincere.kboss;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/**
 * Created by Michael on 10/26/2016.
 */
public class JobTypeActivity extends ActivityTempl {
    ImageView btnBack;

    ArrayList<LinearLayout> llsJobType = new ArrayList<>();
    ArrayList<TextView> lblsJobType = new ArrayList<>();
    ArrayList<Boolean> m_chkStats = new ArrayList<>();

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int jobid = (Integer) v.getTag();
            boolean chk = m_chkStats.get(jobid-1);
            m_chkStats.set(jobid-1, !chk);
            updateSelected(jobid-1, !chk);
        }
    };

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String jobs = getJobsString();
            if (jobs.isEmpty()) {
                jobs = "0";

            } else {
                Log.e("test","Jobs:"+jobs);
                callApiSetSkills(jobs);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobtype);

        initUI();

        updateUI();
    }

    void updateUI() {
        String jobs = KbossApplication.g_userinfo.f_skills;
        if (jobs.isEmpty()) {
            jobs = "1";
        }

        m_chkStats.clear();
        for (int i=0; i<llsJobType.size(); i++) {
            m_chkStats.add(i, false);
        }

        if (jobs.equals("0")) {
            jobs = "1";
        }

        String[] jobids = jobs.split(",");
        for (int i = 0; i < jobids.length; i++) {
            int jobid = Integer.parseInt(jobids[i]);

            updateSelected(jobid-1, true);
            m_chkStats.set(jobid-1, true);
        }
    }

    void updateSelected(int id, boolean checked) {
        if (checked) {
            llsJobType.get(id).setBackgroundColor(getResources().getColor(R.color.clr_red_dark));
            lblsJobType.get(id).setTextColor(getResources().getColor(android.R.color.white));
        } else {
            llsJobType.get(id).setBackgroundColor(getResources().getColor(android.R.color.white));
            lblsJobType.get(id).setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    public void onConfirmPressed(View view)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        btnBackClickListener.onClick(null);
    }

    public String getJobsString() {
        if (m_chkStats.size() == 0) {
            return "";
        }

        String ret = "";
        for (int i=0; i<m_chkStats.size(); i++) {
            boolean b = m_chkStats.get(i);
            if (b) {
                ret += String.valueOf(i+1) + ",";
            }
        }

        if (!ret.isEmpty() && ret.charAt(ret.length()-1)==',') {
            ret = ret.substring(0, ret.length()-1);
        }

        return ret;
    }

    void callApiSetSkills(final String skills) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(JobTypeActivity.this, retVal.msg);

                } else {
                    KbossApplication.g_userinfo.f_skills = skills;

                    returnBack(null);
                }
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

        ServiceManager.inst.setSkills(skills, handler);
    }

    void initUI() {
        TextView lbl;
        LinearLayout ll;

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);

        ll = (LinearLayout) findViewById(R.id.job_jobtitle1);
        ll.setTag(1);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle2);
        ll.setTag(2);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle3);
        ll.setTag(3);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle4);
        ll.setTag(4);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle5);
        ll.setTag(5);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle6);
        ll.setTag(6);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle7);
        ll.setTag(7);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle8);
        ll.setTag(8);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle9);
        ll.setTag(9);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle10);
        ll.setTag(10);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle11);
        ll.setTag(11);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle12);
        ll.setTag(12);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle13);
        ll.setTag(13);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle14);
        ll.setTag(14);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle15);
        ll.setTag(15);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle16);
        ll.setTag(16);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle17);
        ll.setTag(17);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle18);
        ll.setTag(18);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle19);
        ll.setTag(19);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle20);
        ll.setTag(20);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle21);
        ll.setTag(21);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle22);
        ll.setTag(22);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle23);
        ll.setTag(23);
        llsJobType.add(ll);
        ll = (LinearLayout) findViewById(R.id.job_jobtitle24);
        ll.setTag(24);
        llsJobType.add(ll);

        lbl = (TextView) findViewById(R.id.job_job1);
        lbl.setTag(1);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job2);
        lbl.setTag(2);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job3);
        lbl.setTag(3);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job4);
        lbl.setTag(4);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job5);
        lbl.setTag(5);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job6);
        lbl.setTag(6);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job7);
        lbl.setTag(7);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job8);
        lbl.setTag(8);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job9);
        lbl.setTag(9);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job10);
        lbl.setTag(10);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job11);
        lbl.setTag(11);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job12);
        lbl.setTag(12);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job13);
        lbl.setTag(13);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job14);
        lbl.setTag(14);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job15);
        lbl.setTag(15);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job16);
        lbl.setTag(16);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job17);
        lbl.setTag(17);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job18);
        lbl.setTag(18);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job19);
        lbl.setTag(19);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job20);
        lbl.setTag(20);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job21);
        lbl.setTag(21);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job22);
        lbl.setTag(22);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job23);
        lbl.setTag(23);
        lblsJobType.add(lbl);
        lbl = (TextView) findViewById(R.id.job_job24);
        lbl.setTag(24);
        lblsJobType.add(lbl);

        for (int i=0; i<llsJobType.size(); i++) {
            ll = llsJobType.get(i);
            ll.setOnClickListener(clickListener);
        }
    }
}

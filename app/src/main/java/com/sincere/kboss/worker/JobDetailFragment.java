package com.sincere.kboss.worker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.WebViewActivity;
import com.sincere.kboss.adapters.WorkShopHistoryAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJobWorker;
import com.sincere.kboss.stdata.STPayType;
import com.sincere.kboss.stdata.STSpotManager;
import com.sincere.kboss.stdata.STWorkShopHstory;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Michael on 2016.11.03.
 */
public class JobDetailFragment extends FragmentTempl implements View.OnClickListener {
    TextView lblTitle;
    TextView btnStat;
    int state;
    RelativeLayout rl05WorkDetail;
    RelativeLayout rl08;
    ListView lv_placehistory;
    WorkShopHistoryAdapter m_WorkShopHistory;
    ArrayList<STWorkShopHstory> m_arrWorkshopHistory;

    TextView lblWorkday;
    TextView lblWorkplace;
    TextView lblWorktime;
    TextView lblWorktype;
    TextView lblPerDay;
    RelativeLayout rlReadMore;
    Button btnGotoWork;
    CheckBox chkAgreement;
    RelativeLayout rlAgreement;
    TextView lblRealPay;
    TextView lblWorkDetail;
    TextView lblRegisterMan;
    TextView lblBuildingComp;
    TextView lblOrgOffice;
    TextView lblManager;

    RelativeLayout rlDetailPathInfo;

    ScrollView svMain;

    public final static String ARG_JOB = "job";
    STJobWorker workerjob;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_job, container, false);

        ImageView btnBack = (ImageView) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.gotoPrevFragment();
            }
        });

        svMain = (ScrollView)v.findViewById(R.id.svMain);

        lblTitle = (TextView) v.findViewById(R.id.lblTitle);
        btnStat = (TextView) v.findViewById(R.id.btnStat);

        ImageView imgTick = (ImageView)v.findViewById(R.id.imgTick);
        imgTick.setOnClickListener(this);

        lblWorkday = (TextView) v.findViewById(R.id.lblWorkday);
        lblWorkplace = (TextView) v.findViewById(R.id.lblWorkplace);
        lblWorktime = (TextView) v.findViewById(R.id.lblWorktime);
        lblWorktype = (TextView) v.findViewById(R.id.lblWorktype);
        lblPerDay = (TextView) v.findViewById(R.id.lblPerDay);
        rlReadMore = (RelativeLayout) v.findViewById(R.id.rlReadMore);
        btnGotoWork = (Button) v.findViewById(R.id.btnGotoWork);
        btnGotoWork.setOnClickListener(this);
        chkAgreement = (CheckBox) v.findViewById(R.id.chkAgreement);
        rlAgreement = (RelativeLayout)v.findViewById(R.id.rlAgreement);
        lblRealPay = (TextView) v.findViewById(R.id.lblRealPay);
        lblWorkDetail = (TextView) v.findViewById(R.id.lblWorkDetail);
        lblRegisterMan = (TextView) v.findViewById(R.id.lblRegisterMan);
        lblBuildingComp = (TextView) v.findViewById(R.id.lblBuildingComp);
        lblOrgOffice = (TextView) v.findViewById(R.id.lblOrgOffice);
        lblManager = (TextView)v.findViewById(R.id.lblManager);

        rl05WorkDetail = (RelativeLayout) v.findViewById(R.id.rl05WorkDetail);
        //rl08 = (RelativeLayout) v.findViewById(R.id.rl08);
        //rl08.setVisibility(View.GONE);
        m_arrWorkshopHistory=new ArrayList<>();
        rlDetailPathInfo = (RelativeLayout)v.findViewById(R.id.rlDetailPathInfo);
        rlDetailPathInfo.setOnClickListener(this);
        lv_placehistory=(ListView)v.findViewById(R.id.lstLogs) ;
        workerjob = getArguments().getParcelable(ARG_JOB);
        updateUI();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    void updateUI() {
        if (workerjob != null) {
            String temp = workerjob.f_spot_name;
            if(temp.length() > 15) {
               temp =  temp.substring(0, 15) + "...";
            }
            lblTitle.setText(temp);

            lblWorkday.setText(Functions.getDateStringWeekday_3(workerjob.job.f_workdate));
            lblWorkplace.setText(workerjob.job.f_address);
            lblWorktime.setText(workerjob.job.f_worktime_start.substring(0, 5)+"~"+workerjob.job.f_worktime_end.substring(0, 5));
            lblWorktype.setText(String.format("%s / %d명", Functions.getJobsString(String.valueOf(workerjob.job.f_skill), getActivity().getApplicationContext()), workerjob.job.f_worker_count));

            int realpay = workerjob.job.f_payment - (int)(workerjob.job.f_payment*STPayType.getCancelledPercent(KbossApplication.g_userinfo.f_pay_type)/100);
            lblPerDay.setText(Functions.getLocaleNumberString(workerjob.job.f_payment, "원"));
            lblRealPay.setText(Functions.getLocaleNumberString(realpay, ""));
        }

        // job status on the title bar
        if (workerjob.f_support_time.isEmpty() || workerjob.f_support_time.equals("0000-00-00 00:00:00") ) {
            btnStat.setText(R.string.jobnotification_incollecting);
            state=0;
            btnGotoWork.setVisibility(View.VISIBLE);
            //chkAgreement.setVisibility(View.VISIBLE);
            rlAgreement.setVisibility(View.VISIBLE);

            callApiWorkerSpotHistory(workerjob.job.f_id);
            callApigetRecommendOwner(workerjob.job.f_spot_id,workerjob.f_worker_id);

            m_WorkShopHistory = new WorkShopHistoryAdapter(getActivity());
            m_WorkShopHistory.setData(m_arrWorkshopHistory);
            lv_placehistory.setAdapter(m_WorkShopHistory);
        } else {
            btnStat.setText(R.string.jobnotification_insupporting);
            if (workerjob.f_support_cancel == 1) {
                btnStat.setText(R.string.jobnotification_supportcancel);
            } else {
                if (workerjob.f_support_check == 1) {
                    btnStat.setText(R.string.jobnotification_presentationok);
                }
                if(workerjob.f_signin_cancel == 1) {
                    btnStat.setText(R.string.jobnotification_presentationcancel);
                }
               /* else {
                    btnStat.setText(R.string.jobnotification_incollecting);
                }
                */

            }
            btnGotoWork.setVisibility(View.GONE);
            //chkAgreement.setVisibility(View.GONE);
            rlAgreement.setVisibility(View.GONE);
        }

        // job detail
        rl05WorkDetail.setVisibility(workerjob.job.f_detail.isEmpty() ? View.GONE : View.VISIBLE);
        if (! workerjob.job.f_detail.isEmpty()) {
            lblWorkDetail.setText(workerjob.job.f_detail);
        }

        lblManager.setText(workerjob.f_manager);
        // job owner
        lblRegisterMan.setText(workerjob.job.f_owner_name);

        lblBuildingComp.setText(workerjob.f_buildcompany);
        lblOrgOffice.setText(workerjob.f_mainbuilding);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlReadMore: {
                break;
            }
            case R.id.rlDetailPathInfo:
                Intent intent1 = new Intent(getActivity(), WebViewActivity.class);
                intent1.putExtra("url", "");
                String temp = workerjob.f_spot_name;
                if(temp.length() > 15) {
                    temp =  temp.substring(0, 15) + "...";
                }
                intent1.putExtra("title", temp);
                String content = workerjob.f_spot_content;
                intent1.putExtra("content", content);
                startActivity(intent1);
                break;
            case R.id.btnGotoWork: {
                if (chkAgreement.isChecked()) {
                    callApiWorkForJob(workerjob.job.f_id);
                    break;
                }
                svMain.post(new Runnable() {
                    @Override
                    public void run() {
                        svMain.fullScroll(View.FOCUS_DOWN);
                    }
                });
                Functions.showToast(getActivity().getApplicationContext(), R.string.agree_privacy);
                break;
            }
            case R.id.imgTick:
//                com.sincere.kboss.worker.MainActivity mainActivity = (com.sincere.kboss.worker.MainActivity) getActivity();
//                mainActivity.gotoMapViewFragment(workerjob);
                Intent intent = new Intent(getActivity(),MapViewActivity.class);
                intent.putExtra("logitude",workerjob.job.f_longitude);
                intent.putExtra("latitude",workerjob.job.f_latitude);
                intent.putExtra("position",lblTitle.getText().toString());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
    }

    void callApiWorkForJob(int jobid) {

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.e("test","근로지원 workforjob:"+response.toString());
                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                    state=1;
                    onFinish();
                } else {
                    Functions.showToastLong(getActivity(), String.format(getString(R.string.wait_owner_agree), workerjob.f_spot_name));

                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.gotoPrevFragment();


                }
            }
        };

        ServiceManager.inst.workForJob(jobid, handler);
    }

    public void callApiWorkerSpotHistory(int jobid){

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.e("test","근로지원 workforjob:"+response.toString());
                ArrayList<STJobWorker> newhistories = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetWorkHistory(response, newhistories);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    GetWorkerSpotHistory(newhistories);
                } else {

                }
            }
        };

        ServiceManager.inst.workhistoryForJob(jobid, handler);
    }

    public void GetWorkerSpotHistory(ArrayList<STJobWorker> data)
    {
        for ( int i=0;i<data.size();i++ )
        {
            STWorkShopHstory workerspotinfo=new STWorkShopHstory();
            workerspotinfo.content="";
            workerspotinfo.date=data.get(i).f_signout_time;

            workerspotinfo.num_id=(int)Functions.getElapsedDate(data.get(i).f_signout_time,data.get(i).f_signin_time);
            workerspotinfo.content="";
            m_arrWorkshopHistory.add(workerspotinfo);
        }
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Intent intent = new Intent();
        intent.putExtra("state", state);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        getFragmentManager().popBackStack();
//        callbackIni.onDateIniSelected(state);
    }

    public void callApigetRecommendOwner(int spotid,int member_id){

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.e("test","근로지원 workforjob:"+response.toString());
                ArrayList<STSpotManager> newhistories = new ArrayList<>();
                retVal = ServiceManager.inst.parsegetRecommendOwner(response, newhistories);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    for ( int i=0;i<newhistories.size();i++ )
                    {
                        STWorkShopHstory workshoptmp=new STWorkShopHstory();
                        workshoptmp.content=String.format("%s은 나를 일하고 싶은 사람으로 등록했습니다.",newhistories.get(i).f_name);
                        workshoptmp.date=newhistories.get(i).f_regTime;
                        m_arrWorkshopHistory.add(workshoptmp);
                    }
                } else {

                }
            }
        };

        ServiceManager.inst.getRecommendOwner(spotid,member_id, handler);
    }
}

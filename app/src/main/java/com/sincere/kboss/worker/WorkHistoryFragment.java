package com.sincere.kboss.worker;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.WebViewActivity;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJobWorker;
import com.sincere.kboss.stdata.STPayType;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Michael on 2016.11.03.
 */
public class WorkHistoryFragment extends FragmentTempl implements View.OnClickListener {
    TextView lblTitle;

    TextView lblWorkday;
    TextView lblWorkplace;
    TextView lblWorktime;
    TextView lblWorktype;
    TextView lblPerDay;
    TextView lblRealPay;
    TextView lbguide;
    TextView lbacksignin;
    TextView lbackwork;
    TextView lbackpay;

    TextView lb_elec_promise;
    TextView lb_work_ackbook;
    TextView lb_agent_book;
    LinearLayout lay_guide;
    LinearLayout lay_result_message;
    LinearLayout lay_result_proof;
    RelativeLayout rlReadMore;
    TextView lblReadMore;
    ImageView imgDownArrow;
    Button btnReqConfirm;
    Button btnhistory;
    Button btnContract;

    RelativeLayout rlDetail;
    RelativeLayout rl06WorkDetail;
    TextView lblWorkDetail;
    TextView lblRegisterMan;
    TextView lblBuildingComp;
    TextView lblOrgOffice;
    TextView lblManager;


    public final static String ARG_HISTORY = "history";
    STJobWorker workerjob;
    String employName;

    private CountDownTimer mTimer = new CountDownTimer(1000 * 4, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            lay_guide.setVisibility(View.GONE);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_workhistory, container, false);

        ImageView btnBack = (ImageView) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.sincere.kboss.worker.MainActivity mainActivity = (com.sincere.kboss.worker.MainActivity) getActivity();
                mainActivity.gotoPrevFragment();
            }
        });

        lblTitle = (TextView) v.findViewById(R.id.lblTitle);

        lblWorkday = (TextView) v.findViewById(R.id.lblWorkday);
        lblWorkplace = (TextView) v.findViewById(R.id.lblWorkplace);
        lblWorktime = (TextView) v.findViewById(R.id.lblWorktime);
        lblWorktype = (TextView) v.findViewById(R.id.lblWorktype);
        lblPerDay = (TextView) v.findViewById(R.id.lblPerDay);
        lbguide=(TextView)v.findViewById(R.id.txt_guide1) ;

        lb_elec_promise=(TextView)v.findViewById(R.id.tv_elec_promise);
        lb_work_ackbook=(TextView)v.findViewById(R.id.tv_ack_workbook);
        lb_agent_book=(TextView)v.findViewById(R.id.tv_agent_payment);

        lay_guide=(LinearLayout)v.findViewById(R.id.layout_guide);
        lay_result_message=(LinearLayout)v.findViewById(R.id.result_message);
        lay_result_message.setVisibility(View.GONE);
        lay_result_proof=(LinearLayout)v.findViewById(R.id.result_proof);
        lay_result_proof.setVisibility(View.GONE);
        rlReadMore = (RelativeLayout) v.findViewById(R.id.rlReadMore);
        rlReadMore.setOnClickListener(this);
        lblReadMore = (TextView)v.findViewById(R.id.lblReadMore);
        imgDownArrow = (ImageView)v.findViewById(R.id.imgDownArrow);
        btnReqConfirm = (Button) v.findViewById(R.id.btnReqConfirm);
        btnhistory=(Button)v.findViewById(R.id.btnHistory);
        btnhistory.setOnClickListener(this);
        btnContract=(Button)v.findViewById(R.id.btnContract);
        btnContract.setOnClickListener(this);
        btnReqConfirm.setOnClickListener(this);
        lblRealPay = (TextView) v.findViewById(R.id.lblRealPay);
        lbackpay=(TextView)v.findViewById(R.id.tv_ack_payment);
        lbacksignin=(TextView)v.findViewById(R.id.tv_ack_signin);
        lbackwork=(TextView)v.findViewById(R.id.tv_ack_work);

        rlDetail = (RelativeLayout)v.findViewById(R.id.rlDetail);
        rl06WorkDetail = (RelativeLayout)v.findViewById(R.id.rl06WorkDetail);
        lblWorkDetail = (TextView) v.findViewById(R.id.lblWorkDetail);
        lblRegisterMan = (TextView) v.findViewById(R.id.lblRegisterMan);
        lblBuildingComp = (TextView) v.findViewById(R.id.lblBuildingComp);
        lblOrgOffice = (TextView) v.findViewById(R.id.lblOrgOffice);
        lblManager = (TextView)v.findViewById(R.id.lblManager);

        ImageView imgTick = (ImageView)v.findViewById(R.id.imgTick);
        imgTick.setOnClickListener(this);

        workerjob = getArguments().getParcelable(ARG_HISTORY);
//        if(workerjob.f_workamount_checked == 1) {
//            btnReqConfirm.setVisibility(View.GONE);
//            DisplayGuide("김소장 앱에서 지급처리 예정이오니\n잠시만 기다려주세요.\n문의전화 : 02-1234-1234");
//        } else {
            if (workerjob.f_signout_check != 1) {
                if (!KbossApplication.g_usersignout && workerjob.f_signin_check == 1 && workerjob.f_signin_cancel == 0 && (workerjob.f_signout_time.isEmpty() || workerjob.f_signout_time.equals("0000-00-00 00:00:00"))) {
                    btnReqConfirm.setVisibility(View.VISIBLE);
                    DisplayGuide("앗! 현장에서 작업확인이 안되어 있어요.\n작업이 완료되었다면 작업확인요청을\n 해보세요.");
                } else {
                    btnReqConfirm.setVisibility(View.GONE);
                    if(workerjob.f_workamount_checked == 1)
                        DisplayGuide("김소장 앱에서 지급처리 예정이오니\n잠시만 기다려주세요.\n문의전화 : 02-6941-0491");
                    else lay_guide.setVisibility(View.GONE);
                }
            } else {
                btnReqConfirm.setVisibility(View.GONE);
                lay_guide.setVisibility(View.GONE);
            }
//        }
        DisplayAck();
        return v;
    }
    public void DisplayGuide(String message)
    {
        lay_guide.setVisibility(View.VISIBLE);
        lbguide.setText(message);
        //mTimer.start();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    void updateUI() {
        String temp = workerjob.f_spot_name;
        if(temp.length() > 15) {
            temp  = temp.substring(0, 15) + "...";
        }
        lblTitle.setText(temp);

        lblWorkday.setText(Functions.getDateStringWeekday_3(workerjob.job.f_workdate));
        lblWorkplace.setText(workerjob.job.f_address);
        lblWorktime.setText(workerjob.job.f_worktime_start.substring(0,5));
        lblWorktype.setText(String.format("%s / %d명", Functions.getJobsString(String.valueOf(workerjob.job.f_skill), getActivity().getApplicationContext()), workerjob.job.f_worker_count));

        int realpay = workerjob.job.f_payment - (int)(workerjob.job.f_payment* STPayType.getCancelledPercent(KbossApplication.g_userinfo.f_pay_type)/100);
        lblPerDay.setText(Functions.getLocaleNumberString(workerjob.job.f_payment, "원"));
        lblRealPay.setText(Functions.getLocaleNumberString(realpay, ""));

        // job detail
        rl06WorkDetail.setVisibility(workerjob.job.f_detail.isEmpty() ? View.GONE : View.VISIBLE);
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
                if(rlDetail.getVisibility() == View.VISIBLE) {
                    rlDetail.setVisibility(View.GONE);
                    lblReadMore.setText("현장정보 상세정보 더보기");
                    imgDownArrow.setImageResource (R.drawable.downarrow);
                } else {
                    rlDetail.setVisibility(View.VISIBLE);
                    lblReadMore.setText("현장정보 상세정보 접기");
                    imgDownArrow.setImageResource(R.drawable.uparrow);
                }
                break;
            }
            case R.id.btnReqConfirm: {
                callApiConfirmSignout(workerjob.job.f_id);
                break;
            }
            case R.id.btnHistory:
                DisplayAck();
                break;
            case R.id.btnContract:
                Displayproof();
                break;
            case R.id.imgTick:
                Intent intent = new Intent(getActivity(),MapViewActivity.class);
                intent.putExtra("logitude",workerjob.job.f_longitude);
                intent.putExtra("latitude",workerjob.job.f_latitude);
                intent.putExtra("position",lblTitle.getText().toString());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
    }

    public void DisplayAck()
    {
        lay_result_proof.setVisibility(View.INVISIBLE);
        lay_result_message.setVisibility(View.VISIBLE);
        btnhistory.setBackgroundResource(R.color.clr_btn_history_on);
        btnContract.setBackgroundResource(R.color.clr_btn_history_off);
        callApiGetManager(workerjob.job.f_owner_id);
    }

    public void Displayproof()
    {
        btnhistory.setBackgroundResource(R.color.clr_btn_history_off);
        btnContract.setBackgroundResource(R.color.clr_btn_history_on);
        lay_result_message.setVisibility(View.INVISIBLE);
        lay_result_proof.setVisibility(View.VISIBLE);
        lb_elec_promise.setText("전자근로계약서(2016.08.26일자)");
        lb_work_ackbook.setText("작업확인서");
        lb_agent_book.setText("임금지급영수증");
        lb_elec_promise.setClickable(true);
        lb_elec_promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent web=new Intent(getActivity(), WebViewActivity.class);
                web.putExtra("title","전자근로계약서");
                web.putExtra("url","file:///android_res/raw/"+"a.html");
                startActivity(web);
            }
        });
        lb_agent_book.setClickable(true);
        lb_agent_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent web=new Intent(getActivity(), WebViewActivity.class);
                web.putExtra("title","임금지급영수증");
                web.putExtra("url","file:///android_res/raw/"+"b.html");
                startActivity(web);
            }
        });
        lb_work_ackbook.setClickable(true);
        lb_work_ackbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent web=new Intent(getActivity(), WebViewActivity.class);
                web.putExtra("title","작업확인서");
                web.putExtra("url","file:///android_res/raw/"+"c.html");
                startActivity(web);
            }
        });

    }

    void callApiConfirmSignout(int jobid) {
        Toast.makeText(getActivity(), "현장관리자에게 작업확인을 요청하였습니다.", Toast.LENGTH_SHORT).show();

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    Log.e("test",response.toString());
                    KbossApplication.g_usersignout=true;
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.gotoPrevFragment();
                }
            }
        };

        ServiceManager.inst.confirmSignOut(jobid, handler);
    }

    void callApiGetManager(int owner_id)
    {

        if ( workerjob.f_signin_check==1 )
        {
            lbacksignin.setText(workerjob.job.f_owner_name+"님이 "+Functions.changeDateTimeForm(workerjob.f_signin_time)+"에 출근 확인했습니다.");
        }
        else
        {
            lbacksignin.setText("출근 기록이 없습니다.");
        }
        if ( workerjob.f_workamount_checked==1 )
        {
            lbackwork.setText(workerjob.job.f_owner_name+"님이 "+Functions.changeDateTimeForm(workerjob.f_signout_time)+"에 작업 확인했습니다.");
        }
        else {
            lbackwork.setText("작업 확인되지 않았습니다.");
        }
        if ( workerjob.f_signout_check==1 )
        {
            lbackpay.setText(Functions.changeDateTimeForm(workerjob.f_signout_time)+"에 계좌지급처리 되었습니다."+"("+
                    KbossApplication.g_banktypes.get(KbossApplication.g_userinfo.f_bank_type).f_name+" "+
                    KbossApplication.g_userinfo.f_bank_acct+" "+KbossApplication.g_userinfo.f_name+")");
        }
        else {
            lbackpay.setText("지불되지 않았습니다.");
        }


//        handler = new JsonHttpResponseHandler() {
//            RetVal retVal;
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                retVal = ServiceManager.inst.parseNoData(response);
//                if (retVal.code != ServiceParams.ERR_NONE) {
//                    Functions.showToast(getActivity(), retVal.msg);
//                } else {
//                    try {
//                        employName = response.getString("SVCC_DATA");
//                        workerjob.job.f_owner_name=employName;
//                        if ( workerjob.f_signin_check==1 )
//                        {
//                            lbacksignin.setText(employName+"님이 "+workerjob.f_signin_time+"에 출근 확인했습니다.");
//                        }
//                        else
//                        {
//                            lbacksignin.setText("출근 기록이 없습니다.");
//                        }
//                        if ( workerjob.f_workamount_checked==1 )
//                        {
//                            lbackwork.setText(workerjob.job.f_owner_name+"님이"+workerjob.f_signout_time+"에 작업 확인했습니다.");
//                        }
//                        else {
//                            lbackwork.setText("작업 확인되지 않았습니다.");
//                        }
//                        if ( workerjob.f_signout_check==1 )
//                        {
//                            lbackpay.setText(workerjob.f_support_time+"에 계좌지급처리 되었습니다."+"("+
//                                    KbossApplication.g_banktypes.get(KbossApplication.g_userinfo.f_bank_type).f_name+
//                                    KbossApplication.g_userinfo.f_bank_acct+KbossApplication.g_userinfo.f_name+")");
//                        }
//                        else {
//                            lbackpay.setText("지불되지 않았습니다.");
//                        }
//                    }catch(Exception e){};
//
//                }
//            }
//        };
//        ServiceManager.inst.getManagername(owner_id, handler);
    }
}

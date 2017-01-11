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
        lblWorktime.setText(workerjob.job.f_worktime_start.substring(0,5) + " ~ " + workerjob.job.f_worktime_end.substring(0,5)); // modified by Adonis
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
//                web.putExtra("url","file:///android_res/raw/"+"a.html");

                String content = "<html >\n" +
                        "\n" +
                        "<head></head>\n" +
                        "\n" +
                        "<body lang=KO style='tab-interval:40.0pt'>\n" +
                        "\n" +
                        "<table width=699 height=60>\n" +
                        "\t<tr><td align=center >\n" +
                        "\t\t<b><span style='font-size:22.0pt;mso-bidi-font-size:11.0pt;line-height:107%;font-family:\"맑은 고딕\";'>근로계약서</span></b>\n" +
                        "  </td></tr>\n" +
                        "</table>\n" +
                        "\n" +
                        "<table border=1 cellspacing=0 cellpadding=0 width=699 style='width:524.25pt;border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;mso-yfti-tbllook:1184;mso-padding-alt:0cm 5.4pt 0cm 5.4pt'>\n" +
                        " <tr >\n" +
                        "  <td width=86 rowspan=2 style='width:64.4pt;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\";'>사용자(갑)</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=86 style='width:64.4pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\";'>상호</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=197 style='width:147.4pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>㈜" + workerjob.f_buildcompany + "</span></i></p>\n" +
                        "  </td>\n" +
                        "  <td width=85 style='width:63.75pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\"; '>대표자</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=246 style='width:184.3pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_owner_name + "</span></i></p>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=86 style='width:64.4pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\";'>주소</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=197 style='width:147.4pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_address + "</span></i></p>\n" +
                        "  </td>\n" +
                        "  <td width=85 style='width:63.75pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\"; '>전화번호</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=246 style='width:184.3pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>02-1234-1234</span></i></p>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=86 rowspan=2 style='width:64.4pt;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\";'>근로자(을)</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=86 style='width:64.4pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\";'>성명</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=197 style='width:147.4pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>김일용</span></i></p>\n" +
                        "  </td>\n" +
                        "  <td width=85 style='width:63.75pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\"; '>주민번호</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=246 style='width:184.3pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>501010-1234567</span></i></p>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=86 style='width:64.4pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\";'>주소</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=197 style='width:147.4pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>서울 강남구 역삼동 111</span></i></p>\n" +
                        "  </td>\n" +
                        "  <td width=85 style='width:63.75pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<span style='font-family:\"맑은 고딕\"; '>전화번호</span></p>\n" +
                        "  </td>\n" +
                        "  <td width=246 style='width:184.3pt;border:solid windowtext 1.0pt;border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                        "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p    align=center style='margin-bottom:0cm;margin-bottom:.0001pt;text-align:center;line-height:normal'>\n" +
                        "  \t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>010-1234-1234</span></i></p>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        "</table>\n" +
                        "<p><span style='font-family:\"맑은 고딕\";'>“갑”과“을”은 아래 근로조건을 성실 이행할 것을 약정하고 근로계약을 체결한다.</span></p>\t\n" +
                        "<table width=699>\n" +
                        "\t<tr>\n" +
                        "\t\t<td align=center >---------- 아&nbsp;&nbsp;&nbsp;&nbsp;래 ----------\n" +
                        "\t\t</td>\n" +
                        "\t</tr>\n" +
                        "\t<tr>\n" +
                        "\t\t<td>\n" +
                        "\t\t\t<p><b>1. 근로계약기간 :    </b>\n" +
                        "\t\t\t\t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_workdate.substring(0, 4) +"</span></i> <b>년   </b>  \n" +
                        "\t\t\t\t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_workdate.substring(5, 7) +"</span></i>  <b>월   </b>   \n" +
                        "\t\t\t\t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_workdate.substring(8, 10) +"</span></i>  <b>일</b>\n" +
                        "\t\t\t</p>\n" +
                        "\n" +
                        "\t\t\t<p><b>2. 근로장소 및 작업공종 :  </b>\n" +
                        "\t\t\t\t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.f_spot_name + " </span></i> <b> 현장 </b> \n" +
                        "\t\t\t\t<i><span style='font-family:\"맑은 고딕\";color:#0070C0'> 골조 </span></i> <b> 공종 </b> \n" +
                        "\t\t\t</p>\n" +
                        "\t\t\t<p style='margin-bottom:0cm;'><b>3. 임금</b></p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t(1) “을”의 임금은 시급은 <i><span style='font-size:11pt;font-weight:bold;font-family:\"맑은 고딕\";color:#0070C0'>100,000</i></span> 원으로 하고, 연장ㆍ야간근로시(22:00~06:00)에는 시급의 50%를 각 가산하여 지급한다.</p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t(2) 임금은 매일 근로종료 후 당일지급하며, 다만 근로계약이 갱신되는 경우 근로자의 동의하에 특정 요일 또는 월의 특정일을 정하여 지급할 수 있다.</p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t(3) 임금지급시 근로소득세 및 고용보험료, 건강보험료(1월 이상 근로시), 국민연금(1월 이상 근로시) 본인부담금 내지 기여금을 원천징수한 후 지급한다.</p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t(4) 임금지급방식은 본인에게 직접 지급하는 것을 원칙으로 한다.</p>\n" +
                        "\n" +
                        "\t\t\t<p style='margin-bottom:0cm;'><b>4. 근로시간</b></p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t(1) 시업 및 종업시간 :  <i><span style='font-size:11pt;font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_worktime_start.substring(0, 5) +"</span></i> ~ \n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t<i><span style='font-size:11pt;font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_worktime_end.substring(0,5) +"</span></i></p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:30pt;'>\n" +
                        "\t\t\t\t  휴게시간 : 09:00-09:30, 12:00-13:00, 15:30-16:00</p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\t\t\t  \n" +
                        "\t\t\t\t(2) 근로시간은 휴게시간을 제외하고 1일 8시간, 1주 40시간을 원칙으로 한다.</p>\n" +
                        "\t\t\t\t\n" +
                        "\t\t\t<p style='margin-bottom:0cm;'><b>5. 휴일 및 휴가</b></p>\t\t\t\t\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t (1) 1주일(월~금)간 계속 근로한 경우 토요일은 무급(휴일, 휴무일), 일요일은 유급주휴일로 한다.</p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t (2) 근로자의 날, 연차유급휴가는 근로기준법상 요건에 해당되는 때에 준다.</p>\n" +
                        "\t\t\t\t  \n" +
                        "\t\t\t<p style='margin-bottom:0cm;'><b>6. 근로계약의 해지사유</b></p>\t\t\t\t\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t (1) 정당한 업무지시를 불이행함으로써 사고나 손실을 야기시킨 때</p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t (2) 고의 또는 중대한 과실로 사고나 손실을 야기시킨 때</p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t (3) 신체ㆍ정신상 장애로 해당 업무를 수행할 수 없을 때</p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t (4) 불법체류자로 밝혀진 때</p>\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t (5) 그 외 “갑”의 「현장계약직 취업규칙」상의 해고기준에 해당될 때</p>\n" +
                        "\t\t\t<p style='margin-bottom:0cm;'><b>7. 기타</b></p>\t\t\t\t\n" +
                        "\t\t\t<p style='font-size:9.0pt;margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:14.2pt;'>\n" +
                        "\t\t\t\t (1) 본 계약서상에 명시되지 않은 사항은 근로기준법에 따른다.</p>\n" +
                        "\t\t</td>\n" +
                        "\t</tr>\n" +
                        "\t<tr height=60>\n" +
                        "\t\t<td align=center >\n" +
                        "\t\t\t<b><i style='font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_workdate.substring(0, 4) +"</i> 년 <i style='font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_workdate.substring(5, 7) +"</i> 월 <i style='font-family:\"맑은 고딕\";color:#0070C0'>" + workerjob.job.f_workdate.substring(8, 10) +"</i> 일</b>\n" +
                        "\t\t</td>\n" +
                        "\t</tr>\n" +
                        "</table>\n" +
                        "\n" +
                        "<table border=1 cellspacing=0 cellpadding=0 width=699 style='width:524.25pt;border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;mso-yfti-tbllook:1184;mso-padding-alt:0cm 5.4pt 0cm 5.4pt'>\n" +
                        " <tr >\n" +
                        "  <td width=349 style='width:64.4pt;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p>사용자</p>\n" +
                        "  <p>현장대리인 : <i style='font-weight:bold;color:#0070C0'>㈜" + workerjob.f_buildcompany + " " + workerjob.job.f_owner_name  + "</i> (직인생략)</p>\n" +
                        "  </td>\n" +
                        "  <td width=349 style='width:64.4pt;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                        "  <p>근로자</p>\n" +
                        "  <p>성명 : <i style='font-weight:bold;color:#0070C0'>김일용</i></p>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        "</table>\n" +
                        "\t\n" +
                        "\t\n" +
                        "</body>\n" +
                        "</html>";
                web.putExtra("content",content);
                startActivity(web);
            }
        });
        lb_agent_book.setClickable(true);
        lb_agent_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent web=new Intent(getActivity(), WebViewActivity.class);
                web.putExtra("title","임금지급영수증");
                String content = "<html >\n" +
                        "\n" +
                        "<head></head>\n" +
                        "\n" +
                        "<body lang=KO style='tab-interval:40.0pt'>\n" +
                        "\n" +
                        "<table width=699 height=60>\n" +
                        "\t<tr><td align=center >\n" +
                        "\t\t<b><span style='font-size:22.0pt;mso-bidi-font-size:11.0pt;line-height:107%;font-family:\"맑은 고딕\";'>노무비지급영수증</span></b>\n" +
                        "  </td></tr>\n" +
                        "</table>\n" +
                        "<b>1.작업정보</b>\n" +
                        "<table border=1 cellspacing=0 cellpadding=0 width=699 style='width:524.25pt;border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;mso-yfti-tbllook:1184;'>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t\t현장\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>광교 제 2공구 IPARK</i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t\t건설사\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>㈜튼튼건설 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t원청사\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>대림건설 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t공종\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>골조 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t\t작업일/시간\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>2016년 10월 26일(08:00~17:00) </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t작업내용\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>보통인부(청소) </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t작업자\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>김일용 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t주민번호\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>500101-1234567 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t연락처\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>010-1234-1234 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " </table>\n" +
                        "<br> \n" +
                        "<b>2.지급정보</b>\n" +
                        "<table border=1 cellspacing=0 cellpadding=0 width=699 style='width:524.25pt;border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;mso-yfti-tbllook:1184;'>\n" +
                        "\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t지급금액\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>90,000 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t지급계좌\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>신한 123-12-123456 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t지급일\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>2016년 10월 26일 </i><br>\n" +
                        "  \t<i style='font-size:9pt;color:#0070C0'>(지급정보 처리일시 : 2016.10.26 20:30:10) </i>  \t\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        "  <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t지급처\n" +
                        "  </td>\n" +
                        "  <td width=431 style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "    (주)인투웍스\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " </table>\n" +
                        "<table width=699>\n" +
                        " <tr>\n" +
                        "  <td>\n" +
                        " \t<p style='text-align:left;font-size:10pt'>\n" +
                        "\t본 지급영수증은 가입약관에 따라 김소장 앱에 등록된 지급정보를 기준으로 작성되었으며, ㈜인투웍스가 해당 작업에 대한 노무비를 지급완료했음을 증명합니다.</p>\n" +
                        " \t</td>\n" +
                        " </tr>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>";
                web.putExtra("content", content);

                startActivity(web);
            }
        });
        lb_work_ackbook.setClickable(true);
        lb_work_ackbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent web=new Intent(getActivity(), WebViewActivity.class);
                web.putExtra("title","작업확인서");
                String content = "<html >\n" +
                        "\n" +
                        "<head></head>\n" +
                        "\n" +
                        "<body lang=KO style='tab-interval:40.0pt'>\n" +
                        "\n" +
                        "<table width=699 height=60>\n" +
                        "\t<tr><td align=center >\n" +
                        "\t\t<b><span style='font-size:22.0pt;mso-bidi-font-size:11.0pt;line-height:107%;font-family:\"맑은 고딕\";'>작업확인서</span></b>\n" +
                        "  </td></tr>\n" +
                        "</table>\n" +
                        "\n" +
                        "<b>1. 작업자 정보</b>\n" +
                        "<table border=1 cellspacing=0 cellpadding=0 width=699 style='width:524.25pt;border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;mso-yfti-tbllook:1184;'>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t작업자\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>김일용 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t주민번호\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>500101-1234567 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t연락처\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>010-1234-1234 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        "</table>\n" +
                        "<br>\n" +
                        "<b>2.작업 정보</b>\n" +
                        "<table border=1 cellspacing=0 cellpadding=0 width=699 style='width:524.25pt;border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;mso-yfti-tbllook:1184;'>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t\t현장\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>광교 제 2공구 IPARK</i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t\t건설사\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>㈜튼튼건설 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t원청사\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>대림건설 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t공종\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>골조 </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t\t작업일/시간\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>2016년 10월 26일(08:00~17:00) </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t작업내용\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>보통인부(청소) </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        "\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t작업확인자\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>(주)튼튼건설 김소장(010-1234-1234) </i>\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        " <tr >\n" +
                        "  <td width=170 bgcolor=lightgrey style='font-weight:bold;text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t작업확인일시\t\n" +
                        "  </td>\n" +
                        "  <td width=431 style='text-align:center;border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .5pt;'>\n" +
                        "  \t<i style='color:#0070C0'>2016년 10월 26일 20:30:12 </i> \t\n" +
                        "  </td>\n" +
                        " </tr>\n" +
                        "</table>\n" +
                        "<table width=699>\n" +
                        " <tr>\n" +
                        "  <td>\n" +
                        " \t<p style='text-align:left;font-size:10pt'>\n" +
                        "\t본 작업확인서는 현장근로자가 김소장앱을 통해 작업확인 처리한 정보를 기반으로 작성되었습니다.</p>\n" +
                        " \t</td>\n" +
                        " </tr>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>";
                web.putExtra("content", content);
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

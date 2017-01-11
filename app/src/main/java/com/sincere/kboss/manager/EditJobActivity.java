package com.sincere.kboss.manager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJobManager;
import com.sincere.kboss.stdata.STSelectWorker;
import com.sincere.kboss.widget.TimePickerFragment;

import org.apache.http.Header;
import org.chenglei.widget.datepicker.DatePicker;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Michael on 11/7/2016.
 */
public class EditJobActivity extends ActivityTempl implements DatePickerDialogFragment.DatePickerDialogHandler,
        View.OnClickListener, TimePickerDialogFragment.TimePickerDialogHandler, DatePicker.OnDateChangedListener,TimePickerFragment.TimePickerDialogHandler {
    TextView lblWorkday;
    TextView lblSigninTime;
    ToggleButton tglWorktime;
    TextView lblSkill;
    RelativeLayout rl04; // skill
    EditText edtJobDetail;
    EditText edtWorkerCount;
    EditText edtPayment;
    CheckBox chkAutoMatch;
    Button btnDelete;
    TextView btnConfirm;

    int pagecount;

    Date workday = new Date();
    Date signintime = new Date();
    int skillid = 1;
    private String format = "";
    RelativeLayout rlDetail;

    STJobManager job;
    public final static String EXTRA_JOB = "job";
    Boolean isAPICalling = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        initUI();

        updateUI();
    }

    void initUI() {
        lblWorkday = (TextView) findViewById(R.id.lblWorkday);
        lblSigninTime = (TextView) findViewById(R.id.lblSigninTime);
        tglWorktime = (ToggleButton) findViewById(R.id.tglWorktime);
        lblSkill = (TextView) findViewById(R.id.lblSkill);
        rl04 = (RelativeLayout) findViewById(R.id.rl04);
        edtJobDetail = (EditText) findViewById(R.id.edtJobDetail);
        edtWorkerCount = (EditText) findViewById(R.id.edtWorkerCount);
        edtPayment = (EditText) findViewById(R.id.edtPayment);
        chkAutoMatch = (CheckBox) findViewById(R.id.chkAutoMatch);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnConfirm = (TextView) findViewById(R.id.btnConfirm);

        rlDetail = (RelativeLayout)findViewById(R.id.rl05);

        lblWorkday.setOnClickListener(this);
        lblSigninTime.setOnClickListener(this);
        rl04.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        job = getIntent().getParcelableExtra(EXTRA_JOB);
    }

    void updateUI() {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        try {
            workday = sdf.parse(job.job.f_workdate);
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREAN);
            lblWorkday.setText(dayFormat.format(workday));
        } catch (Exception e) {
            e.printStackTrace();
            workday = today.getTime();
            long daydist = (workday.getTime() - today.getTimeInMillis()) / 1000 / 3600 / 24;
            lblWorkday.setText(Functions.getDateStringWeekday_2((int)daydist));
        }

        Log.e("test","workday:"+job.job.f_workdate.toString() + " "+ workday.toString());
//        long daydist = (workday.getTime() - today.getTimeInMillis()) / 1000 / 3600 / 24;
//        lblWorkday.setText(Functions.getDateStringWeekday_2((int)daydist));

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
        try {
            signintime = sdf2.parse(job.job.f_worktime_start);
        } catch (Exception e2) {
            e2.printStackTrace();
            signintime = today.getTime();
            signintime.setHours(7);
            signintime.setMinutes(0);
        }
        lblSigninTime.setText(String.format("%02d:%02d", signintime.getHours(), signintime.getMinutes()));

        Date signouttime;
        try {
            signouttime = sdf2.parse(job.job.f_worktime_end);
        } catch (Exception e3) {
            e3.printStackTrace();
            signouttime = today.getTime();
            signouttime.setHours(17);
            signouttime.setMinutes(0);
        }

        tglWorktime.setChecked(signouttime.getHours()-signintime.getHours() > 5);

        skillid = job.job.f_skill;
        lblSkill.setText(Functions.getJobsString(String.valueOf(skillid), getApplicationContext()));

        edtJobDetail.setText(job.job.f_detail);
        edtWorkerCount.setText(String.valueOf(job.job.f_worker_count));
        edtPayment.setText(String.valueOf(job.job.f_payment));

        chkAutoMatch.setChecked(job.job.f_automatch == 1);

        if(lblSkill.getText().toString().equals("보통인부")) {
            rlDetail.setVisibility(View.VISIBLE);
        } else rlDetail.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lblWorkday: {
//                Holder holder = new ViewHolder(R.layout.dialog_datepicker);
//                SimpleAdapter adapter = new SimpleAdapter(EditJobActivity.this, false);
//                DialogPlus dialog = new DialogPlus.Builder(EditJobActivity.this)
//                        .setContentHolder(holder)
//                        .setCancelable(true)
//                        .setGravity(DialogPlus.Gravity.CENTER)
//                        .setAdapter(adapter)
//                        .create();
//
//                dialog.show();
//                DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker1);
//
//
//                datePicker.setDate(workday);
//
//                datePicker.setOnDateChangedListener(EditJobActivity.this);

                showDialog(999);
                break;
            }

            case R.id.lblSigninTime: {
                /*
                TimePickerBuilder tpb = new TimePickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment_Light);
                tpb.show();
                */
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");
                ((TimePickerFragment)newFragment).setTimePickerDialogHandlers(EditJobActivity.this);
                break;
            }

            case R.id.rl04: { // skill
                gotoSelectSkillActivity(null);
                break;
            }

            case R.id.btnConfirm: {
                if (checkInput()) {
                    callApiEditJob();
                }
                break;
            }

            case R.id.btnDelete: {

                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.btnYes:
                                callApiGetWorkerCandidates();
                                dialog.dismiss();
                                break;

                            case R.id.btnNo:
                                dialog.dismiss();
                                break;
                        }
                    }
                } ;

                Holder holder = new ViewHolder(R.layout.dialog_yesno);
                SimpleAdapter adapter = new SimpleAdapter(EditJobActivity.this, false);
                DialogPlus dialog = new DialogPlus.Builder(EditJobActivity.this)
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(DialogPlus.Gravity.CENTER)
                        .setAdapter(adapter)
                        .setOnClickListener(clickListener)
                        .create();

                dialog.show();
                break;
            }
        }
    }
    @Override
    public void onSetTime(int hour,int time)
    {
        signintime.setHours(hour);
        signintime.setMinutes(time);
        lblSigninTime.setText(String.format("%02d:%02d",hour,time));
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            Calendar mCalendar;
            mCalendar = Calendar.getInstance();
            mCalendar.setTime(workday);
            return  new DatePickerDialog(this,
                    myDateListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    Calendar today = Calendar.getInstance();
                    Calendar selectedday = Calendar.getInstance();
                    Log.e("test",arg1+" "+arg2+" "+arg3);
                    selectedday.set(arg1, arg2, arg3);

                    if (selectedday.after(today)) {
                        //long daydist = (selectedday.getTimeInMillis() - today.getTimeInMillis()) / 1000 / 3600 / 24;
                        //lblWorkday.setText(Functions.getDateStringWeekday_2((int)daydist));
                        lblWorkday.setText(Functions.getDateStringWeekdayFromDate(selectedday.getTime()));
                        workday = selectedday.getTime();

                    } else {
                        Functions.showToast(EditJobActivity.this, R.string.can_select_after_today);
                    }
                }
            };

    boolean checkInput() {
        String workercount = edtWorkerCount.getText().toString();
        String payment = edtPayment.getText().toString();

        if (workercount.isEmpty()) {
            Functions.showToast(EditJobActivity.this, R.string.input_workercount);
            return false;
        }

        if (payment.isEmpty()) {
            Functions.showToast(EditJobActivity.this, R.string.input_payment);
            return false;
        }

        return true;
    }

    @Override
    public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
        Calendar today = Calendar.getInstance();
        Calendar selectedday = Calendar.getInstance();

        selectedday.set(year, monthOfYear, dayOfMonth);

        if (selectedday.after(today)) {
            long daydist = (selectedday.getTimeInMillis() - today.getTimeInMillis()) / 1000 / 3600 / 24;
            lblWorkday.setText(Functions.getDateStringWeekday_2((int)daydist));
            workday = selectedday.getTime();

        } else {
            Functions.showToast(EditJobActivity.this, R.string.can_select_after_today);
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar today = Calendar.getInstance();
        Calendar selectedday = Calendar.getInstance();

        selectedday.set(year, monthOfYear-1, dayOfMonth);

        if (selectedday.after(today)) {
            long daydist = (selectedday.getTimeInMillis() - today.getTimeInMillis()) / 1000 / 3600 / 24;
            lblWorkday.setText(Functions.getDateStringWeekday_2((int)daydist));
            workday = selectedday.getTime();

        } else {
            Functions.showToast(EditJobActivity.this, R.string.can_select_after_today);
        }
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        signintime.setHours(hourOfDay);
        signintime.setMinutes(minute);
        lblSigninTime.setText(String.format("%02d:%02d", hourOfDay, minute));
    }

    public void gotoSelectSkillActivity(View v) {
        Intent i = new Intent(this, SelectSkillActivity.class);
        i.putExtra(SelectSkillActivity.EXTRA_SKILL_ID, skillid);
        startActivityForResult(i, SelectSkillActivity.RESULT_SKILL);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SelectSkillActivity.RESULT_SKILL &&
                resultCode == SelectSkillActivity.RESULT_SKILL) {
            skillid = data.getIntExtra(SelectSkillActivity.EXTRA_SKILL_ID, 1);
            lblSkill.setText(Functions.getJobsString(String.valueOf(skillid), getApplicationContext()));
            if(lblSkill.getText().toString().equals("보통인부")) {
                rlDetail.setVisibility(View.VISIBLE);
            } else rlDetail.setVisibility(View.GONE);
        }
    }

    void callApiEditJob() {
        if(isAPICalling ) return;
        if (job.job.f_spot_id == 0 || job.job.f_id == 0) {
            return;
        }
        isAPICalling  = true;
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(EditJobActivity.this, retVal.msg);

                } else {
                    returnBack(null);
                    int spot_id;
                    if (MainActivity.g_curSpot < 0) {
                        spot_id = 0;
                    } else {
                        spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
                    }
                    for(int i = ManageSpotFragment.curFrag;i<4;i++)
                        ManageSpotFragment.registeredFragments.get(i).updateJobList(Functions.getDateTimeStringFromToday(i), spot_id);
                }
                isAPICalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgress();
                isAPICalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                hideProgress();
                isAPICalling = false;
            }
        };

        showProgress();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String jobdate = sdf.format(workday);
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
        String worktime_start = sdf2.format(signintime);
        Date signouttime = signintime;
        boolean wholeday = tglWorktime.isChecked();
        signouttime.setHours(signintime.getHours() + (wholeday?10:5));
        String worktime_end = sdf2.format(signouttime);
        String jobdetail = edtJobDetail.getText().toString();
        String workercount = edtWorkerCount.getText().toString();
        String payment = edtPayment.getText().toString();
        int automatch = chkAutoMatch.isChecked() ? 1 : 0;

        ServiceManager.inst.editJob(job.job.f_spot_id, job.job.f_id, jobdate, worktime_start, worktime_end,
                skillid, jobdetail, workercount, payment, automatch, handler);
    }

    void callApiCancelJob() {
        if(isAPICalling) return;
        if (job.job.f_spot_id == 0 || job.job.f_id == 0) {
            return;
        }
        isAPICalling = true;
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(EditJobActivity.this, retVal.msg);

                } else {
                    returnBack(null);
                    int spot_id;
                    if (MainActivity.g_curSpot < 0) {
                        spot_id = 0;
                    } else {
                        spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
                    }

                    //modified by Adonis
                    //ManageSpotFragment.registeredFragments.get(ManageSpotFragment.curFrag).updateJobList(Functions.getDateTimeStringFromToday(ManageSpotFragment.curFrag), spot_id);
                    ManageSpotFragment.registeredFragments.get(ManageSpotFragment.curFrag).updateJobList(Functions.getDateTimeStringFromToday(ManageSpotFragment.curFrag - 28), spot_id);
                }
                isAPICalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgress();
                isAPICalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                hideProgress();
                isAPICalling = false;
            }
        };

        showProgress();

        ServiceManager.inst.cancelJob(job.job.f_spot_id, job.job.f_id, handler);
    }

    void callApiGetWorkerCandidates() {

        callApiCancelJob();

        //comment removed by Adonis
/*        pagecount = 0;
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                Log.e("test",response.toString());
                ArrayList<STSelectWorker> newWorkers = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetWorkerCandidates(response, newWorkers);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    pagecount ++;
                    if ( newWorkers.size()==0 )
                    {
                        callApiCancelJob();
                    }
                    else
                    {
                        Functions.showToast(EditJobActivity.this, "지원자가 있어 작업을 취소할수 없습니다.");
                    }
                    // adapter.setData(jobs);
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

        ServiceManager.inst.getWorkerCandidates(job.job.f_id, pagecount, ServiceParams.PAGE_SIZE, handler);*/

    }

}

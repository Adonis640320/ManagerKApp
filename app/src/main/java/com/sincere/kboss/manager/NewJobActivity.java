package com.sincere.kboss.manager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.widget.TimePickerFragment;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.widget.DatePicker;

/**
 * Created by Michael on 11/7/2016.
 */
public class NewJobActivity extends ActivityTempl implements DatePickerDialogFragment.DatePickerDialogHandler,
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
    Button btnSave;

    Date workday = new Date();
    Date signintime = new Date();
    int skillid = 1;
    int spot_id = 0;
    RelativeLayout rlDetail;

    public final static String EXTRA_SPOT_ID = "spot_id";
    public final static String SELECTED_DATE_STR = "selected_date"; // added by Adonis
    Boolean isAPICalling = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job);
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
        btnSave = (Button) findViewById(R.id.btnSave);

        rlDetail = (RelativeLayout)findViewById(R.id.rl05);

        lblWorkday.setOnClickListener(this);
        lblSigninTime.setOnClickListener(this);
        rl04.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        spot_id = getIntent().getIntExtra(EXTRA_SPOT_ID, 0);
    }

    void updateUI() {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 1);
        workday = today.getTime();

// commented by Adonis
//        lblWorkday.setText(Functions.getDateStringWeekday_2(1));
        lblWorkday.setText( getIntent().getCharSequenceExtra(SELECTED_DATE_STR) );

        String selectedDateStr = getIntent().getCharSequenceExtra(SELECTED_DATE_STR).toString();

        int year = 100 + Character.getNumericValue(selectedDateStr.charAt(0)) * 10 + Character.getNumericValue(selectedDateStr.charAt(1));
        int month = Character.getNumericValue(selectedDateStr.charAt(3)) * 10 + Character.getNumericValue(selectedDateStr.charAt(4));
        int da = Character.getNumericValue(selectedDateStr.charAt(6)) * 10 + Character.getNumericValue(selectedDateStr.charAt(7));
        workday = new Date(year, month - 1, da );

        signintime.setHours(7);
        signintime.setMinutes(0);
        lblSigninTime.setText("07:00");

        lblSkill.setText(Functions.getJobsString(String.valueOf(skillid), getApplicationContext()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lblWorkday: {
//                Holder holder = new ViewHolder(R.layout.dialog_datepicker);
//                SimpleAdapter adapter = new SimpleAdapter(NewJobActivity.this, false);
//                DialogPlus dialog = new DialogPlus.Builder(NewJobActivity.this)
//                        .setContentHolder(holder)
//                        .setCancelable(true)
//                        .setGravity(DialogPlus.Gravity.CENTER)
//                        .setAdapter(adapter)
//                        .create();
//
//                dialog.show();
//                DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker1);
//
//                datePicker.setDate(workday);
//
//                datePicker.setOnDateChangedListener(NewJobActivity.this);
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
                ((TimePickerFragment)newFragment).setTimePickerDialogHandlers(NewJobActivity.this);
                break;
            }

            case R.id.rl04: { // skill
                gotoSelectSkillActivity(null);
                break;
            }

            case R.id.btnSave: {
                if (checkInput()) {
                    callApiAddJob();
                }
                break;
            }
        }
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
                public void onDateSet(DatePicker arg0,
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
                        Functions.showToast(NewJobActivity.this, R.string.can_select_after_today);
                    }
                }
            };

    boolean checkInput() {
        String workercount = edtWorkerCount.getText().toString();
        String payment = edtPayment.getText().toString();

        if (workercount.isEmpty()) {
            Functions.showToast(NewJobActivity.this, R.string.input_workercount);
            return false;
        }

        if (payment.isEmpty()) {
            Functions.showToast(NewJobActivity.this, R.string.input_payment);
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
            Functions.showToast(NewJobActivity.this, R.string.can_select_after_today);
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
            Functions.showToast(NewJobActivity.this, R.string.can_select_after_today);
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

    void callApiAddJob() {
        if(isAPICalling) return;
        if (spot_id == 0) {
            return;
        }
        isAPICalling = true;

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                Log.e("test","New Job:"+response.toString());
                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(NewJobActivity.this, retVal.msg);
                    isAPICalling = false;
                } else {
                    returnBack(null);
                    int spot_id;
                    if (MainActivity.g_curSpot < 0) {
                        spot_id = 0;
                    } else {
                        spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
                    }
                    SimpleDateFormat dayFormat = new SimpleDateFormat("yy.MM.dd(E)", Locale.KOREAN);
                    String weekDay = dayFormat.format(workday);

                    //modified by Adonis
                    int pos = 0;
                    //for(int i=0;i<4;i++) {
                    for(int i=0;i<57;i++) {
                        if(weekDay.equals(Functions.getDateStringWeekday(i - 28))) {
                            pos = i;
                            break;
                        }
                    }

                    // ManageSpotFragment.registeredFragments.get(pos).updateJobList(Functions.getDateTimeStringFromToday(pos), spot_id);
                    ManageSpotFragment.registeredFragments.get(pos).updateJobList(Functions.getDateTimeStringFromToday(pos - 28), spot_id); // modified by Adonis
                }
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

        ServiceManager.inst.addJob(spot_id, jobdate, worktime_start, worktime_end,
                skillid, jobdetail, workercount, payment, automatch, handler);
    }
}

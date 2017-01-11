package com.sincere.kboss;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.manager.NewJobActivity;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STUserInfo;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.chenglei.widget.datepicker.DatePicker;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Michael on 10/26/2016.
 */
public class BasicSecurityActivity extends ActivityTempl implements DatePickerDialogFragment.DatePickerDialogHandler, DatePicker.OnDateChangedListener {
    LinearLayout ll01;
    ImageView imgCert;
    TextView lblBasicSecDate;
    ImageView btnBack;
    Button btnDelete;
    TextView tvGuide;

    String photoPath = "";
    String birthday_db;
    boolean set = false;
    boolean f_remove=false;
    DisplayImageOptions options;

    final static int INTENT_SELECT_PICTURE = 200;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( f_remove )
            {
                callApiDeleteBasicSec();
            }
            if ( set )
            {
                callApiSetBasicSec();
            }
            if ( !f_remove && !set )
                returnBack(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basicsecurity);

        initUI();
        birthday_db = KbossApplication.g_userinfo.f_basicsec_date;
        updateUI();
    }

    void initUI() {
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.cert_templ)
                .showImageOnFail(R.drawable.cert_templ)
                .showImageOnLoading(R.drawable.cert_templ)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        f_remove=false;set=false;

        tvGuide = (TextView) findViewById(R.id.tvGuide);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String certuri = KbossApplication.g_userinfo.f_basicsec_cert;
                if (KbossApplication.g_basicsec_img == null && certuri.isEmpty()) return;

                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.btnYes:
                                f_remove=true;
                                KbossApplication.g_userinfo.f_basicsec_date = "";
                                KbossApplication.g_userinfo.f_basicsec_cert = "";
                                KbossApplication.g_basicsec_img = null;

                                updateUI();
                                dialog.dismiss();
                                break;

                            case R.id.btnNo:
                                dialog.dismiss();
                                break;
                        }
                    }
                } ;

                Holder holder = new ViewHolder(R.layout.dialog_yesno);
                SimpleAdapter adapter = new SimpleAdapter(BasicSecurityActivity.this, false);
                DialogPlus dialog = new DialogPlus.Builder(BasicSecurityActivity.this)
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(DialogPlus.Gravity.CENTER)
                        .setAdapter(adapter)
                        .setOnClickListener(clickListener)
                        .create();

                dialog.show();

                Button btnYes = (Button)dialog.findViewById(R.id.btnYes);
                btnYes.setBackgroundColor( getResources().getColor(R.color.clr_red_dark));

            }
        });
        TextView ack=(TextView)findViewById(R.id.btnConfirm);
        ack.setClickable(true);
        ack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("secur","1111111");
                if ( f_remove )
                {
                    callApiDeleteBasicSec();
                }
                if ( set )
                {
                    callApiSetBasicSec();
                }
                if ( !f_remove && !set )
                    returnBack(null);
            }
        });
        ll01 = (LinearLayout) findViewById(R.id.ll01);
        ll01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Holder holder = new ViewHolder(R.layout.dialog_datepicker);
                SimpleAdapter adapter = new SimpleAdapter(BasicSecurityActivity.this, false);
                DialogPlus dialog = new DialogPlus.Builder(BasicSecurityActivity.this)
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(DialogPlus.Gravity.CENTER)
                        .setAdapter(adapter)
                        .create();

                dialog.show();
                DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker1);

                Date today = new Date();
                if (birthday_db.isEmpty()) {
                    datePicker.setDate(today);

                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date date = sdf.parse(birthday_db);
                        datePicker.setDate(date);

                    } catch (Exception e) {
                        datePicker.setDate(today);
                    }
                }

                datePicker.setOnDateChangedListener(BasicSecurityActivity.this);*/


                showDialog(999);
            }
        });

        imgCert = (ImageView) findViewById(R.id.imgCert);
        imgCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*
                Intent intent = new Intent(BasicSecurityActivity.this, SelectPhotoActivity.class);
                startActivityForResult(intent, INTENT_SELECT_PICTURE);*/

                Intent intent = new Intent(BasicSecurityActivity.this, PhotoSelectMainActivity.class);
                startActivityForResult(intent, INTENT_SELECT_PICTURE);

            }
        });

        lblBasicSecDate = (TextView) findViewById(R.id.lblBasicSecDate);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            Date today = new Date();
            Calendar mCalendar;
            mCalendar = Calendar.getInstance();

            if (birthday_db.isEmpty()) {
                mCalendar.setTime(today);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = sdf.parse(birthday_db);
                    mCalendar.setTime(date);

                } catch (Exception e) {
                    mCalendar.setTime(today);
                }
            }
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
                    String birthday = String.format("%d년 %d월 %d일", arg1, arg2+1, arg3);
                    birthday_db = String.format("%04d-%02d-%02d 00:00:00", arg1, arg2+1, arg3);
                    lblBasicSecDate.setText(birthday);
                    set = true;
                }
            };

    @Override
    public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
        String birthday = String.format("%d년 %d월 %d일", year, monthOfYear+1, dayOfMonth);
        birthday_db = String.format("%04d-%02d-%02d 00:00:00", year, monthOfYear+1, dayOfMonth);
        lblBasicSecDate.setText(birthday);
        set = true;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String birthday = String.format("%d년 %d월 %d일", year, monthOfYear, dayOfMonth);
        birthday_db = String.format("%04d-%02d-%02d 00:00:00", year, monthOfYear, dayOfMonth);
        lblBasicSecDate.setText(birthday);
        set = true;
    }

    void updateUI() {

        if (birthday_db.isEmpty() || birthday_db.equals("0000-00-00 00:00:00")) {
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            birthday_db = sdf.format(today);
        }

        String basicsecdate = Functions.getDateString(birthday_db);
        lblBasicSecDate.setText(basicsecdate);

        if (KbossApplication.g_basicsec_img != null) {
            imgCert.setImageBitmap(KbossApplication.g_basicsec_img);
        } else {
            String certuri = KbossApplication.g_userinfo.f_basicsec_cert;
            if (!certuri.isEmpty()) {
                KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + "basicsec/"+certuri, imgCert, options);
            } else {
                imgCert.setImageResource(R.drawable.cert_templ);
            }
        }

        String certuri = KbossApplication.g_userinfo.f_basicsec_cert;
        if (KbossApplication.g_basicsec_img == null && certuri.isEmpty()) {
            btnDelete.setVisibility(View.GONE);
            tvGuide.setVisibility(View.VISIBLE);
        }
        else {
            btnDelete.setVisibility(View.VISIBLE);
            tvGuide.setVisibility(View.GONE);
        }
    }

    private void updateBasicSecWithPath(String szPath)
    {
        try {
			/* Update user photo info view */
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(szPath, options);

            if (bitmap != null)
            {
                int nWidth = bitmap.getWidth(), nHeight = bitmap.getHeight();
                int nScaledWidth = 0, nScaledHeight = 0;
                if (nWidth > nHeight)
                {
                    nScaledWidth = SelectPhotoActivity.IMAGE_WIDTH;
                    nScaledHeight = nScaledWidth * nHeight / nWidth;
                }
                else
                {
                    nScaledHeight = SelectPhotoActivity.IMAGE_HEIGHT;
                    nScaledWidth = nScaledHeight * nWidth / nHeight;
                }

                KbossApplication.g_basicsec_img = Bitmap.createScaledBitmap(bitmap, nScaledWidth, nScaledHeight, false);
                imgCert.setImageBitmap(KbossApplication.g_basicsec_img);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                f_remove=false;
                Uri fileUri = data.getParcelableExtra(SelectPhotoActivity.szRetUri);

                String filepath = data.getStringExtra(SelectPhotoActivity.szRetPath);
                int rescode = data.getIntExtra(SelectPhotoActivity.szRetCode, SelectPhotoActivity.nRetCancelled);
                if (rescode == SelectPhotoActivity.nRetSuccess) {
                    if (fileUri == null) {
                        photoPath = filepath;
                    } else {
                        //photoPath = fileUri.getPath();
                        String uri = fileUri.toString();
                        String[] projection = {MediaStore.Images.Media.DATA};
                        String where = MediaStore.Images.Media._ID + " = " + uri.substring(uri.lastIndexOf("/") + 1);
                        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, null );
                        if(cursor.moveToFirst()){
                            photoPath = cursor.getString(0);
                        } else {
                            photoPath = "";
                        }
                        cursor.close();
                    }

                    updateBasicSecWithPath(photoPath);
                    set = true;
                    updateUI();
                    //Toast.makeText(this, "select picture->  path:" + photoPath, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Occur error during selecting picture", Toast.LENGTH_SHORT).show();
                    photoPath = "";
                }
            }
        }
    }



    void callApiSetBasicSec() {
        if (set == false) {
            returnBack(null);
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
                    Functions.showToast(BasicSecurityActivity.this, retVal.msg);
                } else {
                    KbossApplication.g_userinfo.f_basicsec_date = birthday_db;
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

        ServiceManager.inst.setBasicSec(birthday_db, photoPath, handler);
    }

    void callApiDeleteBasicSec() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(BasicSecurityActivity.this, retVal.msg);
                } else {
                    KbossApplication.g_userinfo.f_basicsec_date = "";
                    KbossApplication.g_userinfo.f_basicsec_cert = "";
                    KbossApplication.g_basicsec_img = null;

                    updateUI();
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

        ServiceManager.inst.deleteBasicSec(handler);
    }

    public void onClickBasicInfo(View v)
    {
        Intent intent  = new Intent(this, WebViewActivity.class);
        intent.putExtra("url","");
        intent.putExtra("title", getResources().getString(R.string.personalinfosetting_basicsecurityinfo));
        startActivity(intent);
    }

    public void onConfirmPressed(View view)
    {
    }

    @Override
    public void onBackPressed() {
        btnBackClickListener.onClick(null);
    }
}


package com.sincere.kboss;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.manager.EditJobActivity;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STUserInfo;
import com.sincere.kboss.utils.CircularImageView;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.chenglei.widget.datepicker.DatePicker;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import whdghks913.tistory.examplebroadcastreceiver.Broadcast;

/**
 * Created by Michael on 2016.10.26.
 */
public class RegisterInputActivity extends ActivityTempl implements DatePickerDialogFragment.DatePickerDialogHandler, View.OnClickListener, DatePicker.OnDateChangedListener {
    CheckBox manCheckbox;
    CheckBox womanCheckbox;
    EditText nameEdit;
    TextView birthdayEdit;
    EditText detailsignupinfo_elector_text;
    EditText detailsignupinfo_email_text;
    CheckBox detailsignupinfo_man;
    EditText detailphone_text;
    RelativeLayout rlyProfileImage;
    CircularImageView circlePhoto;
    LinearLayout llWhole, llScroll;

    String photoPath = "";
    String birthday_db="";
    Integer memberid = 0;
    String authkey = "";
    KbossApplication m_app;
    final static int INTENT_SELECT_PICTURE = 200;

    View.OnClickListener hideKeyboard = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Functions.hideVirtualKeyboard(RegisterInputActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerinput);
        String token = FirebaseInstanceId.getInstance().getToken();
        KbossApplication.setToken(token);
        Log.e("test","RegisterInput Token:"+token);

        initUI();
    }

    private void initUI() {
        findViewById(R.id.imv_select_picture).setOnClickListener(RegisterInputActivity.this);
        rlyProfileImage = (RelativeLayout) findViewById(R.id.rly_profile_image);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);

        nameEdit = (EditText) findViewById(R.id.detailsignupinfo_name_text);
        birthdayEdit = (TextView) findViewById(R.id.detailsignupinfo_birthday_text);
        detailsignupinfo_elector_text = (EditText) findViewById(R.id.detailsignupinfo_elector_text);
        detailsignupinfo_email_text = (EditText) findViewById(R.id.detailsignupinfo_email_text);
        detailsignupinfo_man = (CheckBox) findViewById(R.id.detailsignupinfo_man);
        detailphone_text=(EditText)findViewById(R.id.detailsignupinfo_phone_text);
        detailphone_text.setText(KbossApplication.g_mphone);
        circlePhoto = (CircularImageView) findViewById(R.id.circlePhoto);
        circlePhoto.setOnClickListener(RegisterInputActivity.this);

        birthdayEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Holder holder = new ViewHolder(R.layout.dialog_datepicker);
                SimpleAdapter adapter = new SimpleAdapter(RegisterInputActivity.this, false);
                DialogPlus dialog = new DialogPlus.Builder(RegisterInputActivity.this)
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(DialogPlus.Gravity.CENTER)
                        .setAdapter(adapter)
                        .create();

                dialog.show();
                DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date date = new Date();
                try {
                    if (birthday_db.isEmpty()) {
                        date = sdf.parse("1980-01-01 00:00:00");
                        datePicker.setDate(date);

                    } else {
                        datePicker.setDate(sdf.parse(birthday_db));
                    }
                } catch (Exception e) {
                    datePicker.setDate(date);
                }

                datePicker.setOnDateChangedListener(RegisterInputActivity.this);
            }
        });
        m_app = KbossApplication.getInstance();
        nameEdit.setText(m_app.getSharedPreferencesData("name",""));
        birthdayEdit.setText(m_app.getSharedPreferencesData("birthday",""));
        detailsignupinfo_email_text.setText(m_app.getSharedPreferencesData("email",""));
        detailsignupinfo_elector_text.setText(m_app.getSharedPreferencesData("elector",""));
//        detailsignupinfo_man.setText(m_app.getSharedPreferencesData("man",""));
        photoPath=m_app.getSharedPreferencesData("photopath","");
        boolean sex=m_app.getSharedPreferencesData("sex",false);
        manCheckbox = (CheckBox) findViewById(R.id.detailsignupinfo_man);
        womanCheckbox = (CheckBox) findViewById(R.id.detailsignupinfo_woman);
        if ( sex ) {
            manCheckbox.setChecked(true);
            womanCheckbox.setChecked(false);
        }else
        {
            manCheckbox.setChecked(false);
            womanCheckbox.setChecked(true);
        }
        llWhole = (LinearLayout) findViewById(R.id.llWhole);
        llScroll = (LinearLayout) findViewById(R.id.llScroll);
        llWhole.setOnClickListener(hideKeyboard);
        llScroll.setOnClickListener(hideKeyboard);
    }

    @Override
    public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
        String birthday = String.format("%d년 %d월 %d일", year, monthOfYear+1, dayOfMonth);
        birthday_db = String.format("%04d-%02d-%02d 00:00:00", year, monthOfYear+1, dayOfMonth);
        birthdayEdit.setText(birthday);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String birthday = String.format("%d년 %d월 %d일", year, monthOfYear, dayOfMonth);
        birthday_db = String.format("%04d-%02d-%02d 00:00:00", year, monthOfYear, dayOfMonth);
        birthdayEdit.setText(birthday);
    }

    private void updateUserImageWithPath(String szPath)
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

                KbossApplication.g_user_photo = Bitmap.createScaledBitmap(bitmap, nScaledWidth, nScaledHeight, false);
                circlePhoto.setImageBitmap(KbossApplication.g_user_photo);
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

                    updateUserImageWithPath(photoPath);
                    //Toast.makeText(this, "select picture->  path:" + photoPath, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Occur error during selecting picture", Toast.LENGTH_SHORT).show();
                    photoPath = "";
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circlePhoto:
            case R.id.imv_select_picture: {
                Intent intent = new Intent(this, SelectPhotoActivity.class);
                startActivityForResult(intent, INTENT_SELECT_PICTURE);
            }
            break;

            default:
                break;
        }
    }

    public void gotoRegisterCompleteActivity(View v) {
        if (checkInput()) {
            callApiRegister();
        }
    }

    public void onManClicked(View v) {
        manCheckbox = (CheckBox) findViewById(R.id.detailsignupinfo_man);
        womanCheckbox = (CheckBox) findViewById(R.id.detailsignupinfo_woman);

        manCheckbox.setChecked(true);
        womanCheckbox.setChecked(false);
    }

    public void onWomanClicked(View v) {
        manCheckbox = (CheckBox) findViewById(R.id.detailsignupinfo_man);
        womanCheckbox = (CheckBox) findViewById(R.id.detailsignupinfo_woman);

        manCheckbox.setChecked(false);
        womanCheckbox.setChecked(true);
    }

    boolean checkInput() {
        if (nameEdit.getText().toString().isEmpty()) {
            Functions.showToast(RegisterInputActivity.this, R.string.detailsignupinfo_name_placeholder_en);
            return false;
        }

        if (birthdayEdit.getText().toString().isEmpty()) {
            Functions.showToast(RegisterInputActivity.this, R.string.detailsignupinfo_birthday_placeholder_en);
            return false;
        }
        String phone = detailphone_text.getText().toString();
        if(phone.isEmpty()) {
            Functions.showToast(RegisterInputActivity.this, R.string.detailsignupinfo_phone_placeholder_en);
            return false;
        }

        if (!phone.isEmpty() && phone.length() != 11) {
            Functions.showToast(RegisterInputActivity.this, R.string.mphone_must_11_chars);
            return false;
        }
/*
        String recphone = detailsignupinfo_elector_text.getText().toString();
        if(recphone.isEmpty()) {
            Functions.showToast(RegisterInputActivity.this, R.string.detailsignupinfo_elector_placeholder_en1);
            return false;
        }

        if (!recphone.isEmpty() && recphone.length() != 11) {
            Functions.showToast(RegisterInputActivity.this, R.string.mphone_must_11_chars);
            return false;
        }
        */
        String email = detailsignupinfo_email_text.getText().toString();
        if (!email.isEmpty() && !Functions.isValidEmail(email)) {
            Functions.showToast(RegisterInputActivity.this, R.string.incorrect_email);
            return false;
        }

        return true;
    }

    void callApiRegister() {
        if (KbossApplication.UI_TEST) {
            Intent i = new Intent(this, RegisterCompleteActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
            finish();

        } else {
            final String name = nameEdit.getText().toString();
            String birthday = birthday_db;
            final String email = detailsignupinfo_email_text.getText().toString();
            final String phone=detailphone_text.getText().toString();
            final String recphone = detailsignupinfo_elector_text.getText().toString();
            final int gender = detailsignupinfo_man.isChecked() ? 1 : 0;

            handler = new JsonHttpResponseHandler() {
                RetVal retVal;

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    hideProgress();

                    retVal = ServiceManager.inst.parseSignup(response);
                    if (retVal.code != ServiceParams.ERR_NONE) {
                        Functions.showToast(RegisterInputActivity.this, retVal.msg);
                    } else {
                        Intent i = new Intent(RegisterInputActivity.this, RegisterCompleteActivity.class);

                        // Build userinfo structure
                        //KbossApplication.g_userinfo = new STUserInfo();
                        //KbossApplication.g_userinfo.f_id = memberid;
                        KbossApplication.g_mphone=detailphone_text.getText().toString();
                        KbossApplication.g_userinfo.f_mphone = KbossApplication.g_mphone;
                        KbossApplication.g_userinfo.f_birthday = birthday_db;
                        KbossApplication.g_userinfo.f_name = name;
                        KbossApplication.g_userinfo.f_email = email;
                        KbossApplication.g_userinfo.f_rec_phone = recphone;
                        KbossApplication.g_userinfo.f_gender = gender;
                        //KbossApplication.g_userinfo.f_authkey = authkey;

                        Functions.saveUserInfo(getApplicationContext());

                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        finish();
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

            ServiceManager.inst.signup(KbossApplication.g_mphone, name, email, birthday, recphone, gender, photoPath, handler);
        }
    }
    public void setInputdatas()
    {
        m_app = KbossApplication.getInstance();
        m_app.setSharedPreferencesData("name",nameEdit.getText().toString());
        m_app.setSharedPreferencesData("birthday",birthdayEdit.getText().toString());
        m_app.setSharedPreferencesData("email",detailsignupinfo_email_text.getText().toString());
        m_app.setSharedPreferencesData("elector",detailsignupinfo_elector_text.getText().toString());
        m_app.setSharedPreferencesData("man",detailsignupinfo_man.getText().toString());
        m_app.setSharedPreferencesData("photopath",photoPath);

        if ( manCheckbox.isSelected() )
            m_app.setSharedPreferencesData("sex",true);
        else if ( womanCheckbox.isSelected() )
            m_app.setSharedPreferencesData("sex",false);
    }
    public void backClick(View view){
        setInputdatas();
        returnBack(view);
    }
}

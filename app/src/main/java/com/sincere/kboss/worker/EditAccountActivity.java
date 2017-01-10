package com.sincere.kboss.worker;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.SelectPhotoActivity;
import com.sincere.kboss.global.Functions;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Michael on 10/26/2016.
 */
public class EditAccountActivity extends ActivityTempl implements DatePickerDialogFragment.DatePickerDialogHandler, DatePicker.OnDateChangedListener, View.OnClickListener {
    CheckBox manCheckbox;
    CheckBox womanCheckbox;
    EditText edtMphone;
    EditText nameEdit;
    TextView birthdayEdit;
    EditText edtAuthCode;
    EditText detailsignupinfo_email_text;
    CheckBox detailsignupinfo_man;
    RelativeLayout rlyProfileImage;
    CircularImageView circlePhoto;
    LinearLayout llWhole, llScroll;
    Button btnEdit;

    TextView btnAuthReq;
    TextView btnAuth;

    String photoPath = "";
    String birthday_db = "";
    boolean isAuth = false;

    DisplayImageOptions options;

    final static int INTENT_SELECT_PICTURE = 200;

    ImageView btnBack;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkInput()) {
                callApiEditAccount();
            }
            //returnBack(null);
        }
    };

    View.OnClickListener hideKeyboard = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // hide soft keyboard
            InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editaccount_worker);

        initUI();

        updateUI();
    }

    private void initUI() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.photo)
                .showImageOnFail(R.drawable.photo)
                .showImageOnLoading(R.drawable.photo)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        findViewById(R.id.imv_select_picture).setOnClickListener(EditAccountActivity.this);
        rlyProfileImage = (RelativeLayout) findViewById(R.id.rly_profile_image);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        edtMphone = (EditText) findViewById(R.id.edtMphone);
        nameEdit = (EditText) findViewById(R.id.detailsignupinfo_name_text);
        birthdayEdit = (TextView) findViewById(R.id.detailsignupinfo_birthday_text);
        detailsignupinfo_email_text = (EditText) findViewById(R.id.detailsignupinfo_email_text);
        detailsignupinfo_man = (CheckBox) findViewById(R.id.detailsignupinfo_man);
        edtAuthCode = (EditText) findViewById(R.id.edtAuthCode);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    callApiEditAccount();
                }
            }
        });

        btnAuthReq = (TextView) findViewById(R.id.btnAuthReq);
        btnAuthReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerifyRequest(null);
            }
        });
        btnAuth = (TextView) findViewById(R.id.btnAuth);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify(null);
            }
        });

        circlePhoto = (CircularImageView) findViewById(R.id.circlePhoto);
        circlePhoto.setOnClickListener(EditAccountActivity.this);

        birthdayEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Holder holder = new ViewHolder(R.layout.dialog_datepicker);
                SimpleAdapter adapter = new SimpleAdapter(EditAccountActivity.this, false);
                DialogPlus dialog = new DialogPlus.Builder(EditAccountActivity.this)
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

                datePicker.setOnDateChangedListener(EditAccountActivity.this);
            }
        });

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

    void updateUI() {
        birthday_db = KbossApplication.g_userinfo.f_birthday;

        nameEdit.setText(KbossApplication.g_userinfo.f_name);
        edtMphone.setText(KbossApplication.g_userinfo.f_mphone);
        birthdayEdit.setText(Functions.getDateString2(KbossApplication.g_userinfo.f_birthday));
        detailsignupinfo_email_text.setText(KbossApplication.g_userinfo.f_email);

        if (KbossApplication.g_user_photo != null) {
            circlePhoto.setImageBitmap(KbossApplication.g_user_photo);
        } else {
            String photouri = KbossApplication.g_userinfo.f_photo;
            if (!photouri.isEmpty()) {
                KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + photouri, circlePhoto, options);
            }
        }

        if (KbossApplication.g_userinfo.f_gender == 1) {
            onManClicked(null);
        } else {
            onWomanClicked(null);
        }
    }

    public void sendVerifyRequest(View v) {
        String phone = edtMphone.getText().toString();
        if (phone.isEmpty()) {
            Functions.showToast(EditAccountActivity.this, R.string.detailsignupinfo_elector_placeholder_en);
            return;
        }
        if(phone.equals(KbossApplication.g_userinfo.f_mphone)) {
            Functions.showToast(EditAccountActivity.this, R.string.exist_phone);
            return;
        }

        handler = new JsonHttpResponseHandler()
        {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code == ServiceParams.ERR_NONE) {
                    Functions.showToast(EditAccountActivity.this, R.string.success_send_authreq);
                } else {
                    Functions.showToast(EditAccountActivity.this, retVal.msg);
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

        ServiceManager.inst.sendAuthReq(phone, handler);
    }

    public void verify(View v) {
        final String phone = edtMphone.getText().toString();
        String authcode = edtAuthCode.getText().toString();
        if (phone.isEmpty()) {
            Functions.showToast(EditAccountActivity.this, R.string.detailsignupinfo_elector_placeholder_en);
            return;
        }
        if (authcode.isEmpty()) {
            Functions.showToast(EditAccountActivity.this, R.string.input_authcode);
            return;
        }

        handler = new JsonHttpResponseHandler()
        {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                ArrayList<STUserInfo> users = new ArrayList<>();
                retVal = ServiceManager.inst.parseLogin(response, users);
                if (retVal.code == ServiceParams.ERR_NONE || retVal.code == -5) {
                    Functions.showToast(EditAccountActivity.this, R.string.success_auth);
                    isAuth = true;
                    //KbossApplication.g_userinfo = users.get(0);
                    KbossApplication.g_userinfo.f_mphone = phone;
                } else {
                    Functions.showToast(EditAccountActivity.this, retVal.msg);
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

        ServiceManager.inst.login(phone, authcode, handler);
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
        if (edtMphone.getText().toString().isEmpty()) {
            Functions.showToast(EditAccountActivity.this, R.string.detailsignupinfo_elector_placeholder_en);
            return false;
        }

        if (nameEdit.getText().toString().isEmpty()) {
            Functions.showToast(EditAccountActivity.this, R.string.detailsignupinfo_name_placeholder_en);
            return false;
        }

        if (birthdayEdit.getText().toString().isEmpty()) {
            Functions.showToast(EditAccountActivity.this, R.string.detailsignupinfo_birthday_placeholder_en);
            return false;
        }

        if (!edtMphone.getText().toString().equals(KbossApplication.g_userinfo.f_mphone) && edtAuthCode.getText().toString().isEmpty()) {
            Functions.showToast(EditAccountActivity.this, R.string.input_authcode);
            return false;
        }

        String email = detailsignupinfo_email_text.getText().toString();
        if (!email.isEmpty() && !Functions.isValidEmail(email)) {
            Functions.showToast(EditAccountActivity.this, R.string.incorrect_email);
            return false;
        }

        Log.e("test", edtMphone.getText().toString()+" "+KbossApplication.g_userinfo.f_mphone);
        if (!edtMphone.getText().toString().equals(KbossApplication.g_userinfo.f_mphone) && isAuth == false) {

            Functions.showToast(EditAccountActivity.this, R.string.auth_phone);
            return false;
        }
        return true;
    }

    void callApiEditAccount() {
        final String phone = edtMphone.getText().toString();
        final String name = nameEdit.getText().toString();
        final String birthday = birthday_db;
        final String email = detailsignupinfo_email_text.getText().toString();
        final int gender = detailsignupinfo_man.isChecked() ? 1 : 0;

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(EditAccountActivity.this, retVal.msg);

                } else {
                    // update user info
                    KbossApplication.g_userinfo.f_mphone = phone;
                    KbossApplication.g_userinfo.f_name = name;
                    KbossApplication.g_userinfo.f_birthday = birthday;
                    KbossApplication.g_userinfo.f_email = email;
                    KbossApplication.g_userinfo.f_gender = gender;

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

        ServiceManager.inst.editAccount(phone, name, email, birthday, gender, photoPath, handler);
    }
    public void onConfirmPressed(View view)
    {
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        btnBackClickListener.onClick(null);
    }
}

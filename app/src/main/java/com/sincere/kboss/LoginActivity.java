package com.sincere.kboss;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STUserInfo;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

import whdghks913.tistory.examplebroadcastreceiver.Broadcast;

/**
 * Created by Michael on 2016.10.25.
 */
public class LoginActivity extends ActivityTempl {
    public interface SmsReceiveCallback {
        void onReceive(String authcode);
    }

    EditText login_phonenumber;
    EditText login_verify_text;

    TextView login_title;
    TextView login_support_message;
    TextView login_support_number;

    Button login_verify_request;
    Button login_verifybutton;
    Button login_bigbutton;

    boolean isAuth = false;

    BroadcastReceiver myReceiver;

    SmsReceiveCallback smsReceiveCallback = new SmsReceiveCallback() {
        @Override
        public void onReceive(final String authcode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    login_verify_text.setText(authcode);
                }
            });
        }
    };

    private void checkSNSPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {

            }
            else
            {
                String[] permissions = {
                        Manifest.permission.RECEIVE_SMS
                };
                // 권한이 할당되지 않았으면 해당한 권한을 요청
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String token = FirebaseInstanceId.getInstance().getToken();
        KbossApplication.setToken(token);
        Log.e("test","Login Token:"+token);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
        pw.setTextColor(getResources().getColor(android.R.color.white));

        Typeface tfGodoB = Typeface.createFromAsset(getAssets(), "fonts/GodoB.ttf");
        Typeface tfGodoM = Typeface.createFromAsset(getAssets(), "fonts/GodoM.ttf");

        login_title = (TextView) findViewById(R.id.login_title);
        login_phonenumber = (EditText) findViewById(R.id.login_phonenumber);
        login_verify_text = (EditText) findViewById(R.id.login_verify_text);
        login_verify_request = (Button) findViewById(R.id.login_verify_request);
        login_verifybutton = (Button) findViewById(R.id.login_verifybutton);
        login_bigbutton = (Button) findViewById(R.id.login_bigbutton);
        login_support_message = (TextView) findViewById(R.id.login_support_message);
        login_support_number = (TextView) findViewById(R.id.login_support_number);

        login_title.setTypeface(tfGodoB);
        //login_phonenumber.setTypeface(tfGodoM);
        //login_verify_text.setTypeface(tfGodoM);
        //login_verify_request.setTypeface(tfGodoM);
        //login_verifybutton.setTypeface(tfGodoM);
        login_bigbutton.setTypeface(tfGodoM);
        //login_support_message.setTypeface(tfGodoM);
        //login_support_number.setTypeface(tfGodoM);

        checkSNSPermissions();

        myReceiver = new Broadcast(smsReceiveCallback);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

        registerReceiver(myReceiver, intentFilter);

        final LinearLayout llBottom = (LinearLayout)findViewById(R.id.llBottom);
        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();

                activityRootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = llBottom.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    //enter your code here
                    llBottom.setVisibility(View.INVISIBLE);
                }else{
                    //enter code for hid
                    llBottom.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // suppress the keyboard until the user actually touches the edittext view
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(myReceiver);
    }

    public void sendVerifyRequest(View v) {
        String phone = login_phonenumber.getText().toString();
        if (phone.isEmpty()) {
            Functions.showToast(LoginActivity.this, R.string.detailsignupinfo_elector_placeholder_en);
            return;
        }
        if (phone.length() != 11) {
            Functions.showToast(LoginActivity.this, R.string.mphone_must_11_chars);
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
                    Functions.showToast(LoginActivity.this, R.string.success_send_authreq);
                } else {
                    Functions.showToast(LoginActivity.this, retVal.msg);
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
        String phone = login_phonenumber.getText().toString();
        String authcode = login_verify_text.getText().toString();
        if (phone.isEmpty()) {
            Functions.showToast(LoginActivity.this, R.string.detailsignupinfo_elector_placeholder_en);
            return;
        }
        if (phone.length() != 11) {
            Functions.showToast(LoginActivity.this, R.string.mphone_must_11_chars);
            return;
        }
        if (authcode.isEmpty()) {
            Functions.showToast(LoginActivity.this, R.string.input_authcode);
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
                if (retVal.code == ServiceParams.ERR_NONE) {
                    Functions.showToast(LoginActivity.this, R.string.success_auth);
                    isAuth = true;
                    Functions.hideVirtualKeyboard(LoginActivity.this);
                    KbossApplication.g_userinfo = users.get(0);

                    Functions.saveUserInfo(getApplicationContext());

                } else if (retVal.code == ServiceParams.ERR_INVALID_USER) {
                    Functions.showToast(LoginActivity.this, R.string.success_auth);
                    Functions.hideVirtualKeyboard(LoginActivity.this);
                    isAuth = true;
                } else {
                    Functions.showToast(LoginActivity.this, retVal.msg);
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

    public void gotoMainActivity(View v) {
        if (isAuth == false) {
            Functions.showToast(LoginActivity.this, R.string.auth_phone);
            return;
        }

        if (KbossApplication.g_userinfo == null || KbossApplication.g_userinfo.f_id == 0) {
            KbossApplication.g_mphone = login_phonenumber.getText().toString();

            Intent i = new Intent(this, RegisterStartActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);

        } else {
            if (KbossApplication.g_userinfo.f_level == ServiceParams.LV_MANAGER) {
                Intent i = new Intent(LoginActivity.this, com.sincere.kboss.manager.MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();

            } else { // worker is default
                Intent i = new Intent(LoginActivity.this, com.sincere.kboss.worker.MainActivity.class);
                if (KbossApplication.g_userinfo.minimumRequirement() == false) {
                    i.putExtra(com.sincere.kboss.worker.MainActivity.EXTRA_GOTO_USERINFO, true);
                }
                if(getIntent().hasExtra("workend")) {
                    i.putExtra("workend", getIntent().getIntExtra("workend",0));
                }
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (Functions.gToast!=null && Functions.gToast.getView().isShown()) {
            finish();
            System.exit(0);
        }

        Functions.showToast(LoginActivity.this, R.string.back_again);
    }
}

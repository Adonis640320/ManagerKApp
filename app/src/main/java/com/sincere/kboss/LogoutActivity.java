package com.sincere.kboss;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.adapters.LogoutListAdapter;
import com.sincere.kboss.adapters.PayTypeListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Michael on 10/26/2016.
 */
public class LogoutActivity extends ActivityTempl {
    ListView lstReasons;
    LogoutListAdapter adapter;
    Button btnLogout;
    ImageView btnBack;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            returnBack(null);
        }
    };

    View.OnClickListener btnLogoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callApiLogout(adapter.reason);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        initUI();
    }

    void initUI() {
        lstReasons = (ListView) findViewById(R.id.lstReasons);
        adapter = new LogoutListAdapter(LogoutActivity.this, getApplicationContext());
        adapter.setData(KbossApplication.g_reasons);
        lstReasons.setAdapter(adapter);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(btnLogoutClickListener);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    void callApiLogout(final int reason) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(LogoutActivity.this, retVal.msg);

                } else {
                    KbossApplication.g_userinfo.f_id = 0;
                    KbossApplication.g_userinfo.f_authkey = "";

                    Functions.saveUserInfo(getApplicationContext());

                    Intent i  = new Intent(LogoutActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
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

        ServiceManager.inst.logout(String.valueOf(reason), handler);
    }

    @Override
    public void onBackPressed() {
        btnBackClickListener.onClick(null);
    }
}

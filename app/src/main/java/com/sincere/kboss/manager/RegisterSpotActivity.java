package com.sincere.kboss.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Michael on 11/7/2016.
 */
public class RegisterSpotActivity extends ActivityTempl {
    TextView btnConfirm;
    EditText edt01, edt02, edt03, edt04;

    String spotName;
    String address;
    String manager;
    String phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_spot);

        initUI();
    }

    void initUI() {
        btnConfirm = (TextView) findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    callApiAddSpotNotReady();
                }
            }
        });

        edt01 = (EditText) findViewById(R.id.edt01);
        edt02 = (EditText) findViewById(R.id.edt02);
        edt03 = (EditText) findViewById(R.id.edt03);
        edt04 = (EditText) findViewById(R.id.edt04);
    }

    boolean checkInput() {
        spotName = edt01.getText().toString();
        address = edt02.getText().toString();
        manager = edt03.getText().toString();
        phone = edt04.getText().toString();

        if (spotName.isEmpty()) {
            Functions.showToast(RegisterSpotActivity.this, R.string.input_spotname);
            return false;
        }

        if (address.isEmpty()) {
            Functions.showToast(RegisterSpotActivity.this, R.string.input_address);
            return false;
        }

        if (manager.isEmpty()) {
            Functions.showToast(RegisterSpotActivity.this, R.string.input_manager);
            return false;
        }

        if (phone.isEmpty()) {
            Functions.showToast(RegisterSpotActivity.this, R.string.detailsignupinfo_elector_placeholder_en);
            return false;
        }

        return true;
    }

    void callApiAddSpotNotReady() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(RegisterSpotActivity.this, retVal.msg);

                } else {

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

        ServiceManager.inst.addSpotNotReady(spotName, address, manager, phone, handler);
    }
}

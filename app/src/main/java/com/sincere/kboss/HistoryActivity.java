package com.sincere.kboss;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.JsonHttpResponseHandler;
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
public class HistoryActivity extends ActivityTempl {
    EditText edtHistoryYear;
    ImageView btnBack;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String historyyear = edtHistoryYear.getText().toString();
            if (historyyear.isEmpty()) {
                historyyear = "0"; // FIXME
            }

            callApiSetSkillsYear(Integer.parseInt(historyyear));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initUI();

        updateUI();
    }

    void initUI() {
        edtHistoryYear = (EditText) findViewById(R.id.edtHistoryYear);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    void updateUI() {
        int historyyear = KbossApplication.g_userinfo.f_skills_year;
        if (historyyear == 0) {
            edtHistoryYear.setText("");
        } else {
            edtHistoryYear.setText(String.valueOf(historyyear));
        }
    }

    void callApiSetSkillsYear(final int skillsyear) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(HistoryActivity.this, retVal.msg);

                } else {
                    KbossApplication.g_userinfo.f_skills_year = skillsyear;

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

        ServiceManager.inst.setSkillsYear(skillsyear, handler);
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

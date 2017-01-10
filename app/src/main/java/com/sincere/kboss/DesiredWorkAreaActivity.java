package com.sincere.kboss;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.adapters.AreaListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STUserInfo;
import com.sincere.kboss.stdata.STWorkingArea;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 10/26/2016.
 */
public class DesiredWorkAreaActivity extends ActivityTempl {
    ListView lstWorkingArea;
    AreaListAdapter adapter;
    ImageView btnBack;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String areas = adapter.getWorkingAreaString();
            if (areas.isEmpty()) {
                areas = "-1"; // FIXME
            }

            if (areas.equals("-1")) {
                Toast.makeText(DesiredWorkAreaActivity.this, "근무지역을 선택해주세요",Toast.LENGTH_SHORT).show();
                return;
            }

            callApiSetWorkingArea(areas);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desiredworkarea);

        initUI();
    }

    void initUI() {
        lstWorkingArea = (ListView) findViewById(R.id.lstWorkingArea);
        adapter = new AreaListAdapter(DesiredWorkAreaActivity.this, getApplicationContext());
        adapter.setData(KbossApplication.g_areas);
        lstWorkingArea.setAdapter(adapter);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    void callApiSetWorkingArea(final String areas) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(DesiredWorkAreaActivity.this, retVal.msg);

                } else {
                    KbossApplication.g_userinfo.f_prefer_zone = areas;

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

        ServiceManager.inst.setWorkingArea(areas, handler);
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

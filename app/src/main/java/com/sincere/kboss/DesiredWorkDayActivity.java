package com.sincere.kboss;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.adapters.WeekdayListAdapter;
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
public class DesiredWorkDayActivity extends ActivityTempl {
    ListView lstWeekdays;
    WeekdayListAdapter adapter;
    ImageView btnBack;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String weekdays = adapter.getWeekdayString();
            if (weekdays.isEmpty()) {
                weekdays = "-1"; // FIXME
            }

            if (weekdays.equals("-1")) {
                Toast.makeText(DesiredWorkDayActivity.this, "근무요일을 선택해주세요",Toast.LENGTH_SHORT).show();
                return;
            }

            callApiSetWeekday(weekdays);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desiredworkday);

        initUI();
    }

    void initUI() {
        lstWeekdays = (ListView) findViewById(R.id.lstWeekdays);
        adapter = new WeekdayListAdapter(DesiredWorkDayActivity.this, getApplicationContext());
        adapter.setData(KbossApplication.g_weekdays);
        lstWeekdays.setAdapter(adapter);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    void callApiSetWeekday(final String weekdays) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(DesiredWorkDayActivity.this, retVal.msg);

                } else {
                    KbossApplication.g_userinfo.f_prefer_time = weekdays;

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

        ServiceManager.inst.setWeekday(weekdays, handler);
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

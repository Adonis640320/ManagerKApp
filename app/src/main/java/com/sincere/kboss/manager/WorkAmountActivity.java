package com.sincere.kboss.manager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.WorkAmountListAdapter;
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
public class WorkAmountActivity extends ActivityTempl {
    ListView lstWorkAmounts;
    WorkAmountListAdapter adapter;
    ImageView btnBack;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConfirmWorkSubFragment.m_cur_workamount = adapter.workamount;

            if (ConfirmWorkSubFragment.m_cur_workamount_pos >= 0) {
                ConfirmWorkSubFragment.m_changed_workamount_positions.add(ConfirmWorkSubFragment.m_cur_workamount_pos);
                ConfirmWorkSubFragment.m_changed_workamounts.add(ConfirmWorkSubFragment.m_cur_workamount);
                ConfirmWorkSubFragment.m_cur_workamount_pos = -1;
            }
            int spot_id;
            if (MainActivity.g_curSpot < 0) {
                spot_id = 0;
            } else {
                spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
            }
            ConfirmWorkFragment.registeredFragments.get(ConfirmWorkFragment.curFrag).updateHistoryList(Functions.getDateTimeStringFromToday(ConfirmWorkFragment.curFrag-28), spot_id);
//            Log.e("test","Hahaha");
            returnBack(null);
        }
    };

    int workamount_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workamount);
        workamount_id = getIntent().getIntExtra("workamount_id",0);
        initUI();
    }

    void initUI() {
        lstWorkAmounts = (ListView) findViewById(R.id.lstWorkAmounts);
        adapter = new WorkAmountListAdapter(WorkAmountActivity.this, getApplicationContext());
        adapter.setData(KbossApplication.g_workamounts, workamount_id);
        lstWorkAmounts.setAdapter(adapter);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    @Override
    public void onBackPressed() {
        btnBackClickListener.onClick(null);
    }
}

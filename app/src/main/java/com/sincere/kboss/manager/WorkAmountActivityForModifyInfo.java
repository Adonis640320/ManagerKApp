package com.sincere.kboss.manager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.WorkAmountListAdapter;
import com.sincere.kboss.global.Functions;
import com.todddavies.components.progressbar.ProgressWheel;

/**
 * Created by Michael on 10/26/2016.
 */
public class WorkAmountActivityForModifyInfo extends ActivityTempl {
    ListView lstWorkAmounts;
    WorkAmountListAdapter adapter;
    ImageView btnBack;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ModifyWorkInfoFragment.m_workamount_id = adapter.workamount;

            ModifyWorkInfoFragment.modifyWorkInfoFragment.updateInfo();
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
        adapter = new WorkAmountListAdapter(WorkAmountActivityForModifyInfo.this, getApplicationContext());
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

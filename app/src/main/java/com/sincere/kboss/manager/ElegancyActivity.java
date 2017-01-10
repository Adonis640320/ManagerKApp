package com.sincere.kboss.manager;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.WorkAmountListAdapter;
import com.sincere.kboss.global.Functions;
import com.todddavies.components.progressbar.ProgressWheel;

/**
 * Created by Michael on 10/26/2016.
 */
public class ElegancyActivity extends ActivityTempl {
    ImageView btnBack;
    int elegancy = 0;
    RadioButton radio1,radio2,radio3;
    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(radio1.isChecked()) ModifyWorkInfoFragment.m_elegancy_id = 2;
            else if(radio2.isChecked()) ModifyWorkInfoFragment.m_elegancy_id = 1;
            else if(radio3.isChecked()) ModifyWorkInfoFragment.m_elegancy_id = 0;

            ModifyWorkInfoFragment.modifyWorkInfoFragment.updateInfo();
            returnBack(null);
        }
    };

    int elegancy_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegancy);
        elegancy_id = getIntent().getIntExtra("elegancy_id",0);
        initUI();
    }

    void initUI() {

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        radio1 = (RadioButton)findViewById(R.id.radio);
        radio2 = (RadioButton)findViewById(R.id.radio1);
        radio3 = (RadioButton)findViewById(R.id.radio2);

        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio2.setChecked(false);
                radio3.setChecked(false);
            }
        });
        radio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio1.setChecked(false);
                radio3.setChecked(false);
            }
        });
        radio3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio1.setChecked(false);
                radio2.setChecked(false);
            }
        });
        radio1.setChecked(false);
        radio2.setChecked(false);
        radio3.setChecked(false);
        if(elegancy_id == 0) radio3.setChecked(true);
        if(elegancy_id == 1) radio2.setChecked(true);
        if(elegancy_id == 2) radio1.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        btnBackClickListener.onClick(null);
    }
}

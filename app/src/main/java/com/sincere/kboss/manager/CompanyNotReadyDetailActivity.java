package com.sincere.kboss.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STCompany;
import com.sincere.kboss.stdata.STSpotNotReady;

/**
 * Created by Michael on 2016.11.07.
 */
public class CompanyNotReadyDetailActivity extends ActivityTempl {
    TextView lblName, lblRegtime, lblManager, lblManagerPhone, lblFeedbackDate, lblFeedback;
    RelativeLayout rlPostBack;

    public final static String EXTRA_COMPANY = "extra_company";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_notready_detail);

        initUI();

        updateUI();
    }

    void initUI() {
        lblName = (TextView) findViewById(R.id.lblName);
        lblRegtime = (TextView) findViewById(R.id.lblRegtime);
        lblManager = (TextView) findViewById(R.id.lblManager);
        lblManagerPhone = (TextView) findViewById(R.id.lblManagerPhone);
        lblFeedback = (TextView) findViewById(R.id.lblFeedback);
        lblFeedbackDate = (TextView) findViewById(R.id.lblFeedbackDate);

        rlPostBack = (RelativeLayout) findViewById(R.id.rlPostBack);
    }

    void updateUI() {
        STCompany spot = (STCompany)getIntent().getSerializableExtra(EXTRA_COMPANY);
        lblName.setText(spot.f_name);
        lblRegtime.setText(Functions.getDateTimeStringFmt(spot.f_regtime, "%d-%02d-%02d %02d:%02d:%02d"));
        lblManager.setText(spot.f_manager);
        lblManagerPhone.setText(spot.f_manager_phone);

        if (!spot.f_feedback.isEmpty()) {
            rlPostBack.setVisibility(View.VISIBLE);
            lblFeedback.setText(spot.f_feedback);
            lblFeedbackDate.setText(Functions.getDateStringFmt(spot.f_feedback_date, "%d-%02d-%02d"));
        } else {
            rlPostBack.setVisibility(View.INVISIBLE);
        }
    }
}

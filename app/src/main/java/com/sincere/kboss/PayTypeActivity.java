package com.sincere.kboss;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PreemptiveAuthorizationHttpRequestInterceptor;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.adapters.PayTypeListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJobWorker;
import com.sincere.kboss.worker.MainActivity;
import com.sincere.kboss.worker.WorkListFragment;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Michael on 10/26/2016.
 */
public class PayTypeActivity extends ActivityTempl {

    public interface ItemClickListener {
        void onClick(int paytype);
    }

    PayTypeActivity.ItemClickListener itemClickListener = new PayTypeActivity.ItemClickListener() {
        @Override
        public void onClick(final int paytype) {
            Log.e("itemclicklistner:",String.valueOf(paytype));
            OnClickListener clickListener = new OnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    switch (view.getId()) {
                        case R.id.btnYes:
                            callApiSetPayType(String.valueOf(paytype));
                            dialog.dismiss();
                            break;

                        case R.id.btnNo:
                            dialog.dismiss();
                            break;
                    }
                }
            } ;

            Holder holder = new ViewHolder(R.layout.dialog_yesno);
            SimpleAdapter adapter = new SimpleAdapter(PayTypeActivity.this, false);
            DialogPlus dialog = new DialogPlus.Builder(PayTypeActivity.this)
                    .setContentHolder(holder)
                    .setCancelable(true)
                    .setGravity(DialogPlus.Gravity.CENTER)
                    .setAdapter(adapter)
                    .setOnClickListener(clickListener)
                    .create();

            dialog.show();

            Button btnYes = (Button)dialog.findViewById(R.id.btnYes);
            btnYes.setBackgroundColor( getResources().getColor(R.color.clr_red_dark));
            TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
            //lblMessage.setText(String.format(getString(R.string.changepaytype_notpaiddays), notPaidDays,KbossApplication.g_paytypes.get(Integer.valueOf(payTypeString)-1).f_name));
            lblMessage.setText(String.format("%d일 일한 일당이 %s \n수수료율로 계산하여 지급됩니다.\n\n정말 변경하시겠습니까?", notPaidDays,KbossApplication.g_paytypes.get(paytype-1).f_name));

        }
    };

    ListView lstPayTypes;
    PayTypeListAdapter adapter;
    ImageView btnBack;

    int notPaidDays = 0;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String payTypeString = adapter.getPayTypeString();
            if (payTypeString.isEmpty()) {
                payTypeString = "1"; // FIXME
            }

            final String payType = payTypeString;

            if(notPaidDays == 0) {
                callApiSetPayType(payType);
                return;
            }
            callApiSetPayType(payType);
/*
            if(KbossApplication.g_userinfo.f_pay_type == Integer.valueOf(payTypeString)) {
                returnBack(null);
                return;
            }
            */
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytype);

        initUI();
        callApiGetNotPaidWorkHistoryCount();
    }

    void initUI() {
        lstPayTypes = (ListView) findViewById(R.id.lstPayTypes);
        adapter = new PayTypeListAdapter(PayTypeActivity.this, getApplicationContext(),itemClickListener);
        adapter.setData(KbossApplication.g_paytypes);
        lstPayTypes.setAdapter(adapter);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    void callApiGetNotPaidWorkHistoryCount() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseGetWorkHistoryCount(response);
                if (retVal.code != ServiceParams.ERR_NONE) {

                    notPaidDays = 0;

                } else {
                    notPaidDays = retVal.intData;
                }
            }
        };

        ServiceManager.inst.getNotPaidWorkHistoryCount(handler);
    }

    void callApiSetPayType(final String paytype) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(PayTypeActivity.this, retVal.msg);

                } else {
                    KbossApplication.g_userinfo.f_pay_type = Integer.parseInt(paytype);

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

        ServiceManager.inst.setPayType(paytype, handler);
    }


    public void onConfirmPressed(View view)
    {
        btnBackClickListener.onClick(null);
    }

    @Override
    public void onBackPressed() {
        btnBackClickListener.onClick(null);
    }
}

package com.sincere.kboss;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.adapters.MessageListAdapter;
import com.sincere.kboss.adapters.PointListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STBank;
import com.sincere.kboss.stdata.STBankType;
import com.sincere.kboss.stdata.STCert;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 10/26/2016.
 */
public class BankAccountActivity extends ActivityTempl {
    ImageView imgBank;
    LinearLayout llBank;
    EditText edtBankAccount;
    EditText edtOwner;
    Button btnRegister;

    ImageView btnBack;
    RelativeLayout rlTop;
    TextView lblBank;

//    ListView lstHistory;
//    MessageListAdapter adapter;
//    ArrayList<String> messages = new ArrayList<>();

    LinearLayout llBottom;
    LinearLayout llbankRegHistory;
    ArrayList<STBank> bankRegHistory = new ArrayList<STBank>();

//    LinearLayout llRegBank;
//    TextView tvRegBankDate;
//    LinearLayout llBankAgree;
//    TextView tvBankAgreeDate;

    int bankid = -1;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnRegister.performClick();
            //returnBack(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankaccount);

        initUI();
        callApiGetBankRegHistory();
        updateUI();
    }

    void initUI() {
        imgBank = (ImageView) findViewById(R.id.imgBank);
        llBank = (LinearLayout) findViewById(R.id.llBank);
        llBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] bankitems = new String[KbossApplication.g_banktypes.size()];
                for (int i=0; i<KbossApplication.g_banktypes.size(); i++) {
                    bankitems[i] = KbossApplication.g_banktypes.get(i).f_name;
                }

                // TODO
                new AlertDialog.Builder(BankAccountActivity.this)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle(R.string.select_account)
                        .setItems(bankitems, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                bankid = KbossApplication.g_banktypes.get(which).f_id;
                                lblBank.setText(KbossApplication.g_banktypes.get(which).f_name);
                            }
                        }).create().show();
            }
        });

        lblBank = (TextView) findViewById(R.id.lblBank);

        edtBankAccount = (EditText) findViewById(R.id.edtBankAccount);
        edtOwner = (EditText) findViewById(R.id.edtOwner);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = edtBankAccount.getText().toString();
                String owner = edtOwner.getText().toString();
                if (!account.isEmpty()) {
                    if (bankid < 0) bankid = KbossApplication.g_banktypes.get(0).f_id;
                }
                Log.e("test", KbossApplication.g_userinfo.f_bank_type+" "+bankid);
                if(KbossApplication.g_userinfo.f_bank_type == bankid && KbossApplication.g_userinfo.f_bank_acct .equals(account) &&  KbossApplication.g_userinfo.f_bank_owner.equals(owner)) {
                    returnBack(null);
                    return;
                }
                if (owner.isEmpty()) {
                    owner = KbossApplication.g_userinfo.f_name;
                }

                if (!account.isEmpty()) {
                    if(bankid < 0) bankid = KbossApplication.g_banktypes.get(0).f_id;
                    callApiSetBank(bankid, account, owner);
                } else {
                    Functions.showToast(BankAccountActivity.this, R.string.input_bank_account);
                }
            }
        });

//        lstHistory = (ListView) findViewById(R.id.lstHistory);
//        adapter = new MessageListAdapter(getApplicationContext());
//        adapter.setData(messages);
//        lstHistory.setAdapter(adapter);

        llBottom  = (LinearLayout)findViewById(R.id.llBottom);
        llbankRegHistory = (LinearLayout)findViewById(R.id.llbankRegHistory);
//        llRegBank = (LinearLayout)findViewById(R.id.llRegBank);;
//        tvRegBankDate = (TextView)findViewById(R.id.tvRegBankDate);
//        llBankAgree = (LinearLayout)findViewById(R.id.llBankAgree);
//        tvBankAgreeDate = (TextView)findViewById(R.id.tvBankAgreeDate);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        rlTop = (RelativeLayout) findViewById(R.id.rlTop);
        rlTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideVirtualKeyboard(BankAccountActivity.this);
            }
        });

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    void updateUI() {
        int banktype = KbossApplication.g_userinfo.f_bank_type;
        String bankacct = KbossApplication.g_userinfo.f_bank_acct;
        if (bankacct.isEmpty()) {
            lblBank.setText(KbossApplication.g_banktypes.get(0).f_name);
        } else {
            lblBank.setText(Functions.getBankName(banktype));
        }

        edtBankAccount.setText(bankacct);

        String owner = KbossApplication.g_userinfo.f_bank_owner;
        if (owner.isEmpty()) {
            edtOwner.setText(KbossApplication.g_userinfo.f_name);
        } else {
            edtOwner.setText(owner);
        }

//        if(KbossApplication.g_userinfo.f_bank_regdate!=null && KbossApplication.g_userinfo.f_bank_regdate!="" && !KbossApplication.g_userinfo.f_bank_regdate.equals("0000-00-00 00:00:00")) {
//            llRegBank.setVisibility(View.VISIBLE);
//            tvRegBankDate.setText("계좌번호를 등록했습니다.("+Functions.changeDateString(KbossApplication.g_userinfo.f_bank_regdate)+")");
//        } else {
//            llBottom.setVisibility(View.GONE);
//        }
//
//        llBankAgree.setVisibility(View.GONE);
//        //tvBankAgreeDate
    }

    void callApiSetBank(final int bankid, final String account, final String owner) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(BankAccountActivity.this, retVal.msg);
                } else {
                    KbossApplication.g_userinfo.f_bank_type = bankid;
                    KbossApplication.g_userinfo.f_bank_acct = account;
                    KbossApplication.g_userinfo.f_bank_owner = owner;
                    try {
                        KbossApplication.g_userinfo.f_bank_regdate = response.getString(ServiceParams.SVCC_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(BankAccountActivity.this,"BankRegDate:"+KbossApplication.g_userinfo.f_bank_regdate,Toast.LENGTH_SHORT).show();
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

        ServiceManager.inst.setBank(owner, String.valueOf(bankid), account, handler);
    }

    void callApiGetBankRegHistory() {

        bankRegHistory.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseGetBanks(response, bankRegHistory);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    llbankRegHistory.removeAllViews();
                    for(int i=0;i<bankRegHistory.size();i++) {
                        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.item_info_message,null);
                        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
                        if(bankRegHistory.get(i).f_bank_history_type == 0) {
                            tvMessage.setText("계좌정보를 등록했습니다.("+ Functions.changeDateString(bankRegHistory.get(i).f_bank_regdate)+")");
                        } else {
                            tvMessage.setText("관리자가 계좌정보를 승인했습니다.("+ Functions.changeDateString(bankRegHistory.get(i).f_bank_regdate)+")");
                        }
                        llbankRegHistory.addView(view);
                    }
                }
                if(bankRegHistory.size() == 0) llBottom.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                if(bankRegHistory.size() == 0) llBottom.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                if(bankRegHistory.size() == 0) llBottom.setVisibility(View.GONE);
            }
        };

        showProgress();

        ServiceManager.inst.getBanks(handler);
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

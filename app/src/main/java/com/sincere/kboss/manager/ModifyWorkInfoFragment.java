package com.sincere.kboss.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STSelectWorker;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by John on 12/18/2016.
 */

public class ModifyWorkInfoFragment extends FragmentTempl {

    TextView tvElagancy,tvWorkAmount;

    public final static String ARG_WORKER = "worker";
    STSelectWorker worker;

    public static int m_workamount_id = 0;
    public static int m_elegancy_id = 0;
    public static ModifyWorkInfoFragment modifyWorkInfoFragment = null;

    boolean callAPI = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_modify_workinfo, container, false);
        modifyWorkInfoFragment = this;
        initUI(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    void initUI(View v) {
        TextView btnConfirm = (TextView)v.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApiModifyWorkerInfo();
            }
        });

        worker = getArguments().getParcelable(ARG_WORKER);
        ImageView btnBack = (ImageView) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.sincere.kboss.manager.MainActivity mainActivity = (com.sincere.kboss.manager.MainActivity) getActivity();
                mainActivity.gotoAddFavoriteFragment(worker);
            }
        });

        TextView tvWorkDate = (TextView) v.findViewById(R.id.tvWorkDate);
        tvWorkDate.setText(Functions.changeDateString(worker.f_workdate));

        TextView tvSkill = (TextView) v.findViewById(R.id.tvSkill);
        tvSkill.setText(Functions.getJobsString(String.valueOf(worker.f_skill), getActivity()));

        TextView tvPayment = (TextView) v.findViewById(R.id.tvPayment);
        tvPayment.setText(Functions.getLocaleNumberString(worker.f_payment, ""));

        m_elegancy_id = worker.f_elegancy_id;
        tvElagancy = (TextView) v.findViewById(R.id.tvElagancy);
        switch (worker.f_elegancy_id)
        {
            case 0: tvElagancy.setText("하"); break;
            case 1: tvElagancy.setText("중"); break;
            case 2: tvElagancy.setText("상"); break;
        }

        m_workamount_id = worker.f_workamount_id;
        tvWorkAmount = (TextView)v.findViewById(R.id.tvWorkAmount);
        tvWorkAmount.setText(String.valueOf(KbossApplication.g_workamounts.get(worker.f_workamount_id).f_workamount));

        tvElagancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoElegancyActivity(null);
            }
        });

        tvWorkAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoWorkAmountActivity(null);
            }
        });

        Button btnSave = (Button)v.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.btnYes:
                                callApiModifyWorkerInfo();
                                dialog.dismiss();
                                break;

                            case R.id.btnNo:
                                dialog.dismiss();
                                break;
                        }
                    }
                } ;

                Holder holder = new ViewHolder(R.layout.dialog_yesno);
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), false);
                DialogPlus dialog = new DialogPlus.Builder(getActivity())
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(DialogPlus.Gravity.CENTER)
                        .setAdapter(adapter)
                        .setOnClickListener(clickListener)
                        .create();

                dialog.show();

                Button btnYes = (Button)dialog.findViewById(R.id.btnYes);
                TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
                lblMessage.setText("작업정보 변경사항을\n저장하시겠습니까?");
            }
        });

    }

    public void updateInfo()
    {
        //worker.f_elegancy_id = m_elegancy_id;
        switch (m_elegancy_id)
        {
            case 0: tvElagancy.setText("하"); break;
            case 1: tvElagancy.setText("중"); break;
            case 2: tvElagancy.setText("상"); break;
        }

        //worker.f_workamount_id = m_workamount_id;
        tvWorkAmount.setText(String.valueOf(KbossApplication.g_workamounts.get(m_workamount_id).f_workamount));
    }

    void callApiModifyWorkerInfo() {
        if(callAPI) return;
        callAPI = true;
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                callAPI = false;
                hideProgress();
                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    try {
                        worker.f_signout_time =response.getString(ServiceParams.SVCC_DATA);
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    worker.f_elegancy_id = m_elegancy_id;
                    worker.f_workamount_id = m_workamount_id;
                    com.sincere.kboss.manager.MainActivity mainActivity = (com.sincere.kboss.manager.MainActivity) getActivity();
                    mainActivity.gotoAddFavoriteFragment(worker);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callAPI = false;
                hideProgress();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callAPI = false;
                hideProgress();
            }
        };

        showProgress();

        ServiceManager.inst.modifyWorkInfo(worker.f_job_id, worker.f_worker_id, m_elegancy_id,m_workamount_id, handler);
    }


    public void gotoWorkAmountActivity(View v) {
        Intent i = new Intent(getActivity(), WorkAmountActivityForModifyInfo.class);
        i.putExtra("workamount_id",m_workamount_id);
        getActivity().startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoElegancyActivity(View v) {
        Intent i = new Intent(getActivity(), ElegancyActivity.class);
        i.putExtra("elegancy_id",m_elegancy_id);
        getActivity().startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}

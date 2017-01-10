package com.sincere.kboss.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.SelectWorkerListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STSelectWorker;
import com.sincere.kboss.stdata.STSpotNotReady;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Michael on 2016.11.07.
 */
public class SelectWorkerActivity extends ActivityTempl {
    TextView btnConfirm;
    Button btnRefresh;
    PullToRefreshListView lstItems;

    SelectWorkerListAdapter adapter;
    ArrayList<STSelectWorker> workers = new ArrayList<>();
    int pagecount = 0;

    public final static String EXTRA_JOB_ID = "job_id";
    public final static String EXTRA_WORKER_COUNT = "worker_count";
    int job_id = 0;
    int worker_count = 0;

    public interface NotifySelectChanged {
        void onSelectChanged();
    }

    NotifySelectChanged notifySelectChanged = new NotifySelectChanged() {
        @Override
        public void onSelectChanged() {
            int real_count = 0;
            int cur_count = 0;
            for (int i=0; i<adapter.m_chkStat.size(); i++) {
                if (adapter.m_chkStat.get(i) == true && workers.get(i).f_signin_cancel==0) {
                    real_count ++;
                }
                if(adapter.m_chkStat.get(i) == true  && workers.get(i).f_support_check==1 && workers.get(i).f_signin_cancel==0) {
                    cur_count++;
                }
            }

            if(real_count < worker_count) {
//                btnConfirm.setVisibility(View.INVISIBLE);
                btnConfirm.setVisibility(real_count >= 1 ? View.VISIBLE:View.INVISIBLE);
            } else {
                if(cur_count != real_count)
                    btnConfirm.setVisibility(real_count >= worker_count ? View.VISIBLE:View.INVISIBLE);

            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_worker);

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        callApiGetWorkerCandidates();
    }

    void initUI() {
        btnConfirm = (TextView) findViewById(R.id.btnConfirm);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        lstItems = (PullToRefreshListView) findViewById(R.id.lstItems);
        adapter = new SelectWorkerListAdapter(getApplicationContext(), notifySelectChanged);
        adapter.setData(workers);
        lstItems.setAdapter(adapter);

        btnRefresh.setVisibility(View.GONE);

        job_id = getIntent().getIntExtra(EXTRA_JOB_ID, 0);
        worker_count = getIntent().getIntExtra(EXTRA_WORKER_COUNT, 0);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workerids = "";
                int cur_count = 0;
                int real_count = 0;
                for (int i=0; i<adapter.m_chkStat.size(); i++) {
                    if (adapter.m_chkStat.get(i) == true && workers.get(i).f_support_check == 0 && workers.get(i).f_signin_cancel==0) {
                        if (workerids.isEmpty()) {
                            workerids = String.valueOf(workers.get(i).f_worker_id);
                        } else {
                            workerids += "," + String.valueOf(workers.get(i).f_worker_id);
                        }
                        cur_count ++;
                    }

                    if (adapter.m_chkStat.get(i) == true) {
                        real_count ++;
                    }
                }

                if (workerids.isEmpty()) {
                    if (cur_count == real_count) {
                        Functions.showToast(SelectWorkerActivity.this, R.string.select_workers);
                    } else {
                        Functions.showToast(SelectWorkerActivity.this, R.string.select_additional_workers);
                    }
                    return;
                }

                Log.e("test","Cur count:"+cur_count+" real_count:"+real_count+" worker_count:"+worker_count + " workers:"+workerids +" jobid:"+job_id);
                if (cur_count > worker_count) {
                    Functions.showToast(SelectWorkerActivity.this, String.format(getString(R.string.cannot_select_worker), worker_count));
                    return;
                }

                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.btnYes:
                                String _workerids = (String) dialog.findViewById(R.id.lblMessage).getTag();
                                callApiSelectWorkers(_workerids);
                                dialog.dismiss();
                                break;

                            case R.id.btnNo:
                                dialog.dismiss();
                                break;
                        }
                    }
                } ;

                Holder holder = new ViewHolder(R.layout.dialog_yesno);
                SimpleAdapter adapter = new SimpleAdapter(SelectWorkerActivity.this, false);
                DialogPlus dialog = new DialogPlus.Builder(SelectWorkerActivity.this)
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(DialogPlus.Gravity.CENTER)
                        .setAdapter(adapter)
                        .setOnClickListener(clickListener)
                        .create();

                dialog.show();
                TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
                lblMessage.setTag(workerids);
                if (real_count == cur_count) {
                    lblMessage.setText(String.format(getString(R.string.confirm_select_worker), cur_count));
                } else {
                    lblMessage.setText(String.format(getString(R.string.confirm_select_additional_worker), cur_count));
                }
            }
        });
    }

    void callApiGetWorkerCandidates() {
        pagecount = 0;
        workers.clear();
        adapter.m_chkStat.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                lstItems.onRefreshComplete();

                Log.e("test",response.toString());
                ArrayList<STSelectWorker> newWorkers = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetWorkerCandidates(response, newWorkers);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    pagecount ++;

                    for (int i=0; i<newWorkers.size(); i++) {
                        adapter.m_chkStat.add(newWorkers.get(i).f_support_check == 1);
                    }

                    // adapter.setData(jobs);
                    workers.addAll(newWorkers);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                lstItems.onRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                lstItems.onRefreshComplete();
            }
        };

        showProgress();

        ServiceManager.inst.getWorkerCandidates(job_id, pagecount, ServiceParams.PAGE_SIZE, handler);
    }

    void callApiSelectWorkers(String workerids) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(SelectWorkerActivity.this, retVal.msg);
                } else {
                    try {
                        setResult(0);
                    }catch(Exception e){}
                    returnBack(null);
                    int spot_id;
                    if (MainActivity.g_curSpot < 0) {
                        spot_id = 0;
                    } else {
                        spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
                    }
                    ManageSpotFragment.registeredFragments.get(ManageSpotFragment.curFrag).updateJobList(Functions.getDateTimeStringFromToday(ManageSpotFragment.curFrag), spot_id);
                    MainActivity mainActivity = (MainActivity) ManageSpotFragment.registeredFragments.get(ManageSpotFragment.curFrag).getActivity();
                    mainActivity.updateConfirmWorkFragmentTitle();
                    mainActivity.updateConfirmWorkSubFragments();
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

        ServiceManager.inst.selectWorkers(job_id, workerids, handler);
    }
}

package com.sincere.kboss.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.ReselectWorkerListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJobManager;
import com.sincere.kboss.stdata.STSelectWorker;
import com.sincere.kboss.stdata.STSpotNotReady;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.11.07.
 */
public class ReselectWorkerActivity extends ActivityTempl {
    Button btnRefresh;
    TextView lblSelectStat;
    PullToRefreshListView lstItems;

    ReselectWorkerListAdapter adapter;
    ArrayList<STSelectWorker> workers = new ArrayList<>();
    int pagecount = 0;

    public final static String EXTRA_JOB_ID = "job_id";
    public final static String EXTRA_WORKER_COUNT = "worker_count";
    int job_id = 0;
    int worker_count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reselect_worker);

        initUI();
        callApiGetWorkerCandidates();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void initUI() {
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        lstItems = (PullToRefreshListView) findViewById(R.id.lstItems);
        adapter = new ReselectWorkerListAdapter(getApplicationContext());
        adapter.setData(workers);
        lstItems.setAdapter(adapter);

        lblSelectStat = (TextView) findViewById(R.id.lblSelectStat);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSelectWorkerActivity();
            }
        });

        job_id = getIntent().getIntExtra(EXTRA_JOB_ID, 0);
        worker_count = getIntent().getIntExtra(EXTRA_WORKER_COUNT, 0);
    }

    void updateHint() {
        int cur_count = 0;
        for (int i=0; i<workers.size(); i++) {
            if (workers.get(i).f_support_check == 1 && workers.get(i).f_signin_cancel == 0) {
                cur_count ++;
            }
        }
        lblSelectStat.setText(String.format(getString(R.string.selected_worker), cur_count, worker_count));
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

                    updateHint();
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
                    Functions.showToast(ReselectWorkerActivity.this, retVal.msg);
                } else {
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

        ServiceManager.inst.selectWorkers(job_id, workerids, handler);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            callApiGetWorkerCandidates();
        }
    }

    void gotoSelectWorkerActivity() {
        Intent i = new Intent(ReselectWorkerActivity.this, SelectWorkerActivity.class);
        i.putExtra(SelectWorkerActivity.EXTRA_JOB_ID, job_id);
        i.putExtra(SelectWorkerActivity.EXTRA_WORKER_COUNT, worker_count);
        startActivityForResult(i,0);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}

package com.sincere.kboss.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.DailyJobListAdapter;
import com.sincere.kboss.adapters.ReselectWorkerListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJob;
import com.sincere.kboss.stdata.STJobManager;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/2/2016.
 */
@SuppressLint("ValidFragment")
public class DailyJobsFragment extends FragmentTempl {
    public static final String ARG_DATE = "jobdate";
    public static final String ARG_SPOT_ID = "spot_id";

    public boolean gettingJob = false;

    public interface ItemClickListener {
        void onClick(STJobManager job);
    }

    ItemClickListener itemClickListener = new ItemClickListener() {
        @Override
        public void onClick(STJobManager job) {
            if (job.f_support_count == 0) {
                gotoEditJobActivity(job);
            } else if (job.f_support_check_count == 0) {
                gotoSelectWorkerActivity(job);
            } else {
                gotoReselectWorkerActivity(job);
            }
        }
    };

    TextView lblNoJobs;
    PullToRefreshListView lstItems;
    DailyJobListAdapter adapter;

    ArrayList<STJobManager> jobs = new ArrayList<>();
    int pagecount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_daily_jobs, container, false);

        lblNoJobs = (TextView) v.findViewById(R.id.lblNoJobs);

        lstItems = (PullToRefreshListView) v.findViewById(R.id.lstItems);
        lstItems.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        lstItems.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                callApiGetJobs();
            }
        });

        adapter = new DailyJobListAdapter(getActivity().getApplicationContext(), itemClickListener);
        adapter.setData(jobs);
        lstItems.setAdapter(adapter);

        updateJobList(getArguments().getString(ARG_DATE), getArguments().getInt(ARG_SPOT_ID));
        Log.e("test","DailyJobsFragment");
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateJobList(String jobdate1, int spot_id1) {
        Bundle args = getArguments();
        args.putString(ARG_DATE, jobdate1);
        args.putInt(ARG_SPOT_ID, spot_id1);

        if (lstItems == null) {
            return;
        }

        callApiGetJobs();
    }

    void callApiGetJobs() {
        if (getArguments().getInt(ARG_SPOT_ID) == 0) {
            Log.e("test","DailyJobsFragment SPOT ID 0");
            lblNoJobs.setVisibility(View.VISIBLE);
            lstItems.setVisibility(View.INVISIBLE);
            return;
        }

        if (gettingJob == true) {
            return;
        }

        gettingJob = true;
        pagecount = 0;
        jobs.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                gettingJob = false;
                hideProgress();
                lstItems.onRefreshComplete();

                ArrayList<STJobManager> newjobs = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetJobs(response, newjobs);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    if (jobs.isEmpty()) {
                        lblNoJobs.setVisibility(View.VISIBLE);
                        lstItems.setVisibility(View.INVISIBLE);
                    }
                        // Functions.showToast(getActivity(), retVal.msg);  ///
                } else {
                    lblNoJobs.setVisibility(View.INVISIBLE);
                    lstItems.setVisibility(View.VISIBLE);

                    pagecount ++;

                    // adapter.setData(jobs);
                    jobs.addAll(newjobs);
                    adapter.notifyDataSetChanged();
                }

                if (getArguments().getInt(ARG_SPOT_ID) == 0) {
                    Log.e("test","DailyJobsFragment SPOT ID 0");
                    lblNoJobs.setVisibility(View.VISIBLE);
                    lstItems.setVisibility(View.INVISIBLE);
                    return;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                gettingJob = false;
                hideProgress();
                lstItems.onRefreshComplete();

                lblNoJobs.setVisibility(View.VISIBLE);
                lstItems.setVisibility(View.INVISIBLE);
                Log.e("test","failure1");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                gettingJob = false;
                hideProgress();
                lstItems.onRefreshComplete();

                lblNoJobs.setVisibility(View.VISIBLE);
                lstItems.setVisibility(View.INVISIBLE);
                Log.e("test","failure2");
            }
        };

        showProgress();

        Log.e("test", "DailyJobs callApiGetJobs "+getArguments().getString(ARG_DATE)+ " "+getArguments().getInt(ARG_SPOT_ID));
        ServiceManager.inst.getJobs(getArguments().getString(ARG_DATE), getArguments().getInt(ARG_SPOT_ID), pagecount, ServiceParams.PAGE_SIZE, handler);
    }

    void gotoEditJobActivity(STJobManager job) {
        Intent i = new Intent(getActivity(), EditJobActivity.class);
        i.putExtra(EditJobActivity.EXTRA_JOB, job);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    void gotoSelectWorkerActivity(STJobManager job) {
        Intent i = new Intent(getActivity(), SelectWorkerActivity.class);
        i.putExtra(SelectWorkerActivity.EXTRA_JOB_ID, job.job.f_id);
        i.putExtra(SelectWorkerActivity.EXTRA_WORKER_COUNT, job.job.f_worker_count);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    void gotoReselectWorkerActivity(STJobManager job) {
        Intent i = new Intent(getActivity(), ReselectWorkerActivity.class);
        i.putExtra(ReselectWorkerActivity.EXTRA_JOB_ID, job.job.f_id);
        i.putExtra(ReselectWorkerActivity.EXTRA_WORKER_COUNT, job.job.f_worker_count);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}

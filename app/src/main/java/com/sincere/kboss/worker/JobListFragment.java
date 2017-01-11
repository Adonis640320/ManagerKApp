package com.sincere.kboss.worker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.JobListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJob;
import com.sincere.kboss.stdata.STJobWorker;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Michael on 11/2/2016.
 */
public class JobListFragment extends FragmentTempl {
    public interface ItemClickListener {
        void onClick(STJobWorker job);
        void onCancelSupport(STJobWorker job);
        void onCancelSignin(STJobWorker job);
        void onConfirmSignin(STJobWorker job);
    }

    ItemClickListener itemClickListener = new ItemClickListener() {
        @Override
        public void onClick(STJobWorker job) {
            if (KbossApplication.g_userinfo.minimumRequirement() == false) {
                Functions.showToast(getActivity(), R.string.update_userinfo);
                return;
            }

            MainActivity activity = (MainActivity) getActivity();
            activity.gotoJobDetailFragment(JobListFragment.this,job);
        }

        @Override
        public void onCancelSupport(final STJobWorker job) {
            OnClickListener clickListener = new OnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    switch (view.getId()) {
                        case R.id.btnYes:
                            callApiCancelSupport(job.job.f_id);
                            dialog.dismiss();
                            callApiGetJobs(); // added by Adonis
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
            btnYes.setBackgroundColor( getResources().getColor(R.color.clr_red_dark));
            TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
            lblMessage.setText(R.string.cancel_support);
        }

        @Override
        public void onCancelSignin(final STJobWorker job) {
            OnClickListener clickListener = new OnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    switch (view.getId()) {
                        case R.id.btnYes:
                            callApiCancelSignin(job.job.f_id);
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
            btnYes.setBackgroundColor( getResources().getColor(R.color.clr_red_dark));
            TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
            lblMessage.setText(R.string.cancel_signin);
        }

        @Override
        public void onConfirmSignin(STJobWorker job) {
            callApiConfirmSignin(job.job.f_id);
        }
    };

    PullToRefreshListView lstItems;
    JobListAdapter adapter;

    ArrayList<STJobWorker> workerjobs = new ArrayList<>();
    int pagecount = 0;

    boolean isRefresh = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_joblist, container, false);

        lstItems = (PullToRefreshListView) v.findViewById(R.id.lstItems);
        lstItems.setMode(PullToRefreshBase.Mode.PULL_FROM_START); // modified by Adonis
        lstItems.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                isRefresh = true;
                callApiGetJobs();
            }
        });

        adapter = new JobListAdapter(getActivity().getApplicationContext(), itemClickListener);
        adapter.setData(workerjobs);
        lstItems.setAdapter(adapter);

        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("test","onActivityResult");
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                int value = data.getIntExtra("state",0);
                Log.e("test","onActivityResult resume");
                callApiGetJobs();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        callApiGetJobs();
    }

    public void callApiGetJobs() {
        pagecount = 0;
        workerjobs.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                lstItems.onRefreshComplete();
                Log.e("test","GetJobs:"+response.toString());

                ArrayList<STJobWorker> newjobs = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetJobsWorker(response, newjobs);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    int support_count = 0;
                    for (int i=0; i<newjobs.size(); i++) {
                        STJobWorker anItem = newjobs.get(i);
                        if (anItem.f_support_check==1 && anItem.f_support_cancel==0 && anItem.f_signin_time.isEmpty()) {
                            support_count ++;
                        }
                    }

                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.showTab1Badge(support_count>0);

                    pagecount ++;

                    // adapter.setData(jobs);
                    workerjobs.addAll(newjobs);
                    adapter.notifyDataSetChanged();;
                    // modified by Adonis
                    scrollMyListViewToTop();
                    //scrollMyListViewToBottom();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                lstItems.onRefreshComplete();
                // modified by Adonis
                scrollMyListViewToTop();
                //scrollMyListViewToBottom();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                lstItems.onRefreshComplete();
                // modified by Adonis
                scrollMyListViewToTop();
                //scrollMyListViewToBottom();
            }
        };

        showProgress();

        ServiceManager.inst.getJobsWorker(pagecount, ServiceParams.PAGE_SIZE, handler);
    }

    // modified by Adonis
    private void scrollMyListViewToTop() {
        if(!isRefresh) return;
        if(adapter.getCount() == 0) return;
        lstItems.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                //lstItems.getRefreshableView().smoothScrollToPosition(adapter.getCount());
                //lstItems.getRefreshableView().setSelection(adapter.getCount()-1);
                lstItems.getRefreshableView().setSelection(0);
                isRefresh = false;
            }
        });
    }

    void callApiCancelSupport(final int jobid) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    Functions.showToast(getActivity().getApplicationContext(), R.string.successfully_cancelled);

                    for (int i=0; i<workerjobs.size(); i++) {
                        STJobWorker anItem = workerjobs.get(i);
                        if (anItem.job.f_id == jobid) {
                            anItem.f_support_cancel = 1;
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        };

        ServiceManager.inst.cancelSupport(jobid, handler);
    }

    void callApiCancelSignin(final int jobid) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    Functions.showToast(getActivity().getApplicationContext(), R.string.successfully_cancelled);

                    for (int i=0; i<workerjobs.size(); i++) {
                        STJobWorker anItem = workerjobs.get(i);
                        if (anItem.job.f_id == jobid) {
                            anItem.f_signin_cancel = 1;
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        };

        ServiceManager.inst.cancelSignIn(jobid, handler);
    }

    void callApiConfirmSignin(final int jobid) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                Log.e("test","JobHistory:"+response.toString());
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    Functions.showToast(getActivity().getApplicationContext(), R.string.signin_check_requested);

                    for (int i=0; i<workerjobs.size(); i++) {
                        STJobWorker anItem = workerjobs.get(i);
                        if (anItem.job.f_id == jobid) {
                            Date today = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            anItem.f_support_time = sdf.format(today);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        };

        ServiceManager.inst.confirmSignIn(jobid, handler);
    }

}

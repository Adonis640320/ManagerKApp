package com.sincere.kboss.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.ConfirmWorkSubListAdapter;
import com.sincere.kboss.adapters.DailyJobListAdapter;
import com.sincere.kboss.adapters.ReselectWorkerListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJob;
import com.sincere.kboss.stdata.STJobManager;
import com.sincere.kboss.stdata.STSelectWorker;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/2/2016.
 */
public class ConfirmWorkSubFragment extends FragmentTempl {
    public static final String ARG_DATE = "jobdate";
    public static final String ARG_SPOT_ID = "spot_id";

    public boolean gettingHistories = false;

    TextView lblNoHistories;
    RelativeLayout rlContents_sub01;
    LinearLayout llSteps;
    PullToRefreshListView lstItems;
    public ConfirmWorkSubListAdapter adapter;
    public LinearLayout llActions;
    public Button btnRefresh;
    public Button btnConfirmSignin;

    TextView lblStep01, lblStep02, lblStep03;
    TextView lblStep01_1, lblStep02_1, lblStep03_1;

    ArrayList<STSelectWorker> histories = new ArrayList<>();
    int pagecount = 0;

    int steps = 0;

    public static int m_cur_workamount_pos = -1;
    public static int m_cur_workamount = 0;
    public static ArrayList<Integer> m_changed_workamount_positions = new ArrayList<>();
    public static ArrayList<Integer> m_changed_workamounts = new ArrayList<>();

    ConfirmWorkSubListAdapter.workamountClickListener _workamountClickListener = new ConfirmWorkSubListAdapter.workamountClickListener() {
        @Override
        public void onWorkAmountClicked(int index,int workamount_id) {
            m_cur_workamount_pos = index;
            gotoWorkAmountActivity(workamount_id);
        }
    };

    public void gotoWorkAmountActivity(int workamount_id) {
        Intent i = new Intent(getActivity(), WorkAmountActivity.class);
        i.putExtra("workamount_id",workamount_id);
        getActivity().startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_confirm_work_sub01, container, false);

        lblNoHistories = (TextView) v.findViewById(R.id.lblNoHistories);

        lstItems = (PullToRefreshListView) v.findViewById(R.id.lstItems);
        lstItems.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lstItems.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                callApiGetWorkerHistories();
            }
        });

        rlContents_sub01 = (RelativeLayout) v.findViewById(R.id.rlContents_sub01);

        adapter = new ConfirmWorkSubListAdapter(this,getActivity().getApplicationContext(), _workamountClickListener);
        adapter.setData(histories);
        lstItems.setAdapter(adapter);

        lblStep01 = (TextView) v.findViewById(R.id.lblStep01);
        lblStep01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStepUI(0);
            }
        });
        lblStep02 = (TextView) v.findViewById(R.id.lblStep02);
        lblStep02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStepUI(1);
            }
        });
        lblStep03 = (TextView) v.findViewById(R.id.lblStep03);
        lblStep03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStepUI(2);
            }
        });

        lblStep01_1 = (TextView) v.findViewById(R.id.lblStep01_1);
        lblStep01_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStepUI(0);
            }
        });
        lblStep02_1 = (TextView) v.findViewById(R.id.lblStep02_1);
        lblStep02_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStepUI(1);
            }
        });
        lblStep03_1 = (TextView) v.findViewById(R.id.lblStep03_1);
        lblStep03_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStepUI(2);
            }
        });

        llSteps = (LinearLayout)v.findViewById(R.id.llSteps);
        llActions = (LinearLayout)v.findViewById(R.id.llActions);
        btnRefresh = (Button) v.findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gotoReselectWorkerActivity();
                Toast.makeText(getActivity(),"준비중인 기능입니다...",Toast.LENGTH_SHORT).show();
            }
        });
        btnConfirmSignin = (Button) v.findViewById(R.id.btnConfirmSignin);
        btnConfirmSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.btnYes:
                                if (steps == 0) {
                                    callApiCheckWorkersSignin();
                                } else if (steps == 1) {
                                    callApiSetWorkersElegancies();
                                } else if (steps == 2) {
                                    callApiSetWorkAmounts();
                                }

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
                TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);

                if (steps == 0) {
                    lblMessage.setText(R.string.confirm_signin_check);
                } else if (steps == 1) {
                    lblMessage.setText(R.string.confirm_set_elegancies);
                } else if (steps == 2) {
                    lblMessage.setText(R.string.confirm_set_workamounts);
                }
            }
        });

        updateHistoryList(getArguments().getString(ARG_DATE), getArguments().getInt(ARG_SPOT_ID));

        return v;
    }

    void updateStepUI(int step) {
        ArrayList<STSelectWorker> m_jobs = new ArrayList<>();
        if(step != 0) {
            for (int i = 0; i < histories.size(); i++) {
                if (histories.get(i).f_signin_check == 1)
                    m_jobs.add(histories.get(i));
            }

            if (step == 1 && (histories.size()==0 || histories.get(histories.size() - 1).f_signin_checked == 0)) {
                Functions.showToast(getActivity(), R.string.confirm_signin_first);
                return;
            }

            if (step == 2 && (m_jobs.size()==0 || m_jobs.get(m_jobs.size() - 1).f_elegancy_checked == 0)) {
                Functions.showToast(getActivity(), R.string.confirm_set_elegancies_first);
                return;
            }
        }

        steps = step;
        adapter.setSteps(step);
        adapter.notifyDataSetChanged();

        lblStep01.setBackgroundResource(R.drawable.step_gray);
        lblStep02.setBackgroundResource(R.drawable.step_gray);
        lblStep03.setBackgroundResource(R.drawable.step_gray);
        if (step == 0) {
            lblStep01.setBackgroundResource(R.drawable.step_blue);
            btnConfirmSignin.setText("출근자 확정");
            //btnConfirmSignin.setVisibility(histories.get(histories.size()-1).f_signin_checked==0?View.VISIBLE:View.GONE);
            if(histories.size() > 0)
                llActions.setVisibility(histories.get(histories.size()-1).f_signin_checked==0?View.VISIBLE:View.GONE);
            else llActions.setVisibility(View.GONE);
        } else if (step == 1) {
            lblStep01.setBackgroundResource(R.drawable.step_blue);
            lblStep02.setBackgroundResource(R.drawable.step_blue);
            btnConfirmSignin.setText("평가완료");
            //btnConfirmSignin.setVisibility(histories.get(histories.size()-1).f_elegancy_checked==0?View.VISIBLE:View.GONE);
            btnRefresh.setVisibility(View.GONE);
            if(m_jobs.size()>0)
                llActions.setVisibility(m_jobs.get(m_jobs.size()-1).f_elegancy_checked==0?View.VISIBLE:View.GONE);
            else llActions.setVisibility(View.GONE);
        } else if (step == 2) {
            lblStep01.setBackgroundResource(R.drawable.step_blue);
            lblStep02.setBackgroundResource(R.drawable.step_blue);
            lblStep03.setBackgroundResource(R.drawable.step_blue);
            btnConfirmSignin.setText("작업완료");
            //btnConfirmSignin.setVisibility(histories.get(histories.size()-1).f_workamount_checked==0?View.VISIBLE:View.GONE);
            btnRefresh.setVisibility(View.GONE);
            if (m_jobs.size() > 0)
                llActions.setVisibility(m_jobs.get(m_jobs.size() - 1).f_workamount_checked == 0 ? View.VISIBLE : View.GONE);
            else llActions.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (m_cur_workamount_pos >= 0) {
            m_changed_workamount_positions.add(m_cur_workamount_pos);
            m_changed_workamounts.add(m_cur_workamount);
            m_cur_workamount_pos = -1;
        }
    }

    public void updateHistoryList(String jobdate1, int spot_id1) {
        Bundle args = getArguments();
        args.putString(ARG_DATE, jobdate1);
        args.putInt(ARG_SPOT_ID, spot_id1);

        Log.e("test", "JobDate:"+jobdate1+" spot_id:"+spot_id1);
        callApiGetWorkerHistories();
    }

    void setVisibleContent(int status)
    {
        llSteps.setVisibility(status);
        llActions.setVisibility(status);
        rlContents_sub01.setVisibility(View.VISIBLE);
    }

    void callApiGetWorkerHistories() {
        pagecount = 0;
        histories.clear();
        adapter.setSteps(steps);
        adapter.notifyDataSetChanged();
        if (getArguments().getInt(ARG_SPOT_ID) == 0) {
            Log.e("test","ConfirmWorkSubFragment SPOT ID 0");
            lblNoHistories.setVisibility(View.VISIBLE);
//            rlContents_sub01.setVisibility(View.INVISIBLE);
            setVisibleContent(View.INVISIBLE);
            return;
        }

        if (gettingHistories == true) {
            return;
        }

        gettingHistories = true;
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                gettingHistories = false;
                hideProgress();
                lstItems.onRefreshComplete();

                Log.e("test","callApiGetWorkerHistories:"+response.toString());

                ArrayList<STSelectWorker> newhistories = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetWorkerHistories(response, newhistories);
                for(int i=0;i<newhistories.size();i++) {
                    newhistories.get(i).f_spot_id = getArguments().getInt(ARG_SPOT_ID);
                }

                if (retVal.code != ServiceParams.ERR_NONE) {
                    if (histories.isEmpty()) {
                        lblNoHistories.setVisibility(View.VISIBLE);
//                        rlContents_sub01.setVisibility(View.INVISIBLE);
                        setVisibleContent(View.INVISIBLE);
                    }
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    if(newhistories.isEmpty()) {
                        lblNoHistories.setVisibility(View.VISIBLE);
//                        rlContents_sub01.setVisibility(View.INVISIBLE);
                        setVisibleContent(View.INVISIBLE);
                        return;
                    }
                    lblNoHistories.setVisibility(View.INVISIBLE);
//                    rlContents_sub01.setVisibility(View.VISIBLE);
                    setVisibleContent(View.VISIBLE);

                    pagecount ++;



                    // adapter.setData(histories);
                    histories.addAll(newhistories);
//                    adapter.setSteps(steps);
//                    adapter.notifyDataSetChanged();
                    updateStepUI(steps);

                    for (int i=0; i<m_changed_workamount_positions.size(); i++) {
                        int changedpos = m_changed_workamount_positions.get(i);
                        int changedvalue = m_changed_workamounts.get(i);

                        if (changedpos < adapter.m_jobs.size()) {
                            adapter.m_jobs.get(changedpos).f_workamount_id = changedvalue;
                            for(int j=0;j<histories.size();j++) {
                                if(histories.get(j).f_spot_id==adapter.m_jobs.get(changedpos).f_spot_id && histories.get(j).f_job_id==adapter.m_jobs.get(changedpos).f_job_id && histories.get(j).f_worker_id==adapter.m_jobs.get(changedpos).f_worker_id) {
                                    histories.get(j).f_workamount_id = changedvalue;
                                    break;
                                }
                            }
                        }
                    }

                    if (steps == 0) {
                        //btnConfirmSignin.setVisibility(histories.get(histories.size()-1).f_signin_checked==0?View.VISIBLE:View.GONE);
                        if(histories.size()>0)
                            llActions.setVisibility(histories.get(histories.size()-1).f_signin_checked==0?View.VISIBLE:View.GONE);
                        else llActions.setVisibility(View.GONE);
                    } else if (steps == 1) {
                        //btnConfirmSignin.setVisibility(histories.get(histories.size()-1).f_elegancy_checked==0?View.VISIBLE:View.GONE);
                        btnRefresh.setVisibility(View.GONE);
                        if(adapter.m_jobs.size()>0)
                            llActions.setVisibility(adapter.m_jobs.get(adapter.m_jobs.size()-1).f_elegancy_checked==0?View.VISIBLE:View.GONE);
                        else llActions.setVisibility(View.GONE);
                    } else if (steps == 2) {
                        //btnConfirmSignin.setVisibility(histories.get(histories.size()-1).f_workamount_checked==0?View.VISIBLE:View.GONE);
                        btnRefresh.setVisibility(View.GONE);
                        if(adapter.m_jobs.size()>0)
                            llActions.setVisibility(adapter.m_jobs.get(adapter.m_jobs.size()-1).f_workamount_checked==0?View.VISIBLE:View.GONE);
                        else llActions.setVisibility(View.GONE);
                    }

                    Log.e("test","callApiGetWorkerHistories success");
                }

                if (getArguments().getInt(ARG_SPOT_ID) == 0) {
                    Log.e("test","ConfirmWorkSubFragment SPOT ID 0");
                    lblNoHistories.setVisibility(View.VISIBLE);
//                    rlContents_sub01.setVisibility(View.INVISIBLE);
                    setVisibleContent(View.INVISIBLE);
                    return;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                gettingHistories = false;
                hideProgress();
                lstItems.onRefreshComplete();

                lblNoHistories.setVisibility(View.VISIBLE);
//                rlContents_sub01.setVisibility(View.INVISIBLE);
                setVisibleContent(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                gettingHistories = false;
                hideProgress();
                lstItems.onRefreshComplete();

                lblNoHistories.setVisibility(View.VISIBLE);
//                rlContents_sub01.setVisibility(View.INVISIBLE);
                setVisibleContent(View.INVISIBLE);
            }
        };

        showProgress();

        ServiceManager.inst.getWorkerHistories(getArguments().getInt(ARG_SPOT_ID), getArguments().getString(ARG_DATE), pagecount, ServiceParams.PAGE_SIZE, handler);
    }

    void callApiCheckWorkersSignin() {
        String job_ids = adapter.getJobIds();
        String worker_ids = adapter.getWorkerIds();
        String stats = adapter.getSigninStats();

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    for (int i=0; i<histories.size(); i++) {
                        histories.get(i).f_signin_checked = 1;
                    }

                    steps ++;
                    updateStepUI(steps);
                }
            }
        };

        int spot_id = getArguments().getInt(ARG_SPOT_ID);
        ServiceManager.inst.checkWorkersSignin(spot_id,job_ids, worker_ids, stats, handler);
    }

    void callApiSetWorkersElegancies() {
        String job_ids = adapter.getJobIds();
        String worker_ids = adapter.getWorkerIds();
        String elegancies = adapter.getElegancies();

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    for (int i=0; i<histories.size(); i++) {
                        histories.get(i).f_elegancy_checked = 1;
                    }

                    steps ++;
                    updateStepUI(steps);
                }
            }
        };

        ServiceManager.inst.setWorkersElegancies(job_ids, worker_ids, elegancies, handler);
    }

    void callApiSetWorkAmounts() {
        String job_ids = adapter.getJobIds();
        String worker_ids = adapter.getWorkerIds();
        String workamounts = adapter.getWorkAmounts();

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);

                } else {
                    m_changed_workamount_positions.clear();
                    m_changed_workamounts.clear();

                    for (int i=0; i<histories.size(); i++) {
                        histories.get(i).f_workamount_checked = 1;
                    }

                    steps = 0;
                    updateStepUI(steps);
                }
            }
        };

        ServiceManager.inst.setWorkAmounts(job_ids, worker_ids, workamounts, handler);
    }

    void gotoReselectWorkerActivity(STJobManager job) {
        Intent i = new Intent(getActivity(), ReselectWorkerActivity.class);
        i.putExtra(ReselectWorkerActivity.EXTRA_JOB_ID, job.job.f_id);
        i.putExtra(ReselectWorkerActivity.EXTRA_WORKER_COUNT, job.job.f_worker_count);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}

package com.sincere.kboss.worker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.WorkListAdapter;
import com.sincere.kboss.dialog.NoticeDetailDialog;
import com.sincere.kboss.dialog.YearMonthDialog;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJobWorker;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.chenglei.widget.datepicker.DatePicker;
import org.chenglei.widget.datepicker.MonthPicker;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Michael on 11/2/2016.
 */
public class WorkListFragment extends FragmentTempl implements View.OnClickListener, org.chenglei.widget.datepicker.MonthPicker.OnMonthChangedListener {
    public interface ItemClickListener {
        void onClick(STJobWorker workHistory);
    }

    ItemClickListener itemClickListener = new ItemClickListener() {
        @Override
        public void onClick(STJobWorker workHistory) {
            if (KbossApplication.g_userinfo.minimumRequirement() == false) {
                Functions.showToast(getActivity(), R.string.update_userinfo);
                return;
            }

            MainActivity activity = (MainActivity) getActivity();
            activity.gotoWorkHistoryFragment(workHistory);
        }
    };

    String history_year_month;
    int history_year, history_month;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    int history_count = 0;

    PullToRefreshListView lstItems;
    WorkListAdapter adapter;
    RelativeLayout rlEmptyPane;

    RelativeLayout rlMonthSum;
    TextView lblMonth, lblSum;

    ArrayList<STJobWorker> histories = new ArrayList<>();
    int pagecount = 0;

    Boolean isRefresh = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_worklist, container, false);

        rlEmptyPane = (RelativeLayout) v.findViewById(R.id.rlEmptyPane);

        rlMonthSum = (RelativeLayout) v.findViewById(R.id.rlMonthSum);
        rlMonthSum.setOnClickListener(this);

        lstItems = (PullToRefreshListView) v.findViewById(R.id.lstItems);
        lstItems.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lstItems.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                isRefresh = true;
                updateUI();
            }
        });

        adapter = new WorkListAdapter(getActivity().getApplicationContext(), itemClickListener);
        lstItems.setAdapter(adapter);
        adapter.setData(histories);

        lblMonth = (TextView) v.findViewById(R.id.lblMonth);
        lblSum = (TextView) v.findViewById(R.id.lblSum);

        initHistoryMonth();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMonthSum: {
                /*
                Holder holder = new ViewHolder(R.layout.dialog_monthpicker);
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), false);
                DialogPlus dialog = new DialogPlus.Builder(getActivity())
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(DialogPlus.Gravity.CENTER)
                        .setAdapter(adapter)
                        .create();

                dialog.show();
                MonthPicker monthPicker = (MonthPicker) dialog.findViewById(R.id.month_picker1);

                Date today = new Date();
                try {
                    monthPicker.setDate(sdf.parse(history_year_month));

                } catch (Exception e) {
                    monthPicker.setDate(today);
                }

                monthPicker.setOnMonthChangedListener(WorkListFragment.this);
                break;
                */
                new YearMonthDialog(getActivity(),new YearMonthDialog.ClickListner() {
                    @Override
                    public void okClick(int year,int monthOfYear) {
                        history_year_month = String.format("%04d-%02d", year, monthOfYear);
                        lblMonth.setText(String.format("%04d년 %02d월", year, monthOfYear));

                        history_year = year;
                        history_month = monthOfYear;

                        updateUI();
                    }

                    @Override
                    public void onBack() {

                    }
                }).show();
            }
        }
    }

    @Override
    public void onMonthChanged(MonthPicker view, int year, int monthOfYear) {
        history_year_month = String.format("%04d-%02d", year, monthOfYear);
        lblMonth.setText(String.format("%04d년 %02d월", year, monthOfYear));

        history_year = year;
        history_month = monthOfYear;

        updateUI();
    }

    void updateUI() {
        callApiGetWorkHistoryCount();

        callApiGetWorkHistory();
    }

    void initHistoryMonth() {
        Date today = new Date();

        history_year_month = sdf.format(today);
        history_year = today.getYear() + 1900;
        history_month = today.getMonth() + 1;

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월");
        lblMonth.setText(sdf2.format(today));

        lblSum.setText(String.valueOf(history_count));
    }

    void callApiGetWorkHistoryCount() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseGetWorkHistoryCount(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    rlEmptyPane.setVisibility(View.VISIBLE);
                    history_count = 0;

                } else {
                    history_count = retVal.intData;
                }

                lblSum.setText(String.valueOf(history_count));
            }
        };

        ServiceManager.inst.getWorkHistoryCount(history_year, history_month, handler);
    }

    void callApiGetWorkHistory() {
        pagecount = 0;
        histories.clear();

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                lstItems.onRefreshComplete();

                ArrayList<STJobWorker> newhistories = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetWorkHistory(response, newhistories);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    rlEmptyPane.setVisibility(View.VISIBLE);
                    //Functions.showToast(getActivity(), retVal.msg);
                } else {
                    rlEmptyPane.setVisibility(View.INVISIBLE);
                    pagecount ++;

                    histories.addAll(newhistories);
                    adapter.notifyDataSetChanged();
                }
                scrollMyListViewToBottom();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                lstItems.onRefreshComplete();
                scrollMyListViewToBottom();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                lstItems.onRefreshComplete();
                scrollMyListViewToBottom();
            }
        };

        showProgress();

        ServiceManager.inst.getWorkHistory(history_year, history_month, pagecount, ServiceParams.PAGE_SIZE, handler);
    }

    private void scrollMyListViewToBottom() {
        if(!isRefresh) return;
        if(adapter.getCount() == 0) return;
        lstItems.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                //lstItems.getRefreshableView().smoothScrollToPosition(adapter.getCount());
                lstItems.getRefreshableView().setSelection(adapter.getCount()-1);
                isRefresh = false;
            }
        });
    }
}

package com.sincere.kboss.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.PointListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STPoint;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.11.03.
 */
public class PointFragment extends FragmentTempl {
    TextView lblPointSum;
    PullToRefreshListView lstItems;
    PointListAdapter adapter;

    ArrayList<STPoint> points = new ArrayList<>();
    int pagecount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_points_manager, container, false);

        ImageView btnBack = (ImageView) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.sincere.kboss.manager.MainActivity mainActivity = (com.sincere.kboss.manager.MainActivity) getActivity();
                mainActivity.gotoPrevFragment();
            }
        });

        lblPointSum = (TextView) v.findViewById(R.id.lblPointSum);

        lstItems = (PullToRefreshListView) v.findViewById(R.id.lstItems);
        lstItems.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lstItems.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                callApiGetPoints();
            }
        });

        adapter = new PointListAdapter(getActivity().getApplicationContext(), true);
        adapter.setData(points);
        lstItems.setAdapter(adapter);

        pagecount = 0;
        callApiGetPointSum();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void callApiGetPointSum() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                // hideProgress();
                Log.e("test","callApiGetPointSum:"+response.toString());
                retVal = ServiceManager.inst.parseGetPointSum(response);
                if (retVal.code == ServiceParams.ERR_NONE) {
                    lblPointSum.setText(Functions.getLocaleNumberString(retVal.intData, "P"));

                    callApiGetPoints();
                }
            }
        };

        ServiceManager.inst.getPointSum(handler);
    }

    void callApiGetPoints() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                lstItems.onRefreshComplete();

                ArrayList<STPoint> newPoints = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetPoints(response, newPoints);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    pagecount ++;

                    points.addAll(newPoints);
                    adapter.notifyDataSetChanged();;
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

        ServiceManager.inst.getPoints(pagecount, ServiceParams.PAGE_SIZE, handler);
    }
}

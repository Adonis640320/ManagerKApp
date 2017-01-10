package com.sincere.kboss.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.LinkSpotListAdapter;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STSpot;
import com.sincere.kboss.stdata.STSpotNotReady;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/2/2016.
 */
public class LinkSpotFragment extends FragmentTempl {
    public interface OnSpotClickListener {
        void onClick(STSpot spot);
    }

    ImageView btnBack;
    PullToRefreshListView lstSpots;

    LinkSpotListAdapter adapterLinkSpots;
    ArrayList<STSpot> spots = new ArrayList<STSpot>();

    int pagecount = 0;

    OnSpotClickListener spotClickListener = new OnSpotClickListener() {
        @Override
        public void onClick(STSpot spot) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.gotoSpotInfoFragment(spot);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_link_spot, container, false);

        initUI(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        reloadListView();
    }

    void reloadListView() {

        callApiGetSpots();
    }

    void initUI(View v) {
        btnBack = (ImageView) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.gotoPrevFragment();
            }
        });

        lstSpots = (PullToRefreshListView) v.findViewById(R.id.lstSpots);
        lstSpots.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lstSpots.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                callApiGetSpots();
            }
        });

        adapterLinkSpots = new LinkSpotListAdapter(getActivity().getApplicationContext(), spotClickListener);
        adapterLinkSpots.setData(spots);

        lstSpots.setAdapter(adapterLinkSpots);
    }

    void callApiGetSpots() {
        pagecount = 0;
        spots.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                lstSpots.onRefreshComplete();

                ArrayList<STSpot> newSpots = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetSpots(response, newSpots, false);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    pagecount ++;

                    // adapter.setData(jobs);
                    spots.addAll(newSpots);
                    adapterLinkSpots.notifyDataSetChanged();;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                lstSpots.onRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                lstSpots.onRefreshComplete();
            }
        };

        showProgress();

        ServiceManager.inst.getSpots(handler);
    }
}

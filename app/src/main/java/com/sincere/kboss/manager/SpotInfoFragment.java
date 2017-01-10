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
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.AddManagerListAdapter;
import com.sincere.kboss.adapters.LinkSpotListAdapter;
import com.sincere.kboss.adapters.OwnerListAdapter;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STOwner;
import com.sincere.kboss.stdata.STSpot;
import com.sincere.kboss.stdata.STSpotNotReady;
import com.sincere.kboss.stdata.STUserInfo;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/2/2016.
 */
public class SpotInfoFragment extends FragmentTempl {
    ImageView btnBack;
    ListView lstOwners;
    TextView lblSpotName;
    TextView lblAddress;
    TextView lblBuildCompany;
    TextView lblMainBuilding;
    Button btnAdd;

    public final static String ARG_SPOT = "spot";
    STSpot spot;

    OwnerListAdapter adapter;
    ArrayList<STOwner> owners = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_spot_info, container, false);

        initUI(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        reloadListView();
    }

    void reloadListView() {
        callApiGetOwners();
    }

    void initUI(View v) {
        spot = getArguments().getParcelable(ARG_SPOT);

        btnBack = (ImageView) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.gotoLinkSpotFragment();
            }
        });

        lstOwners = (ListView) v.findViewById(R.id.lstOwners);

        adapter = new OwnerListAdapter(getActivity().getApplicationContext(), R.layout.item_select_manager, R.id.lblName);
        adapter.generateDataset(owners, true);

        lstOwners.setAdapter(adapter);

        lblSpotName = (TextView) v.findViewById(R.id.lblSpotName);
        lblAddress = (TextView) v.findViewById(R.id.lblAddress);
        lblBuildCompany = (TextView) v.findViewById(R.id.lblBuildCompany);
        lblMainBuilding = (TextView) v.findViewById(R.id.lblMainBuilding);
        btnAdd = (Button) v.findViewById(R.id.btnAdd);

        lblSpotName.setText(spot.f_name);
        lblAddress.setText(spot.f_address);
        lblBuildCompany.setText(spot.f_buildcompany);
        lblMainBuilding.setText(spot.f_mainbuilding);

        btnAdd.setVisibility(spot.f_valid==1?View.VISIBLE:View.INVISIBLE);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAddManagerActivity();
            }
        });
    }

    void gotoAddManagerActivity() {
        Intent i = new Intent(getActivity(), AddManagerActivity.class);
        Log.e("Spotinfo:",String.valueOf(spot.f_id));
        i.putExtra(AddManagerActivity.EXTRA_SPOT_ID, spot.f_id);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    void callApiGetOwners() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                owners.clear();
                Log.e("test","callApiGetOwners:"+response.toString());
                retVal = ServiceManager.inst.parseGetOwners(response, owners);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    adapter.generateDataset(owners, true);
                    adapter.notifyDataSetChanged();;
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

        ServiceManager.inst.getOwners(spot.f_id, handler);
    }
}

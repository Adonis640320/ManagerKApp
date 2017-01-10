package com.sincere.kboss.manager;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.CompanyListAdapter;
import com.sincere.kboss.adapters.SpotListAdapter;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STCompany;
import com.sincere.kboss.stdata.STSpotNotReady;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/2/2016.
 */
public class RegisterRequestFragment extends FragmentTempl {
    public interface OnSpotClickListener {
        void onClick(STSpotNotReady spot);
    }

    public interface OnCompanyClickListener {
        void onClick(STCompany company);
    }

    ImageView btnBack;
    PullToRefreshListView lstSpots;
    Button btnRegisterSpot, btnRegisterCompany;
    Button btnRequest;

    SpotListAdapter adapterSpots;
    CompanyListAdapter adapterCompanies;
    ArrayList<STSpotNotReady> spots = new ArrayList<STSpotNotReady>();
    ArrayList<STCompany> companies = new ArrayList<STCompany>();

    int pagecount = 0;

    boolean bIsSpot = true;

    OnSpotClickListener spotClickListener = new OnSpotClickListener() {
        @Override
        public void onClick(STSpotNotReady spot) {
            Intent i = new Intent(getActivity(), SpotNotReadyDetailActivity.class);
            i.putExtra(SpotNotReadyDetailActivity.EXTRA_SPOT, spot);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    };

    OnCompanyClickListener companyClickListener = new OnCompanyClickListener() {
        @Override
        public void onClick(STCompany company) {
            Intent i = new Intent(getActivity(), CompanyNotReadyDetailActivity.class);
            i.putExtra(CompanyNotReadyDetailActivity.EXTRA_COMPANY, company);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_request, container, false);

        initUI(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        reloadListView();
    }

    void reloadListView() {

        if (bIsSpot) {
            callApiGetSpotsNotReady();
        } else {
            callApiGetCompaniesNotReady();
        }
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

        btnRequest = (Button) v.findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bIsSpot) {
                    Intent i = new Intent(getActivity(), RegisterSpotActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                } else {
                    Intent i = new Intent(getActivity(), RegisterCompanyActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }
        });

        lstSpots = (PullToRefreshListView) v.findViewById(R.id.lstSpots);
        lstSpots.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lstSpots.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (bIsSpot) {
                    callApiGetSpotsNotReady();
                } else {
                    callApiGetCompaniesNotReady();
                }
            }
        });

        adapterSpots = new SpotListAdapter(getActivity().getApplicationContext(), spotClickListener);
        adapterCompanies = new CompanyListAdapter(getActivity().getApplicationContext(),companyClickListener);
        adapterSpots.setData(spots);
        adapterCompanies.setData(companies);

        lstSpots.setAdapter(adapterSpots);

        btnRegisterSpot = (Button) v.findViewById(R.id.btnRegisterSpot);
        btnRegisterSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bIsSpot) {
                    return;
                }

                btnRegisterSpot.setBackgroundColor(getResources().getColor(R.color.clr_cyan));
                btnRegisterCompany.setBackgroundColor(getResources().getColor(R.color.clr_gray));

                bIsSpot = true;
                lstSpots.setAdapter(adapterSpots);
                reloadListView();
            }
        });

        btnRegisterCompany = (Button) v.findViewById(R.id.btnRegisterCompany);
        btnRegisterCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bIsSpot == false) {
                    return;
                }

                btnRegisterSpot.setBackgroundColor(getResources().getColor(R.color.clr_gray));
                btnRegisterCompany.setBackgroundColor(getResources().getColor(R.color.clr_cyan));

                bIsSpot = false;
                lstSpots.setAdapter(adapterCompanies);
                reloadListView();
            }
        });
    }

    void callApiGetSpotsNotReady() {
        pagecount = 0;
        spots.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                lstSpots.onRefreshComplete();

                ArrayList<STSpotNotReady> newSpots = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetSpotsNotReady(response, newSpots);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    pagecount ++;

                    // adapter.setData(jobs);
                    spots.addAll(newSpots);
                    adapterSpots.notifyDataSetChanged();;
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

        ServiceManager.inst.getSpotsNotReady(pagecount, ServiceParams.PAGE_SIZE, handler);
    }

    void callApiGetCompaniesNotReady() {
        pagecount = 0;
        companies.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                lstSpots.onRefreshComplete();

                ArrayList<STCompany> newCompanies = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetCompaniesNotReady(response, newCompanies);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    pagecount ++;

                    // adapter.setData(jobs);
                    companies.addAll(newCompanies);
                    adapterCompanies.notifyDataSetChanged();;
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

        ServiceManager.inst.getCompaniesNotReady(pagecount, ServiceParams.PAGE_SIZE, handler);
    }
}

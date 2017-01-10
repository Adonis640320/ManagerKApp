package com.sincere.kboss.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.FavoriteWorkerAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STFavoriteWorker;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/2/2016.
 */
public class FavoriteFragment extends FragmentTempl {
    public interface OnRemoveClickListener {
        void onRemove(int pos, STFavoriteWorker favoriteWorker);
    }

    PullToRefreshListView lstWorkers;
    FavoriteWorkerAdapter adapter;

    ArrayList<STFavoriteWorker> favoriteWorkers = new ArrayList<>();
    int pagecount = 0;

    Boolean isApiCalling = false;

    OnRemoveClickListener removeClickListener = new OnRemoveClickListener() {
        @Override
        public void onRemove(int pos, STFavoriteWorker favoriteWorker) {
            callApiRemoveFavoriteWorker(pos, favoriteWorker.f_spot_id,favoriteWorker.f_worker_id);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite, container, false);

        initUI(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("test","OnResume favorite fragment");
        callApiGetFavoriteWorkers();
    }

    void initUI(View v) {
        lstWorkers = (PullToRefreshListView) v.findViewById(R.id.lstWorkers);
        lstWorkers.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lstWorkers.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                callApiGetFavoriteWorkers();
            }
        });

        adapter = new FavoriteWorkerAdapter(getActivity().getApplicationContext(), removeClickListener);
        adapter.setData(favoriteWorkers);
        lstWorkers.setAdapter(adapter);

    }

    public void updateFragment()
    {
        Log.e("test","update fragment");
        callApiGetFavoriteWorkers();
    }

    void callApiGetFavoriteWorkers() {
        if(isApiCalling) return;
        isApiCalling = true;
        pagecount = 0;
        favoriteWorkers.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                lstWorkers.onRefreshComplete();

                ArrayList<STFavoriteWorker> newWorkers = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetFavoriteWorkers(response, newWorkers);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    pagecount ++;
                    // adapter.setData(jobs);
                    favoriteWorkers.addAll(newWorkers);
                }
                adapter.notifyDataSetChanged();;
                isApiCalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                lstWorkers.onRefreshComplete();
                adapter.notifyDataSetChanged();;
                isApiCalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                lstWorkers.onRefreshComplete();
                adapter.notifyDataSetChanged();;
                isApiCalling = false;
            }
        };

        showProgress();

        ServiceManager.inst.getFavoriteWorkers(pagecount, ServiceParams.PAGE_SIZE, handler);
    }

    void callApiRemoveFavoriteWorker(final int pos,int spot_id, int worker_id) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                lstWorkers.onRefreshComplete();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    favoriteWorkers.remove(pos);
                    adapter.notifyDataSetChanged();;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                lstWorkers.onRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                lstWorkers.onRefreshComplete();
            }
        };

        showProgress();

        ServiceManager.inst.removeFavoriteWorker(spot_id,worker_id, handler);
    }
}

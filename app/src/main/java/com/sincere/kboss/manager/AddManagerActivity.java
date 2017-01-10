package com.sincere.kboss.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.R;
import com.sincere.kboss.SelectPhotoActivity;
import com.sincere.kboss.adapters.AddManagerListAdapter;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STContact;
import com.sincere.kboss.stdata.STFavoriteWorker;
import com.sincere.kboss.stdata.STSpot;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/7/2016.
 */
public class AddManagerActivity extends ActivityTempl {
    // delayed search
    private static final int TRIGGER_SERACH = 1;
    private static final long SEARCH_TRIGGER_DELAY_IN_MS = 1000;

    public Handler handlerKey = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TRIGGER_SERACH) {
                String key=edtKey.getText().toString();
                getFavoriteWorkersFiltered(key);
                if(permissionGranted)
                    Functions.getContacts(getContentResolver(), contacts, key);
                if (contacts.isEmpty()) {
                    adapter.generateDataset(favoriteWorkersFiltered, contacts, true);
                    adapter.notifyDataSetChanged();
                } else {
                    callApiCheckKbossMember();
                }
            }
        }
    };

    EditText edtKey;
    PinnedSectionListView lstItems;
    Spinner spnTitle;
    TextView m_spotname;
    ArrayList<STSpot> m_arrSpot=new ArrayList<>();
    AddManagerListAdapter adapter;
    ArrayList<STFavoriteWorker> favoriteWorkers = new ArrayList<>();
    ArrayList<STFavoriteWorker> favoriteWorkersFiltered = new ArrayList<>();
    ArrayList<STContact> contacts = new ArrayList<>();

    public final static String EXTRA_SPOT_ID = "spot_id";
    int spot_id = 0;
    boolean isApiCalling = false;

    public boolean permissionGranted = false;

    // item click listener
    public interface OnItemClickListener {
        void onAddManagerClick(String phone,int position);
        void onRecommendClick(String phone,int position);
    }
    OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onAddManagerClick(String phone,int position) {
            callApiAddManager(phone,position);
        }

        @Override
        public void onRecommendClick(String phone,int position) {
            callApiRecommend(phone,position);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manager);
        initUI();
        callApiGetSpots();
    }
    void DisplaySpot()
    {
        if ( spot_id==-1)
        {
            m_spotname.setVisibility(View.VISIBLE);
            spnTitle.setVisibility(View.VISIBLE);

            String[] titles = new String[m_arrSpot.size()];
            for (int i=0; i<m_arrSpot.size(); i++) {
                titles[i] = m_arrSpot.get(i).f_name;
                if ( titles[i].length()>15 )
                {
                    titles[i]=(titles[i].substring(0,15))+"...";
                }
            }
            spnTitle.setAdapter(new MyAdapter(
                    spnTitle.getContext(), titles));
        }
        else
        {
            m_spotname.setVisibility(View.VISIBLE);
            spnTitle.setVisibility(View.INVISIBLE);

            for ( int i=0;i<m_arrSpot.size();i++ ) {
                if (m_arrSpot.get(i).f_id == spot_id) {
                    String str=m_arrSpot.get(i).f_name;
                    if ( str.length()>15 )
                        str=str.substring(0,15)+"...";
                    m_spotname.setText(str);
                    getEvent();
                    return;
                }
            }
        }
        getEvent();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
                Functions.getContacts(getContentResolver(), contacts, "");
                callApiGetFavoriteWorkers();
            }
        }

    }

    void initUI() {
        if ( Build.VERSION.SDK_INT>= 23 ) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_CONTACTS", "android.permission.READ_PHONE_STATE"}, 0);
            } else permissionGranted = true;
        } else permissionGranted = true;
        try {
            spot_id = getIntent().getIntExtra(EXTRA_SPOT_ID, 0);
        }catch (Exception e){};
        edtKey = (EditText) findViewById(R.id.edtKey);
        spnTitle = (Spinner) findViewById(R.id.spnTitle);
        m_spotname=(TextView)findViewById(R.id.id_spot_name);
        edtKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                handlerKey.removeMessages(TRIGGER_SERACH);
                handlerKey.sendEmptyMessageDelayed(TRIGGER_SERACH, SEARCH_TRIGGER_DELAY_IN_MS);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        lstItems = (PinnedSectionListView) findViewById(R.id.lstItems);
        adapter = new AddManagerListAdapter(AddManagerActivity.this, R.layout.item_add_manager, R.id.lblName);
        adapter.setItemClickListener(itemClickListener);
        lstItems.setAdapter(adapter);

        if(permissionGranted) {
            Functions.getContacts(getContentResolver(), contacts, "");
            callApiGetFavoriteWorkers();
        }
    }
    public void getEvent()
    {
        handlerKey.removeMessages(TRIGGER_SERACH);
        handlerKey.sendEmptyMessageDelayed(TRIGGER_SERACH, SEARCH_TRIGGER_DELAY_IN_MS);

    }
    void getFavoriteWorkersFiltered(String filter) {
        favoriteWorkersFiltered.clear();

        for (int i=0; i<favoriteWorkers.size(); i++) {
            STFavoriteWorker worker = favoriteWorkers.get(i);

            if (filter.isEmpty() == false) {
                if (!worker.f_name.contains(filter) && !worker.f_mphone.contains(filter)) {
                    continue;
                }
            }
            favoriteWorkersFiltered.add(worker);
        }
    }
    public class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));
            return view;
        }

        @Override
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }

    void callApiGetFavoriteWorkers() {
        favoriteWorkers.clear();
        JsonHttpResponseHandler handler1 = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                ArrayList<STFavoriteWorker> newWorkers = new ArrayList<>();
                retVal = ServiceManager.inst.parseGetFavoriteWorkers(response, newWorkers);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    // adapter.setData(jobs);
                    favoriteWorkers.addAll(newWorkers);
                    getFavoriteWorkersFiltered("");

                    if (contacts.isEmpty()) {
                        adapter.generateDataset(favoriteWorkersFiltered, contacts, true);
                        adapter.notifyDataSetChanged();
                    } else {
                        callApiCheckKbossMember();
                    }
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

        ServiceManager.inst.getFavoriteWorkersForAddManager(0, 50, handler1);
    }

    void callApiCheckKbossMember() {
        JsonHttpResponseHandler handler2 = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseCheckKbossMember(response, contacts);
                //if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                //} else {
                    adapter.generateDataset(favoriteWorkersFiltered, contacts, true);
                    adapter.notifyDataSetChanged();
                //}
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

        ServiceManager.inst.checkKbossMember(contacts, handler2);
    }

    void callApiAddManager(String phone, final int position) {
        if(isApiCalling) return;
        isApiCalling = true;
        JsonHttpResponseHandler handler2 = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(AddManagerActivity.this, retVal.msg);
                } else {
                    // TODO
                    favoriteWorkersFiltered.get(position).f_is_manager = 1;
                    adapter.notifyDataSetChanged();
                }
                isApiCalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                isApiCalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                isApiCalling = false;
            }
        };

        showProgress();

        ServiceManager.inst.addOwner(phone, spot_id, handler2);
    }

    void callApiRecommend(String phone,int position) {
        if(isApiCalling) return;
        isApiCalling = true;
        JsonHttpResponseHandler handler2 = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(AddManagerActivity.this, retVal.msg);
                } else {
                    Functions.showToast(AddManagerActivity.this, R.string.recommended);
                }
                isApiCalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                isApiCalling = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                isApiCalling = false;
            }
        };

        showProgress();

        ServiceManager.inst.recommendKboss(phone, handler2);
    }

    void callApiGetSpots() {

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                // refresh title
                Log.e("test","callApiGetSpots:"+response.toString());
                m_arrSpot.clear();
                retVal = ServiceManager.inst.parseGetSpots(response, m_arrSpot, false);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    m_spotname.setVisibility(View.VISIBLE);
                    spnTitle.setVisibility(View.INVISIBLE);
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    DisplaySpot();
                    //Log.e("test","Spinner Size:"+titles.length);
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
        ServiceManager.inst.getSpots(handler);
    }
}

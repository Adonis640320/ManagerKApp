package com.sincere.kboss.manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.LoginActivity;
import com.sincere.kboss.R;
import com.sincere.kboss.adapters.ConfirmWorkSubListAdapter;
import com.sincere.kboss.adapters.NoticeAdapter;
import com.sincere.kboss.adapters.NoticeListAdapter;
import com.sincere.kboss.dialog.NoticeDetailDialog;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STNotice;
import com.sincere.kboss.stdata.STSelectWorker;
import com.sincere.kboss.utils.CircularImageView;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.sincere.kboss.manager.AddManagerActivity.EXTRA_SPOT_ID;

/**
 * Created by Michael on 11/2/2016.
 */
public class SettingsFragment extends FragmentTempl {
    ToggleButton tglJobNotify;
    CircularImageView imgPhoto;
    LinearLayout llUserInfo;
    Button btnPoints;
    Button btnSpot;
    TextView lblPointValue;
    TextView lblName;

    LinearLayout llLinkSpot, llReqRegister, llAddManager, llInfo;

    DisplayImageOptions options;

    PullToRefreshListView lstHistory;
    ArrayList<STNotice> notices = new ArrayList<>();
    NoticeListAdapter noticeAdapter;
//    ExpandableListView lstItems;
//    NoticeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_manager, container, false);

        initUI(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    void initUI(View v) {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.photo)
                .showImageOnFail(R.drawable.photo)
                .showImageOnLoading(R.drawable.photo)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        tglJobNotify = (ToggleButton)  v.findViewById(R.id.tglJobNotify);
        tglJobNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callApiSendmeJobNotify(isChecked? 1: 0);
            }
        });

        imgPhoto = (CircularImageView) v.findViewById(R.id.imgPhoto);
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoEditAccountActivity();
            }
        });

        llUserInfo = (LinearLayout) v.findViewById(R.id.llUserInfo);
        llUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gotoUserInfoActivity(null);
            }
        });

        btnPoints = (Button) v.findViewById(R.id.btnPoints);
        btnPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.gotoPointsFragment();
            }
        });

        btnSpot = (Button) v.findViewById(R.id.btnSpot);
        btnSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.btnYes:
                                callApiDownToWorker();

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
                lblMessage.setText(R.string.confirm_to_worker);
            }
        });

        lblPointValue = (TextView) v.findViewById(R.id.lblPointValue);
        lblName = (TextView) v.findViewById(R.id.lblName);

        llLinkSpot = (LinearLayout) v.findViewById(R.id.llLinkSpot);
        llLinkSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.gotoLinkSpotFragment();
            }
        });
        llReqRegister = (LinearLayout) v.findViewById(R.id.llReqRegister);
        llReqRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.gotoRegisterRequestFragment();
            }
        });
        llAddManager = (LinearLayout) v.findViewById(R.id.llAddManager);
        llAddManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAddManagerActivity();
            }
        });
        llInfo = (LinearLayout) v.findViewById(R.id.llInfo);
        llInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEditAccountActivity();
            }
        });


        lstHistory = (PullToRefreshListView)v.findViewById(R.id.lstHistory);
        noticeAdapter = new NoticeListAdapter(getActivity(),notices);
        lstHistory.setAdapter(noticeAdapter);

//        lstItems = (ExpandableListView) v.findViewById(R.id.lstHistory);
//        adapter = new NoticeAdapter(getActivity(), notices);
//        lstItems.setAdapter(adapter);

        lstHistory.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lstHistory.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                callApiGetNotices();
            }
        });

        lstHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new NoticeDetailDialog(getActivity(),notices.get(i-1), new NoticeDetailDialog.SelectListner() {
                    @Override
                    public void onBack() {

                    }
                }).show();
            }
        });
    }

    void updateUI() {
        // Update user photo
        if (KbossApplication.g_userinfo == null) {
            return;
        }

        String photouri = KbossApplication.g_userinfo.f_photo;
        if ( KbossApplication.g_user_photo==null ) {
            if (!photouri.isEmpty()) {
                KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + photouri, imgPhoto, options);
            } else {
                imgPhoto.setImageResource(R.drawable.photo);
            }
        }
        else
        {
            imgPhoto.setImageBitmap(KbossApplication.g_user_photo);
        }

        // Update jobnotify toggle button
        tglJobNotify.setChecked(KbossApplication.g_userinfo.f_jobnotify == 1);

        lblName.setText(String.format(getString(R.string.is_manager), KbossApplication.g_userinfo.f_name));

        callApiGetPointSum();
        callApiGetNotices();
    }

    void callApiGetNotices()
    {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                lstHistory.onRefreshComplete();
                notices.clear();
                Log.e("test",response.toString());
                retVal = ServiceManager.inst.parseGetNotices(response,notices);
                if (retVal.code == ServiceParams.ERR_NONE) {
//                    adapter = new NoticeAdapter(getActivity(), notices);
//                    lstItems.setAdapter(adapter);
                    Collections.sort(notices, new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            STNotice p1 = (STNotice) o1;
                            STNotice p2 = (STNotice) o2;
                            return p2.f_date.compareToIgnoreCase(p1.f_date);
                        }
                    });
                    noticeAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                lstHistory.onRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                lstHistory.onRefreshComplete();
            }
        };
        ServiceManager.inst.getNotices(handler);
    }

    void callApiSendmeJobNotify(final int jobnotify) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                // hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code == ServiceParams.ERR_NONE) {
                    KbossApplication.g_userinfo.f_jobnotify = jobnotify;
                }
            }
        };

        ServiceManager.inst.sendmeJobNotify(jobnotify, handler);
    }

    void callApiDownToWorker() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code == ServiceParams.ERR_NONE) {
                    KbossApplication.g_userinfo.f_id = 0;
                    KbossApplication.g_userinfo.f_authkey = "";

                    Functions.saveUserInfo(getActivity().getApplicationContext());

                    Intent i  = new Intent(getActivity(), LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    getActivity().finish();

                } else {
                    Functions.showToast(getActivity(), retVal.msg);
                }
            }
        };

        ServiceManager.inst.downToWorker(handler);
    }

    void callApiGetPointSum() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                // hideProgress();

                retVal = ServiceManager.inst.parseGetPointSum(response);
                if (retVal.code == ServiceParams.ERR_NONE) {
                    lblPointValue.setText(Functions.getLocaleNumberString(retVal.intData, "P"));
                }
            }
        };

        ServiceManager.inst.getPointSum(handler);
    }

    void gotoAddManagerActivity() {
        Intent i = new Intent(getActivity(), AddManagerActivity.class);
        i.putExtra(EXTRA_SPOT_ID,-1);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    void gotoEditAccountActivity() {
        Intent i = new Intent(getActivity(), EditAccountActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}

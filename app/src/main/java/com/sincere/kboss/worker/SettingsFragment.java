package com.sincere.kboss.worker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.sincere.kboss.BankAccountActivity;
import com.sincere.kboss.BasicSecurityActivity;
import com.sincere.kboss.CertActivity;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.UserInfoActivity;
import com.sincere.kboss.adapters.NoticeAdapter;
import com.sincere.kboss.adapters.NoticeListAdapter;
import com.sincere.kboss.dialog.NoticeDetailDialog;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STNotice;
import com.sincere.kboss.utils.CircularImageView;
import com.sincere.kboss.utils.PullToRefreshBase;
import com.sincere.kboss.utils.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    ImageView imgStat01, imgStat02, imgStat03, imgStat04;

    DisplayImageOptions options;
    ArrayList<STNotice> notices = new ArrayList<>();
    PullToRefreshListView lstHistory;
    NoticeListAdapter noticeAdapter;

//    ExpandableListView lstItems;
//    NoticeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_settings_worker, container, false);

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

//        llUserInfo = (LinearLayout) v.findViewById(R.id.llUserInfo);
//        llUserInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gotoUserInfoActivity(null);
//            }
//        });

        TextView setting_individualinfosetting = (TextView)v.findViewById(R.id.setting_individualinfosetting);
        setting_individualinfosetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUserInfoActivity(null);
            }
        });

        LinearLayout llStat1 = (LinearLayout)v.findViewById(R.id.llStat1);
        llStat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUserInfoActivity(null);
                //gotoEditAccountActivity();
            }
        });

        LinearLayout llStat2 = (LinearLayout)v.findViewById(R.id.llStat2);
        llStat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BasicSecurityActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        LinearLayout llStat3 = (LinearLayout)v.findViewById(R.id.llStat3);
        llStat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CertActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        LinearLayout llStat4 = (LinearLayout)v.findViewById(R.id.llStat4);
        llStat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BankAccountActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
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
                MainActivity activity = (MainActivity) getActivity();
                activity.gotoSpotFragment();
            }
        });

        lblPointValue = (TextView) v.findViewById(R.id.lblPointValue);
        lblName = (TextView) v.findViewById(R.id.lblName);

        imgStat01 = (ImageView) v.findViewById(R.id.imgStat01);
        imgStat02 = (ImageView) v.findViewById(R.id.imgStat02);
        imgStat03 = (ImageView) v.findViewById(R.id.imgStat03);
        imgStat04 = (ImageView) v.findViewById(R.id.imgStat04);

//        lstItems = (ExpandableListView) v.findViewById(R.id.lstHistory);
//        adapter = new NoticeAdapter(getActivity(), notices);
//        lstItems.setAdapter(adapter);

        lstHistory = (PullToRefreshListView)v.findViewById(R.id.lstHistory);
        noticeAdapter = new NoticeListAdapter(getActivity(),notices);
        lstHistory.setAdapter(noticeAdapter);

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

        lblName.setText(String.format(getString(R.string.is_worker), KbossApplication.g_userinfo.f_name));

        callApiGetPointSum();

        // user info status
        if (KbossApplication.g_userinfo.minimumRequirement()) {
            imgStat01.setImageResource(R.drawable.setting_basicinfo_red);
        } else {
            imgStat01.setImageResource(R.drawable.setting_basicinfo);
        }

        if (KbossApplication.g_userinfo.f_basicsec_date.isEmpty() || KbossApplication.g_userinfo.f_basicsec_date.equals("0000-00-00 00:00:00")) {
            imgStat02.setImageResource(R.drawable.setting_security);
        } else {
            imgStat02.setImageResource(R.drawable.setting_security_red);
        }

        imgStat03.setImageResource(R.drawable.setting_certificate);
        if (!(KbossApplication.g_userinfo.f_cert_front_date.isEmpty() || KbossApplication.g_userinfo.f_cert_front_date.equals("0000-00-00 00:00:00"))) {
            if (!(KbossApplication.g_userinfo.f_cert_back_date.isEmpty() || KbossApplication.g_userinfo.f_cert_back_date.equals("0000-00-00 00:00:00"))) {
                imgStat03.setImageResource(R.drawable.setting_certificate_red);
            }
        }

        if (KbossApplication.g_userinfo.f_bank_acct.isEmpty()) {
            imgStat04.setImageResource(R.drawable.setting_info);
        } else {
            imgStat04.setImageResource(R.drawable.setting_info_red);
        }

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

    public void gotoUserInfoActivity(View v) {
        Intent i = new Intent(getActivity(), UserInfoActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoEditAccountActivity() {
        if (KbossApplication.g_userinfo.f_level == ServiceParams.LV_MANAGER) {
            Intent i = new Intent(getActivity(), com.sincere.kboss.manager.EditAccountActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            Intent i = new Intent(getActivity(), com.sincere.kboss.worker.EditAccountActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

}

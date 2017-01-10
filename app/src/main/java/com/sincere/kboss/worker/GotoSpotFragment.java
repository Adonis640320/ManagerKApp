package com.sincere.kboss.worker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STSpot;
import com.sincere.kboss.utils.CircularImageView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Star_Man on 2016-11-04.
 */
public class GotoSpotFragment extends FragmentTempl implements View.OnClickListener {
    CircularImageView imgPhoto;
    TextView lblName;
    Button btnGoLater, btnGo;
    TextView lblHint1, lblHint2;

    DisplayImageOptions options;

    ArrayList<STSpot> spots = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_goto_spot, container, false);


        ImageView btnBack = (ImageView) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.sincere.kboss.worker.MainActivity mainActivity = (com.sincere.kboss.worker.MainActivity) getActivity();
                mainActivity.gotoPrevFragment();
            }
        });

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.photo)
                .showImageOnFail(R.drawable.photo)
                .showImageOnLoading(R.drawable.photo)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        imgPhoto = (CircularImageView) v.findViewById(R.id.imgPhoto);
        String photouri = KbossApplication.g_userinfo.f_photo;
        if ( KbossApplication.g_user_photo ==null ) {
            if (!photouri.isEmpty()) {
                KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + photouri, imgPhoto, options);
            } else {
                imgPhoto.setImageResource(R.drawable.photo);
            }
        }
        else
            imgPhoto.setImageBitmap(KbossApplication.g_user_photo);
        lblName = (TextView) v.findViewById(R.id.lblName);
        lblName.setText(String.format(getActivity().getString(R.string.welcome_template), KbossApplication.g_userinfo.f_name));

        btnGoLater = (Button) v.findViewById(R.id.btnGoLater);
        btnGoLater.setOnClickListener(this);
        btnGo = (Button) v.findViewById(R.id.btnGo);
        btnGo.setOnClickListener(this);

        lblHint1 = (TextView) v.findViewById(R.id.lblHint1);
        lblHint2 = (TextView) v.findViewById(R.id.lblHint2);

        callApiCheckOwner();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGo: {
                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.btnYes:
                                dialog.dismiss();

                                callApiConnectSpot();
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

                Button btnYes = (Button)dialog.findViewById(R.id.btnYes);
                btnYes.setBackgroundColor( getResources().getColor(R.color.clr_red_dark));
                TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
                lblMessage.setText(R.string.work_as_owner);
                break;
            }

            case R.id.btnGoLater: {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.gotoPrevFragment();
                break;
            }
        }
    }

    void callApiCheckOwner() {
        spots.clear();

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseCheckOwner(response, spots);
                if (retVal.code == ServiceParams.ERR_NONE) {
                    STSpot spot = spots.get(0);
                    lblHint1.setText(String.format(getString(R.string.connectworkshop_notation1), KbossApplication.g_userinfo.f_name, spot.f_name));
                    lblHint2.setVisibility(View.VISIBLE);
                    lblHint2.setText(R.string.connectworkshop_notation2);

                    btnGoLater.setVisibility(View.VISIBLE);
                    btnGo.setVisibility(View.VISIBLE);

                } else {
                    lblHint1.setText(String.format(getString(R.string.connectworkshop_notation3), KbossApplication.g_userinfo.f_name));
                    lblHint2.setVisibility(View.GONE);
                    btnGoLater.setVisibility(View.INVISIBLE);
                    btnGo.setVisibility(View.INVISIBLE);
                }
            }
        };

        ServiceManager.inst.checkOwner(handler);
    }

    void callApiConnectSpot() {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                KbossApplication.g_userinfo.f_id = 0;
                KbossApplication.g_userinfo.f_authkey = "";

                Functions.saveUserInfo(getActivity().getApplicationContext());

                Intent i  = new Intent(getActivity(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
                getActivity().finish();
            }
        };

        ServiceManager.inst.connectSpot(spots.get(0).f_id, 1, handler);
    }
}

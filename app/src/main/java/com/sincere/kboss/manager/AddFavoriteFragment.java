package com.sincere.kboss.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.PayTypeActivity;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STSelectWorker;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by John on 12/13/2016.
 */

public class AddFavoriteFragment extends FragmentTempl {

    TextView lblTitle;
    ImageView imgPhoto;

    TextView tvFavoriteAdd;

    public final static String ARG_WORKER = "worker";
    STSelectWorker worker;

    ProgressWheel pw;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_favorite, container, false);

        initUI(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    void initUI(View v) {
        worker = getArguments().getParcelable(ARG_WORKER);

        ImageView btnBack = (ImageView) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.sincere.kboss.manager.MainActivity mainActivity = (com.sincere.kboss.manager.MainActivity) getActivity();
                mainActivity.gotoPrevFragment();
                int spot_id;
                if (MainActivity.g_curSpot < 0) {
                    spot_id = 0;
                } else {
                    spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
                }
                ConfirmWorkFragment.registeredFragments.get(ConfirmWorkFragment.curFrag).updateHistoryList(Functions.getDateTimeStringFromToday(ConfirmWorkFragment.curFrag-28), spot_id);
            }
        });

        tvFavoriteAdd = (TextView)v.findViewById(R.id.lbl01);
        tvFavoriteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(tvFavoriteAdd.getText().equals("관심인물 추가")) {
                   callApiAddFavoriteWorker();
               } else {
                   callApiRemoveFavoriteWorker();
               }
            }
        });

        lblTitle = (TextView) v.findViewById(R.id.lblTitle);
        lblTitle.setText(worker.f_name);

        imgPhoto = (ImageView) v.findViewById(R.id.imgPhoto);
        String photouri = worker.f_photo;
        if (!photouri.isEmpty()) {
            KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + photouri, imgPhoto, KbossApplication.g_imageLoaderOptions);
        } else {
            imgPhoto.setImageResource(R.drawable.photo);
        }

        TextView tvWorkDate = (TextView) v.findViewById(R.id.tvWorkDate);
        tvWorkDate.setText(Functions.changeDateString(worker.f_workdate));

        TextView tvSkill = (TextView) v.findViewById(R.id.tvSkill);
        tvSkill.setText(Functions.getJobsString(String.valueOf(worker.f_skill), getActivity()));

        TextView tvPayment = (TextView) v.findViewById(R.id.tvPayment);
        tvPayment.setText(Functions.getLocaleNumberString(worker.f_payment, ""));

        TextView tvElagancy = (TextView) v.findViewById(R.id.tvElagancy);
        switch (worker.f_elegancy_id)
        {
            case 0: tvElagancy.setText("하"); break;
            case 1: tvElagancy.setText("중"); break;
            case 2: tvElagancy.setText("상"); break;
        }
        TextView tvWorkAmount = (TextView)v.findViewById(R.id.tvWorkAmount);
        tvWorkAmount.setText(String.valueOf(KbossApplication.g_workamounts.get(worker.f_workamount_id).f_workamount));

        TextView tvWorkConfirmDate = (TextView)v.findViewById(R.id.tvWorkConfirmDate);
        tvWorkConfirmDate.setText(Functions.changeDateTimeForm1(worker.f_signout_time));

        TextView tvConfirmer = (TextView)v.findViewById(R.id.tvConfirmer);
        tvConfirmer.setText(KbossApplication.g_userinfo.f_name);

        Button btnModifyWorkInfo = (Button)v.findViewById(R.id.btnModifyWorkInfo);

        if(worker.f_signout_check == 1) {
            btnModifyWorkInfo.setVisibility(View.GONE);
        } else {
            btnModifyWorkInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnClickListener clickListener = new OnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialog, View view) {
                            switch (view.getId()) {
                                case R.id.btnYes:
                                    MainActivity mainActivity = (MainActivity)getActivity();
                                    mainActivity.gotoModifyWorkInfoFragment(worker);
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

                    Button btnYes = (Button)dialog.findViewById(R.id.btnYes);
                    TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
                    lblMessage.setText("작업정보 수정 시\n확인 일시가 업데이트 됩니다.\n\n정말 수정하시겠습니까?");
                }
            });
        }

        Button btnReqEverydayWork = (Button)v.findViewById(R.id.btnReqEverydayWork);
        btnReqEverydayWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApiRequestNextdayWork();
            }
        });

        pw = (ProgressWheel)v.findViewById(R.id.pwLoading);
        setupProgress(0);

        callApiGetIsFavoriteWorker(worker.f_spot_id, worker.f_worker_id);
    }

    void callApiRequestNextdayWork() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    Toast.makeText(getActivity(),"익일 근로요청되였습니다.",Toast.LENGTH_SHORT).show();

                    // added by Adonis

                    ((MainActivity) getActivity()).vpContents.setCurrentItem(0);

                    com.sincere.kboss.manager.MainActivity mainActivity = (com.sincere.kboss.manager.MainActivity) getActivity();
                    mainActivity.gotoPrevFragment();
                    int spot_id;
                    if (MainActivity.g_curSpot < 0) {
                        spot_id = 0;
                    } else {
                        spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
                    }
                    ConfirmWorkFragment.registeredFragments.get(ConfirmWorkFragment.curFrag).updateHistoryList(Functions.getDateTimeStringFromToday(ConfirmWorkFragment.curFrag-28), spot_id);

                    // by Adonis
                }
            }
        };
        //Log.e("test","jobid:"+worker.f_job_id+" workerid:"+worker.f_worker_id);
        //Toast.makeText(getActivity(),"jobid:"+worker.f_job_id+" workerid:"+worker.f_worker_id,Toast.LENGTH_SHORT).show();
        ServiceManager.inst.requestNextdayWork(worker.f_job_id,worker.f_worker_id, handler);
    }

    void callApiGetIsFavoriteWorker(int spot_id,int worker_id) {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                     try {
                         if(response.getInt(ServiceParams.SVCC_DATA)  == 0) {
                             tvFavoriteAdd.setText("관심인물 추가");
                         } else {
                             tvFavoriteAdd.setText("관심인물 제거");
                         }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    };
                }
            }
        };

        ServiceManager.inst.isFavoriteWorker(spot_id,worker_id, handler);
    }

    void callApiAddFavoriteWorker() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();
                Log.e("test","callApiAddFavoriteWorker:"+response.toString());
                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    Functions.showToast(getActivity(), "관심인물로 추가하였습니다.");
                    tvFavoriteAdd.setText("관심인물 제거");
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.updateFavoriteFragments();

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

        ServiceManager.inst.addFavoriteWorker(worker.f_spot_id, worker.f_worker_id, handler);
    }

    void callApiRemoveFavoriteWorker() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(getActivity(), retVal.msg);
                } else {
                    tvFavoriteAdd.setText("관심인물 추가");
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.updateFavoriteFragments();
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

        ServiceManager.inst.removeFavoriteWorker(worker.f_spot_id, worker.f_worker_id, handler);
    }

//    public void showProgress() {
//        if (pw != null) {
//            pw.setVisibility(View.VISIBLE);
//        }
//    }
//
//    public void hideProgress() {
//        if (pw != null) {
//            pw.setVisibility(View.INVISIBLE);
//        }
//    }
}

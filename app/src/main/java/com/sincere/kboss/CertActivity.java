package com.sincere.kboss;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.nostra13.universalimageloader.utils.L;
import com.orhanobut.android.dialogplussample.SimpleAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STCert;
import com.sincere.kboss.stdata.STSpot;
import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Michael on 10/26/2016.
 */
public class CertActivity extends ActivityTempl {
    ImageView imgCert;
    ImageView imgCert02;
    ImageView btnBack;
    Button btnDeleteFront;
    Button btnDeleteBack;
    TextView btnConfirm;
    DisplayImageOptions options;

    String photoPath = "";
    String photoPath02 = "";
    boolean f_front_remove;
    boolean f_back_remove;
    LinearLayout llBottom;//,llRegFrontCertDate,llRegBackCertDate;
    //TextView tvRegFrontCertDate,tvRegBackCertDate;
    LinearLayout llCertRegHistory;
    TextView tvFrontGuide, tvBackGuide;

    ArrayList<STCert> certRegHistory = new ArrayList<STCert>();

    final static int INTENT_SELECT_CERT = 200;
    final static int INTENT_SELECT_CERT02 = 201;

    View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!photoPath.isEmpty()) {
                callApiSetCertFront();
            }
            if (!photoPath02.isEmpty()) {
                callApiSetCertBack();
            }
            if ( f_front_remove )
                callApiDeleteCertFront();
            if ( f_back_remove )
                callApiDeleteCertBack();
            if ( photoPath.isEmpty() && photoPath02.isEmpty() && !f_back_remove && !f_front_remove )
                returnBack(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert);
        f_front_remove=false;
        f_back_remove=false;
        initUI();

        callApiGetCertRegHistory();
        updateUI();

    }

    void initUI() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.cert_templ)
                .showImageOnFail(R.drawable.cert_templ)
                .showImageOnLoading(R.drawable.cert_templ)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        tvFrontGuide = (TextView)findViewById(R.id.tvFrontGuide);
        tvBackGuide = (TextView)findViewById(R.id.tvBackGuide);

        llBottom = (LinearLayout)findViewById(R.id.llBottom);
//        llRegFrontCertDate = (LinearLayout)findViewById(R.id.llRegFrontCertDate);
//        llRegBackCertDate = (LinearLayout)findViewById(R.id.llRegBackCertDate);

        llCertRegHistory = (LinearLayout)findViewById(R.id.llCertRegHistory);

        imgCert = (ImageView) findViewById(R.id.imgCert);
        imgCert02 = (ImageView) findViewById(R.id.imgCert02);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnBackClickListener);

        btnDeleteFront = (Button) findViewById(R.id.btnDeleteFront);
        btnDeleteFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String certuri = KbossApplication.g_userinfo.f_cert_front;
                if (KbossApplication.g_certfront_img == null && certuri.isEmpty()) return;
                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.btnYes:
                                f_front_remove=true;
                                dialog.dismiss();
                                KbossApplication.g_userinfo.f_cert_front_date = "";
                                KbossApplication.g_userinfo.f_cert_front = "";
                                KbossApplication.g_certfront_img = null;

                                updateUI();
                                break;

                            case R.id.btnNo:
                                dialog.dismiss();
                                break;
                        }
                    }
                } ;

                Holder holder = new ViewHolder(R.layout.dialog_yesno);
                SimpleAdapter adapter = new SimpleAdapter(CertActivity.this, false);
                DialogPlus dialog = new DialogPlus.Builder(CertActivity.this)
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(DialogPlus.Gravity.CENTER)
                        .setAdapter(adapter)
                        .setOnClickListener(clickListener)
                        .create();

                dialog.show();
                Button btnYes = (Button)dialog.findViewById(R.id.btnYes);
                btnYes.setBackgroundColor( getResources().getColor(R.color.clr_red_dark));

            }
        });

        btnDeleteBack = (Button) findViewById(R.id.btnDeleteBack);
        btnDeleteBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String certuri = KbossApplication.g_userinfo.f_cert_back;
                if (KbossApplication.g_certback_img == null && certuri.isEmpty()) return;
                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.btnYes:
                                f_back_remove=true;
                                dialog.dismiss();

                                KbossApplication.g_userinfo.f_cert_back_date = "";
                                KbossApplication.g_userinfo.f_cert_back = "";
                                KbossApplication.g_certback_img = null;

                                updateUI();
                                break;

                            case R.id.btnNo:
                                dialog.dismiss();
                                break;
                        }
                    }
                } ;

                Holder holder = new ViewHolder(R.layout.dialog_yesno);
                SimpleAdapter adapter = new SimpleAdapter(CertActivity.this, false);
                DialogPlus dialog = new DialogPlus.Builder(CertActivity.this)
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(DialogPlus.Gravity.CENTER)
                        .setAdapter(adapter)
                        .setOnClickListener(clickListener)
                        .create();

                dialog.show();
                Button btnYes = (Button)dialog.findViewById(R.id.btnYes);
                btnYes.setBackgroundColor( getResources().getColor(R.color.clr_red_dark));

            }
        });
        btnConfirm=(TextView)findViewById(R.id.btnConfirm);
        btnConfirm.setClickable(true);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!photoPath.isEmpty()) {
                    callApiSetCertFront();
                }
                if (!photoPath02.isEmpty()) {
                    callApiSetCertBack();
                }
                if ( f_front_remove )
                    callApiDeleteCertFront();
                if ( f_back_remove )
                    callApiDeleteCertBack();
                if ( photoPath.isEmpty() && photoPath02.isEmpty() && !f_back_remove && !f_front_remove )
                    returnBack(null);
            }
        });
        // modified by Adonis
        imgCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(CertActivity.this, SelectPhotoActivity.class);
  // startActivityForResult(intent, INTENT_SELECT_CERT);*/
                Intent intent = new Intent(CertActivity.this, PhotoSelectMainActivity.class);
                startActivityForResult(intent, INTENT_SELECT_CERT);
            }
        });

        imgCert02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Intent intent = new Intent(CertActivity.this, SelectPhotoActivity.class);
                startActivityForResult(intent, INTENT_SELECT_CERT02);*/

                Intent intent = new Intent(CertActivity.this, PhotoSelectMainActivity.class);
                startActivityForResult(intent, INTENT_SELECT_CERT02);
            }
        });

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
        setupProgress(0);
    }

    void updateUI() {

//        if(KbossApplication.g_userinfo.f_cert_front_date!="" && !KbossApplication.g_userinfo.f_cert_front_date.equals("0000-00-00 00:00:00")) {
//            tvRegFrontCertDate.setText("신분증 앞면을 등록했습니다.("+Functions.changeDateString(KbossApplication.g_userinfo.f_cert_front_date)+")");
//        } else llRegFrontCertDate.setVisibility(View.GONE);
//        if(KbossApplication.g_userinfo.f_cert_back_date!="" && !KbossApplication.g_userinfo.f_cert_back_date.equals("0000-00-00 00:00:00")) {
//            tvRegBackCertDate.setText("신분증 뒷면을 등록했습니다.("+Functions.changeDateString(KbossApplication.g_userinfo.f_cert_back_date)+")");
//        } else llRegBackCertDate.setVisibility(View.GONE);
//
//        if(KbossApplication.g_userinfo.f_cert_front.isEmpty() && KbossApplication.g_userinfo.f_cert_back.isEmpty())  llBottom.setVisibility(View.GONE);


        if (KbossApplication.g_certfront_img != null) {
            imgCert.setImageBitmap(KbossApplication.g_certfront_img);
        } else {
            String certuri = KbossApplication.g_userinfo.f_cert_front;
            if (!certuri.isEmpty()) {
                KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + "cert/"+ certuri, imgCert, options);
            } else {
                imgCert.setImageResource(R.drawable.cert_templ);
            }
        }

        if (KbossApplication.g_certback_img != null) {
            imgCert02.setImageBitmap(KbossApplication.g_certback_img);
        } else {
            String certuri = KbossApplication.g_userinfo.f_cert_back;
            if (!certuri.isEmpty()) {
                Log.e("test",ServiceParams.assetsBaseUrl + "cert/"+ certuri);
                KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + "cert/"+ certuri, imgCert02, options);
            } else {
                imgCert02.setImageResource(R.drawable.cert_templ);
            }
        }

        String certuri = KbossApplication.g_userinfo.f_cert_front;
        if (KbossApplication.g_certfront_img == null && certuri.isEmpty()) {
            btnDeleteFront.setVisibility(View.GONE);
            tvFrontGuide.setVisibility(View.VISIBLE);
        }
        else {
            btnDeleteFront.setVisibility(View.VISIBLE);
            tvFrontGuide.setVisibility(View.GONE);
        }

        String certuri1 = KbossApplication.g_userinfo.f_cert_back;
        if (KbossApplication.g_certback_img == null && certuri1.isEmpty()) {
            btnDeleteBack.setVisibility(View.GONE);
            tvBackGuide.setVisibility(View.VISIBLE);
        }
        else {
            btnDeleteBack.setVisibility(View.VISIBLE);
            tvBackGuide.setVisibility(View.GONE);
        }

    }

    private void updateCertWithPath(String szPath, boolean isFront)
    {
        try {
			/* Update user photo info view */
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(szPath, options);

            if (bitmap != null)
            {
                int nWidth = bitmap.getWidth(), nHeight = bitmap.getHeight();
                int nScaledWidth = 0, nScaledHeight = 0;
                if (nWidth > nHeight)
                {
                    nScaledWidth = SelectPhotoActivity.IMAGE_WIDTH;
                    nScaledHeight = nScaledWidth * nHeight / nWidth;
                }
                else
                {
                    nScaledHeight = SelectPhotoActivity.IMAGE_HEIGHT;
                    nScaledWidth = nScaledHeight * nWidth / nHeight;
                }

                if (isFront) {
                    KbossApplication.g_certfront_img = Bitmap.createScaledBitmap(bitmap, nScaledWidth, nScaledHeight, false);;
                    imgCert.setImageBitmap(KbossApplication.g_certfront_img);
                } else {
                    KbossApplication.g_certback_img = Bitmap.createScaledBitmap(bitmap, nScaledWidth, nScaledHeight, false);;
                    imgCert02.setImageBitmap(KbossApplication.g_certback_img);
                }
                String certuri = KbossApplication.g_userinfo.f_cert_front;
                if (KbossApplication.g_certfront_img == null && certuri.isEmpty()) btnDeleteFront.setVisibility(View.GONE);
                else btnDeleteFront.setVisibility(View.VISIBLE);

                String certuri1 = KbossApplication.g_userinfo.f_cert_back;
                if (KbossApplication.g_certback_img == null && certuri1.isEmpty()) btnDeleteBack.setVisibility(View.GONE);
                else btnDeleteBack.setVisibility(View.VISIBLE);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_SELECT_CERT) {
            if (resultCode == RESULT_OK) {
                f_front_remove=false;
                Uri fileUri = data.getParcelableExtra(SelectPhotoActivity.szRetUri);

                String filepath = data.getStringExtra(SelectPhotoActivity.szRetPath);
                int rescode = data.getIntExtra(SelectPhotoActivity.szRetCode, SelectPhotoActivity.nRetCancelled);
                if (rescode == SelectPhotoActivity.nRetSuccess) {
                    if (fileUri == null) {
                        photoPath = filepath;
                    } else {
                        //photoPath = fileUri.getPath();
                        String uri = fileUri.toString();
                        String[] projection = {MediaStore.Images.Media.DATA};
                        String where = MediaStore.Images.Media._ID + " = " + uri.substring(uri.lastIndexOf("/") + 1);
                        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, null );
                        if(cursor.moveToFirst()){
                            photoPath = cursor.getString(0);
                            tvFrontGuide.setVisibility(View.GONE);
                        } else {
                            photoPath = "";
                        }
                        cursor.close();
                    }

                    updateCertWithPath(photoPath, true);
                    //Toast.makeText(this, "select picture->  path:" + photoPath, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Occur error during selecting picture", Toast.LENGTH_SHORT).show();
                    photoPath = "";
                }
            }
        } else if (requestCode == INTENT_SELECT_CERT02) {
            if (resultCode == RESULT_OK) {
                f_back_remove=false;
                Uri fileUri = data.getParcelableExtra(SelectPhotoActivity.szRetUri);

                String filepath = data.getStringExtra(SelectPhotoActivity.szRetPath);
                int rescode = data.getIntExtra(SelectPhotoActivity.szRetCode, SelectPhotoActivity.nRetCancelled);
                if (rescode == SelectPhotoActivity.nRetSuccess) {
                    if (fileUri == null) {
                        photoPath02 = filepath;
                    } else {
                        //photoPath = fileUri.getPath();
                        String uri = fileUri.toString();
                        String[] projection = {MediaStore.Images.Media.DATA};
                        String where = MediaStore.Images.Media._ID + " = " + uri.substring(uri.lastIndexOf("/") + 1);
                        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, null );
                        if(cursor.moveToFirst()){
                            photoPath02 = cursor.getString(0);
                            tvBackGuide.setVisibility(View.GONE);
                        } else {
                            photoPath02 = "";
                        }
                        cursor.close();
                    }

                    updateCertWithPath(photoPath02, false);
                    //Toast.makeText(this, "select picture->  path:" + photoPath, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Occur error during selecting picture", Toast.LENGTH_SHORT).show();
                    photoPath02 = "";
                }
            }
        }
    }

    void callApiSetCertFront() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(CertActivity.this, retVal.msg);
                } else {
                    //Date date = new Date();
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //KbossApplication.g_userinfo.f_cert_front_date = sdf.format(date);
                    try {
                        JSONObject object =  response.getJSONObject(ServiceParams.SVCC_DATA);
                        KbossApplication.g_userinfo.f_cert_front_date = object.getString("DATE");
                        KbossApplication.g_userinfo.f_cert_front = object.getString("IMAGE_NAME");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (photoPath02.isEmpty()) {
                        returnBack(null);
                    } else {
                        callApiSetCertBack();
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

        ServiceManager.inst.setCertFront(photoPath, handler);
    }

    void callApiSetCertBack() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(CertActivity.this, retVal.msg);
                } else {
                    //Date date = new Date();
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //KbossApplication.g_userinfo.f_cert_back_date = sdf.format(date);
                    try {
                        JSONObject object =  response.getJSONObject(ServiceParams.SVCC_DATA);
                        KbossApplication.g_userinfo.f_cert_back_date = object.getString("DATE");
                        KbossApplication.g_userinfo.f_cert_back = object.getString("IMAGE_NAME");
                        returnBack(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
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

        ServiceManager.inst.setCertBack(photoPath02, handler);
    }

    void callApiDeleteCertFront() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(CertActivity.this, retVal.msg);
                } else {
                    KbossApplication.g_userinfo.f_cert_front_date = "";
                    KbossApplication.g_userinfo.f_cert_front = "";
                    KbossApplication.g_certfront_img = null;

                    updateUI();
                    if ( f_back_remove )
                    {
                        callApiDeleteCertBack();
                    }
                    else
                        returnBack(null);

                }
                returnBack(null);
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

        ServiceManager.inst.deleteCertFront(handler);
    }

    void callApiDeleteCertBack() {
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseNoData(response);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    Functions.showToast(CertActivity.this, retVal.msg);
                } else {
                    KbossApplication.g_userinfo.f_cert_back_date = "";
                    KbossApplication.g_userinfo.f_cert_back = "";
                    KbossApplication.g_certback_img = null;

                    updateUI();
                    returnBack(null);
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

        ServiceManager.inst.deleteCertBack(handler);
    }


    void callApiGetCertRegHistory() {

        certRegHistory.clear();
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                retVal = ServiceManager.inst.parseGetCerts(response, certRegHistory);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    llCertRegHistory.removeAllViews();
                    for(int i=0;i<certRegHistory.size();i++) {
                        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.item_info_message,null);
                        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
                        if(certRegHistory.get(i).f_cert_type == 0) {
                            tvMessage.setText("신분증 앞면을 등록했습니다.("+ Functions.changeDateString(certRegHistory.get(i).f_cert_date)+")");
                        } else {
                            tvMessage.setText("신분증 뒤면을 등록했습니다.("+ Functions.changeDateString(certRegHistory.get(i).f_cert_date)+")");
                        }
                        llCertRegHistory.addView(view);
                    }
                }
                if(certRegHistory.size() == 0) llBottom.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                hideProgress();
                if(certRegHistory.size() == 0) llBottom.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                hideProgress();
                if(certRegHistory.size() == 0) llBottom.setVisibility(View.GONE);
            }
        };

        showProgress();

        ServiceManager.inst.getCerts(handler);
    }

    public void onConfirmPressed(View view)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        btnBackClickListener.onClick(null);
    }
}

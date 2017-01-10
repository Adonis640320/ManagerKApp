package com.sincere.kboss.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STSelectWorker;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    private static final String TAG = "MyFirebaseIIDService";

    private KbossApplication m_app;
    JsonHttpResponseHandler handler;
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.

        m_app = KbossApplication.getInstance();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);
        Log.e("test", "MyFirebaseInstanceIDService token : "+token);
        if(token != null && token!="" && KbossApplication.g_userinfo != null)
            sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.e("test","MyFirebaseInstanceIDService success "+response.toString());
                retVal = new RetVal();
                try {
                    retVal.code = response.getInt(ServiceParams.SVCC_RET);
                    retVal.msg = response.getString(ServiceParams.SVCC_RETMSG);
                }catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (retVal.code != ServiceParams.ERR_NONE) {
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("test","MyFirebaseInstanceIDService failed ");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("test","MyFirebaseInstanceIDService failed ");
            }
        };
       ServiceManager.inst.setToken(token, handler);

        String data = FirebaseInstanceId.getInstance().getToken();
        m_app.setSharedPreferencesData("fcm_token", data);
        Log.e("test", "MyFirebaseInstanceIDService pref token:"+m_app.getSharedPreferencesData("fcm_token",""));
    }
}
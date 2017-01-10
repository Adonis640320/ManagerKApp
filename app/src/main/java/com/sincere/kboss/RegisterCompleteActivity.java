package com.sincere.kboss;

import android.content.Intent;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STUserInfo;
import com.sincere.kboss.utils.CircularImageView;
import com.sincere.kboss.worker.MainActivity;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.10.26.
 */
public class RegisterCompleteActivity extends ActivityTempl {
    CircularImageView circlePhoto;
    TextView signupfinish_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registercomplete);

        initUI();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, FirstActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    void initUI() {
        circlePhoto = (CircularImageView) findViewById(R.id.circlePhoto);
        if (KbossApplication.g_user_photo != null) {
            circlePhoto.setImageBitmap(KbossApplication.g_user_photo);
        }

        signupfinish_name = (TextView) findViewById(R.id.signupfinish_name);
        String username = KbossApplication.g_userinfo.f_name;
        signupfinish_name.setText(String.format(getString(R.string.welcome_template), username));
    }

    public void gotoUserInfoActivity(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(MainActivity.EXTRA_GOTO_USERINFO, true);
        startActivity(i);

        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    public void gotoSettingsFragment(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(MainActivity.EXTRA_GOTO_SETTINGS, true);
        startActivity(i);

        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    public void gotoSpotFragment(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(MainActivity.EXTRA_SPOT_FRAGMENT, true);
        startActivity(i);

        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }
}

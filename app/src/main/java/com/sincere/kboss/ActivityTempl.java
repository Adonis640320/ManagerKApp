package com.sincere.kboss;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.global.Functions;
import com.todddavies.components.progressbar.ProgressWheel;

/**
 * Created by Michael on 2016.10.25.
 */
public class ActivityTempl extends AppCompatActivity {
    protected JsonHttpResponseHandler handler;
    protected ProgressWheel pw = null;

    @Override
    protected void onResume() {
        super.onResume();

        // suppress the keyboard until the user actually touches the edittext view
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        hideProgress();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Functions.hideVirtualKeyboard(this);

        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void returnBack(View v) {
        Functions.hideVirtualKeyboard(this);

        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void setupProgress(String text) {
        if (pw != null) {
            pw.setSpinSpeed(10);
            pw.setBarWidth(10);
            pw.setRimColor(0x33FFFFFF);
            pw.setRimWidth(14);
            pw.setBarColor(0x77000000);
            pw.setContourColor(0x00000000);
            if (text.isEmpty()) {
                pw.setText(getString(R.string.please_wait));
            } else {
                pw.setText(text);
            }
            pw.setTextColor(0xAA000000);
            pw.setTextSize((int)getResources().getDimension(R.dimen.ftsize_5));
            pw.spin();
        }
    }

    public void setupProgress(int resid) {
        if (resid == 0) {
            setupProgress("");
        } else {
            setupProgress(getString(resid));
        }
    }

    public void showProgress() {
        if (pw != null) {
            pw.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if (pw != null) {
            pw.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }

    }
}

package com.sincere.kboss;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.sincere.kboss.global.Functions;

/**
 * Created by Michael on 2016.10.25.
 */
public class RegisterStartActivity extends ActivityTempl {
    CheckBox allagreeCheckbox;
    CheckBox useagreeCheckbox;
    CheckBox privacyagreeCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerstart);

        allagreeCheckbox = (CheckBox) findViewById(R.id.licenseagree_allagree_checkbox);
        useagreeCheckbox = (CheckBox) findViewById(R.id.licenseagree_useagree);
        privacyagreeCheckbox = (CheckBox) findViewById(R.id.licenseagree_infoagree);
    }

    public void gotoLicenseActivity(View v) {
        Intent i = new Intent(this, LicenseActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        //
    }

    public void gotoPrivacyActivity(View v) {
        Intent i = new Intent(this, PrivacyActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        //
    }

    public void gotoRegisterInputActivity(View v) {
        if (allagreeCheckbox.isChecked()) {
            Intent i = new Intent(this, RegisterInputActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            Functions.showToast(RegisterStartActivity.this, R.string.please_agree_license);
        }
        //
    }

    public void onAllAgreeClicked(View v){
        if (allagreeCheckbox.isChecked())
        {
            useagreeCheckbox.setChecked(true);
            privacyagreeCheckbox.setChecked(true);
        }
        else
        {
            useagreeCheckbox.setChecked(false);
            privacyagreeCheckbox.setChecked(false);
        }
    }

    public void onUseAgreeClicked(View v){
        if (useagreeCheckbox.isChecked() && privacyagreeCheckbox.isChecked())
        {
            allagreeCheckbox.setChecked(true);
        }
        else
        {
            allagreeCheckbox.setChecked(false);
        }
    }

    public void onPrivacyAgreeClicked(View v){
        if (useagreeCheckbox.isChecked() && privacyagreeCheckbox.isChecked())
        {
            allagreeCheckbox.setChecked(true);
        }
        else
        {
            allagreeCheckbox.setChecked(false);
        }
    }
}

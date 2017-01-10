package com.sincere.kboss;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sincere.kboss.global.Functions;
import com.todddavies.components.progressbar.ProgressWheel;

/**
 * Created by Michael on 2016.10.25.
 */
public class FirstActivity extends ActivityTempl {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        pw = (ProgressWheel) findViewById(R.id.pwLoading);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void gotoLoginActivity(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        //finish();
    }

    public void gotoRegisterStartActivity(View v){
        Intent i = new Intent(this, RegisterStartActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        //finish();
    }

    @Override
    public void onBackPressed() {
        if (Functions.gToast!=null && Functions.gToast.getView().isShown()) {
            finish();
            System.exit(0);
        }

        Functions.showToast(FirstActivity.this, R.string.back_again);
    }
}

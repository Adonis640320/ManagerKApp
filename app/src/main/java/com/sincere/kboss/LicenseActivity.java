package com.sincere.kboss;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.sincere.kboss.service.ServiceParams;

/**
 * Created by Michael on 2016.10.26.
 */
public class LicenseActivity extends ActivityTempl {
    TextView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);


        webView = (TextView) findViewById(R.id.webView);
        if (KbossApplication.g_sysparams != null) {
            //webView.loadUrl(ServiceParams.assetsBaseUrl + KbossApplication.g_sysparams.f_license);
            webView.setText(KbossApplication.g_sysparams.f_license);
        }
    }
    public void onConfirmPressed(View view)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

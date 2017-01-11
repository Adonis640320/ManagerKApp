package com.sincere.kboss;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.sincere.kboss.R;

/**
 * Created by SunMS on 11/28/2016.
 */

public class WebViewActivity extends Activity {
    WebView webView;
    Dialog dialog;
    String url;
    TextView tv_title;
    ImageView backbutton;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        tv_title = (TextView) findViewById(R.id.webview_title);

        tv_title.setText(title);
        backbutton = (ImageView) findViewById(R.id.bt_back);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (WebView)findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);

//modified by Adonis
        String content = getIntent().getStringExtra("content");

/*        if( url != "" ){
            webView.loadUrl(url);
        }
        else{*/
            if(content != null){
                content = "<html><body>" + content + "</body></html>";
            }
            else{
                content = "<html><body>" + "현시할 자료가 없습니다" + "</body></html>";
            }
            webView.loadData(content, "text/html; charset=utf-8", "UTF-8");
//        }

    }
}



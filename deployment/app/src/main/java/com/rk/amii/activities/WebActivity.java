package com.rk.amii.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get url from previous activity
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        System.out.println(url);

        // Get the web view and load the url
        WebView myWebView = new WebView(this);
        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        setContentView(myWebView);
        myWebView.loadUrl(url);
    }
}
package com.sezr.focuspro;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = new WebView(this);
        page.setWebViewClient(new WebViewClient());
        WebSettings settings = page.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        page.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        setContentView(page);
        String asset = "file:///android_asset/focus.html";
        page.loadUrl(asset);
    }

    @Override
    public void onBackPressed() {
        if (page != null && page.canGoBack()) {
            page.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

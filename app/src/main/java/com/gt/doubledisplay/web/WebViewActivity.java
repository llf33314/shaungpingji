package com.gt.doubledisplay.web;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.gt.doubledisplay.base.BaseActivity;

import static com.gt.doubledisplay.web.GTWebViewFrameLayout.PARAM_MODE;
import static com.gt.doubledisplay.web.GTWebViewFrameLayout.PARAM_URL;

/**
 * Created by wzb on 2017/8/1 0001.
 */

public class WebViewActivity extends BaseActivity {

    private final String TAG=WebViewActivity.class.getSimpleName();

    GTWebViewFrameLayout mGTWebViewFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGTWebViewFrameLayout =new GTWebViewFrameLayout(this,getIntent());
        setContentView(mGTWebViewFrameLayout);
        mGTWebViewFrameLayout.loadUrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGTWebViewFrameLayout.destroy();
       /* WebView mWebView= mGTWebViewFrameLayout.getWebView();
        if (mWebView != null) {
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
            mWebView = null;*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && mGTWebViewFrameLayout.getWebView().canGoBack()) {
            mGTWebViewFrameLayout.getWebView().goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}


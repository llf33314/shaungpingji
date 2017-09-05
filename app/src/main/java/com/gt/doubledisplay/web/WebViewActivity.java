package com.gt.doubledisplay.web;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.utils.RxBus;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * Created by wzb on 2017/8/1 0001.
 */

public class WebViewActivity extends BaseActivity {

    private final String TAG=WebViewActivity.class.getSimpleName();

    GTWebViewFrameLayout mGTWebViewFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url=getIntent().getStringExtra(GTWebViewFrameLayout.PARAM_URL);
        //String url= HttpConfig.DUOFRIEND_LOGIN;
        mGTWebViewFrameLayout =new GTWebViewFrameLayout(this,url);
        setContentView(mGTWebViewFrameLayout);
        showBtnBlu();
        mGTWebViewFrameLayout.loadUrl();


        Observable.interval(5, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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


}


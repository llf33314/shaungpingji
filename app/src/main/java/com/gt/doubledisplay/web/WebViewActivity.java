package com.gt.doubledisplay.web;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectService;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

import org.xwalk.core.XWalkNavigationHistory;

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
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         url=getIntent().getStringExtra(GTWebViewFrameLayout.PARAM_URL);
        if (TextUtils.isEmpty(url)){
            url= HttpConfig.DUOFRIEND_XCM;
        }
        mGTWebViewFrameLayout =new GTWebViewFrameLayout(this,url);
        setContentView(mGTWebViewFrameLayout);
        showBtnBlu();
        mGTWebViewFrameLayout.loadUrl();


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

    @Override
    protected void titleTenClick() {
        ToastUtil.getInstance().showToast(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
             if (keyCode == KeyEvent.KEYCODE_BACK) {
                 if (mGTWebViewFrameLayout.getWebView().getNavigationHistory().canGoBack()) {
                     mGTWebViewFrameLayout.getWebView().getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);//返回上一页面
                     } else {
                     ToastUtil.getInstance().showToast("已是最后一页");
                    }
                    return true;
               }
        return super.onKeyDown(keyCode, event);
    }



}


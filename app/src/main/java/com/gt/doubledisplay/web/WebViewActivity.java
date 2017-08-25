package com.gt.doubledisplay.web;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.http.socket.PrintSocketService;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectSerivce;
import com.gt.doubledisplay.update.UpdateManager;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;


/**
 * Created by wzb on 2017/8/1 0001.
 */

public class WebViewActivity extends BaseActivity {

    private final String TAG=WebViewActivity.class.getSimpleName();

    GTWebViewFrameLayout mGTWebViewFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // String url=getIntent().getStringExtra(GTWebViewFrameLayout.PARAM_URL);
        String url= HttpConfig.DUOFRIEND_LOGIN;
        mGTWebViewFrameLayout =new GTWebViewFrameLayout(this,url);
        setContentView(mGTWebViewFrameLayout);
        showBtnBlu();
        mGTWebViewFrameLayout.loadUrl();

        //连接socket  暂时这么写 如果是登录页面就不启动
        UpdateManager updateManager=new UpdateManager(this,"DoubleScreen");
        updateManager.requestUpdate();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && mGTWebViewFrameLayout.getWebView().canGoBack()) {
            mGTWebViewFrameLayout.getWebView().goBack();
        }else{
            ToastUtil.getInstance().showToast("已是最后一页");
        }
        return true;
    }
}


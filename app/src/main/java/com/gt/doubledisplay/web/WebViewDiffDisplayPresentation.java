package com.gt.doubledisplay.web;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.gt.doubledisplay.utils.commonutil.BarUtils;
import com.gt.doubledisplay.utils.commonutil.ScreenUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

/**
 * Created by wzb on 2017/8/7 0007.
 * 是否需要用单例  是否需要把webView封装成一个View
 */

public class WebViewDiffDisplayPresentation extends Presentation {

    GTWebViewFrameLayout mGTWebViewFrameLayout;

    private Intent intent;

    public WebViewDiffDisplayPresentation(Context outerContext, Display display,Intent intent) {
        super(outerContext, display);
        this.intent=intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(this.getContext() instanceof Activity)){
            this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mGTWebViewFrameLayout =new GTWebViewFrameLayout(this.getContext(),intent);
        setContentView(mGTWebViewFrameLayout);
        FrameLayout.LayoutParams lp= (FrameLayout.LayoutParams) mGTWebViewFrameLayout.getLayoutParams();
        lp.setMargins(0, BarUtils.getStatusBarHeight(this.getContext()),0,BarUtils.getNavigationBarHeight());
        mGTWebViewFrameLayout.setLayoutParams(lp);

        mGTWebViewFrameLayout.loadUrl();
    }

    public void show(){
        if (mGTWebViewFrameLayout !=null){//已经初始化了  第一次初始化在onCreate load
            mGTWebViewFrameLayout.loadUrl();
        }
        super.show();
    }

    @Override
    public void dismiss() {
        if (mGTWebViewFrameLayout!=null){
            mGTWebViewFrameLayout.destroy();
        }
        super.dismiss();
    }

    /*  DisplayManager  mDisplayManager;//屏幕管理类
    mDisplayManager = (DisplayManager)this.getContext().getSystemService(Context.DISPLAY_SERVICE);
    displays =mDisplayManager.getDisplays();
            if (displays.length<2){
        ToastUtil.getInstance().showToast("副屏未连接");
        return;
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mGTWebViewFrameLayout.getWebView().canGoBack()) {
            mGTWebViewFrameLayout.getWebView().goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}

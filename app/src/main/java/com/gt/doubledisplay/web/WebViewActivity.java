package com.gt.doubledisplay.web;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.bean.MainScreenMsgBean;
import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.http.rxjava.observable.SchedulerTransformer;
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

        RxBus.get().toObservable(MainScreenMsgBean.class)
                .compose(SchedulerTransformer.<MainScreenMsgBean>transformer())
                .subscribe(new Consumer<MainScreenMsgBean>() {
                    @Override
                    public void accept(@NonNull MainScreenMsgBean mainScreenMsgBean) throws Exception {
                          mGTWebViewFrameLayout.getWebView().loadUrl("javascript:"+mainScreenMsgBean.getMethonName()+"('"+ mainScreenMsgBean.getMsg()+"')");
                    }
                });
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


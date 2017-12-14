package com.gt.doubledisplay.web;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.DeviceBean;
import com.gt.doubledisplay.bean.AssistantScreenMsgBean;
import com.gt.doubledisplay.http.rxjava.observable.SchedulerTransformer;
import com.gt.doubledisplay.utils.RxBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by wzb on 2017/8/7 0007.
 * 是否需要用单例  是否需要把webView封装成一个View
 */

public class WebViewDiffDisplayPresentation extends Presentation {

    GTWebViewFrameLayout mGTWebViewFrameLayout;

    private String mUrl;

    public WebViewDiffDisplayPresentation(Context outerContext, Display display, String url) {
        super(outerContext, display);
        this.mUrl = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = this.getWindow();
        w.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        WindowManager.LayoutParams lp = w.getAttributes();

        mGTWebViewFrameLayout = new GTWebViewFrameLayout(this.getContext(), mUrl);

        ImageView iv = new ImageView(MyApplication.getAppContext());
        iv.setImageResource(R.drawable.bg);
        setContentView(iv);

        //显示轮播广告
        RxBus.get().toObservable(DeviceBean.class).compose(SchedulerTransformer.<DeviceBean>transformer()).subscribe(new Consumer<DeviceBean>() {
            @Override
            public void accept(@NonNull DeviceBean deviceBean) throws Exception {
                setContentView(mGTWebViewFrameLayout);
                mGTWebViewFrameLayout.loadUrl();

            }
        });

        RxBus.get().toObservable(AssistantScreenMsgBean.class)
                .compose(SchedulerTransformer.<AssistantScreenMsgBean>transformer())
                .subscribe(new Consumer<AssistantScreenMsgBean>() {
                    @Override
                    public void accept(@NonNull AssistantScreenMsgBean assistantScreenMsgBean) throws Exception {
                        //暂时这么写 后期小馋猫估计要改
                        if (TextUtils.isEmpty(assistantScreenMsgBean.getMethonName())) {
                            mGTWebViewFrameLayout.getWebView().loadUrl("javascript:getDataAndShow('" + assistantScreenMsgBean.getMsg() + "')");
                        } else {
                            mGTWebViewFrameLayout.getWebView().loadUrl("javascript:" + assistantScreenMsgBean.getMethonName() + "('" + assistantScreenMsgBean.getMsg() + "')");
                        }
                    }
                });
    }
}

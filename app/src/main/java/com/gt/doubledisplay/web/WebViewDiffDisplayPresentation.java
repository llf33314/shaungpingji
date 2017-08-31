package com.gt.doubledisplay.web;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.DeviceBean;
import com.gt.doubledisplay.bean.ScreenMsgBean;
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

    public WebViewDiffDisplayPresentation(Context outerContext, Display display,String url) {
        super(outerContext, display);
        this.mUrl=url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w=this.getWindow();
        w.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        WindowManager.LayoutParams lp=w.getAttributes();

        mGTWebViewFrameLayout =new GTWebViewFrameLayout(this.getContext(),mUrl);

        ImageView iv=new ImageView(MyApplication.getAppContext());
        iv.setImageResource(R.drawable.bg);
       // iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        setContentView(iv);
       // mGTWebViewFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        RxBus.get().toObservable(DeviceBean.class).compose(SchedulerTransformer.<DeviceBean>transformer()).subscribe(new Consumer<DeviceBean>() {
            @Override
            public void accept(@NonNull DeviceBean deviceBean) throws Exception {
                setContentView(mGTWebViewFrameLayout);
               /* FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mGTWebViewFrameLayout.getLayoutParams();
                lp.setMargins(0, BarUtils.getStatusBarHeight(MyApplication.getAppContext()), 0, BarUtils.getNavigationBarHeight());
                mGTWebViewFrameLayout.setLayoutParams(lp);*/
                mGTWebViewFrameLayout.loadUrl();

               // mGTWebViewFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        });

        RxBus.get().toObservable(ScreenMsgBean.class)
                .compose(SchedulerTransformer.<ScreenMsgBean>transformer())
                .subscribe(new Consumer<ScreenMsgBean>() {
                    @Override
                    public void accept(@NonNull ScreenMsgBean screenMsgBean) throws Exception {
                        mGTWebViewFrameLayout.getWebView().loadUrl("javascript:getDataAndShow('"+screenMsgBean.getMsg()+"')");
                        Log.d("WebView","javascript:getDataAndShow('"+screenMsgBean.getMsg()+"')");
                    }
                });



    }
    public void show(){
        /*if (mGTWebViewFrameLayout !=null){//已经初始化了  第一次初始化在onCreate load
            mGTWebViewFrameLayout.loadUrl();
        }*/
        super.show();
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
//        if (keyCode == KeyEvent.KEYCODE_BACK && mGTWebViewFrameLayout.getWebView().canGoBack()) {
//            mGTWebViewFrameLayout.getWebView().goBack();
//            return true;
//        }

        return super.onKeyDown(keyCode, event);
    }

}

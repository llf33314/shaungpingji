package com.gt.doubledisplay.web;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.http.socket.PrintSocketService;
import com.gt.doubledisplay.utils.commonutil.ConvertUtils;

/**
 * Created by wzb on 2017/8/9 0009.
 * 带进度条WebView
 */

public class GTWebViewFrameLayout extends FrameLayout {

    public final static String PARAM_URL = "param_url";

    HorizontalProgress mBar;

    private WebView mWebView;

    private Intent socketIntent;
    private static final String USERAGENT="Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";
    /**
     * 例子里面的框架需要这样写
     */
    private String mUrl;

    private Context mContext;
    public GTWebViewFrameLayout(Context context,String url) {
        super(context);
        this.mContext=context;

        mUrl = url;

        //先执行上面代码 下载一些资源一边初始化 webView
        mBar=new HorizontalProgress(context);
        mWebView =new WebView(context);

        FrameLayout.LayoutParams webViewLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        this.addView(mWebView,webViewLp);
        FrameLayout.LayoutParams barLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ConvertUtils.dp2px(3));
        this.addView(mBar,barLp);

        initWebView();

    }

    private GTWebViewFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initWebView(){

        //没有模式或者url为null
        if (TextUtils.isEmpty(mUrl)) {
            showNullTv();
            return;
        }

        WebSettings webSettings = mWebView.getSettings();

        // add java script interface
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        webSettings.setJavaScriptEnabled(true);
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");

        mWebView.addJavascriptInterface(new DuofenJSBridge(this.getContext(),mWebView),"doubleAndroid");

        // init webview settings
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString(USERAGENT);

        mWebView.setWebChromeClient(getWebChromeClient());
        //拦截url
        mWebView.setWebViewClient(getWebViewClient());

    }


    private void showNullTv(){
        FrameLayout.LayoutParams tvLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        TextView tv=new TextView(this.getContext());
        tv.setText("访问地址为空或未指定网页加载模式");
        tv.setTextSize(30);
        tv.setGravity(Gravity.CENTER);
        this.addView(tv,tvLp);
    }

    public WebViewClient getWebViewClient(){
        return new WebViewClient() {

            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                //拦截登录url跳转到登录页面
                /*if (HttpConfig.WEB_LOGIN.equals(url)){
                    Intent intent=new Intent(view.getContext(), LoginActivity.class);
                    view.getContext().startActivity(intent);
                    ((Activity)view.getContext()).finish();
                }*/

                //不启用socket
                /*if (url!=HttpConfig.DUOFRIEND_XCM&&socketIntent==null){//暂时这么写
                    socketIntent=new Intent(GTWebViewFrameLayout.this.getContext(),PrintSocketService.class);
                    GTWebViewFrameLayout.this.getContext().startService(socketIntent);
                }*/
                return null;
            }
        };
    }

    public WebChromeClient getWebChromeClient(){
        return new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mBar.setVisibility(View.VISIBLE);
                mBar.setProgress(newProgress);
                if (newProgress >= 100) {
                    mBar.setVisibility(View.GONE);
                    mBar.setProgress(0);
                }
                super.onProgressChanged(view, newProgress);
            }

           /* @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
*//*
                builder.setTitle("温馨提示")
                        .setMessage(message)
                        .setPositiveButton("确定", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();//  因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。*//*
                return true;// 不需要绑定按键事件
            }*/

            //=========多窗口的问题==========================================================
            /*@Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(view);
                resultMsg.sendToTarget();
                return true;
            }*/

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                //Log.d("GTWebViewFrameLayout",consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

        };
    }

    public void loadUrl(){
        mWebView.loadUrl(mUrl);
        //mWebView.loadUrl("file:///android_asset/dist/views/Slide/list.html");
    }

    public WebView getWebView(){
        return  mWebView;
    }
}

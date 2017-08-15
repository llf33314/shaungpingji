package com.gt.doubledisplay.web;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.sonic.SonicJavaScriptInterface;
import com.gt.doubledisplay.sonic.SonicRuntimeImpl;
import com.gt.doubledisplay.sonic.SonicSessionClientImpl;
import com.gt.doubledisplay.utils.commonutil.ConvertUtils;
import com.tencent.sonic.sdk.SonicCacheInterceptor;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicConstants;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.tencent.sonic.sdk.SonicSessionConnection;
import com.tencent.sonic.sdk.SonicSessionConnectionInterceptor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wzb on 2017/8/9 0009.
 * 带进度条WebView
 */

public class GTWebViewFrameLayout extends FrameLayout {


    public static final int MODE_DEFAULT = 0;

    public static final int MODE_SONIC = 1;

    public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;

    public static final int PERMISSION_REQUEST_CODE_STORAGE = 1;

    public static final String DEMO_URL = "http://mc.vip.qq.com/demo/indexv3";
    // private static final String DEMO_URL = "http://www.10tiao.com/html/156/201708/2650358532/1.html";

    public final static String PARAM_URL = "param_url";

    public final static String PARAM_MODE = "param_mode";

    private SonicSession sonicSession;

    HorizontalProgress mBar;

    private WebView mWebView;

    SonicSessionClientImpl sonicSessionClient = null;

    int mode;

    /**
     * 例子里面的框架需要这样写
     */
    private Intent intent=null;

    private String mUrl;

    private Context mContext;
    public GTWebViewFrameLayout(Context context,Intent intent) {
        super(context);
        this.mContext=context;
        this.intent=intent;
        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());

        mUrl = intent.getStringExtra(PARAM_URL);
        mode = intent.getIntExtra(PARAM_MODE, -1);

        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(MyApplication.getAppContext()), new SonicConfig.Builder().build());
        }

        // if it's sonic mode , startup sonic session at first time
        if (MODE_DEFAULT != mode) { // sonic mode
            SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();

            // if it's offline pkg mode, we need to intercept the session connection
            if (MODE_SONIC_WITH_OFFLINE_CACHE == mode) {
                sessionConfigBuilder.setCacheInterceptor(new SonicCacheInterceptor(null) {
                    @Override
                    public String getCacheData(SonicSession session) {
                        return null; // offline pkg does not need cache
                    }
                });

                sessionConfigBuilder.setConnectionIntercepter(new SonicSessionConnectionInterceptor() {
                    @Override
                    public SonicSessionConnection getConnection(SonicSession session, Intent intent) {
                        return new OfflinePkgSessionConnection(getContext(), session, intent);
                    }
                });
            }

            // create sonic session and run sonic flow
            sonicSession = SonicEngine.getInstance().createSession(mUrl, sessionConfigBuilder.build());
            if (null != sonicSession) {
                sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
            } else {
                // this only happen when a same sonic session is already running,
                // u can comment following codes to feedback as a default mode.
                throw new UnknownError("create session fail!");
            }
        }

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
        if (TextUtils.isEmpty(mUrl) || -1 == mode) {
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

        mWebView.addJavascriptInterface(new SonicJavaScriptInterface(this.getContext(),sonicSessionClient, intent), "sonic");//dfmb

        // init webview settings
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

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
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (sonicSession != null) {
                    sonicSession.getSessionClient().pageFinish(url);
                }
            }

            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (sonicSession != null) {
                    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                }
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

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("温馨提示")
                        .setMessage(message)
                        .setPositiveButton("确定", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();//  因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;// 不需要绑定按键事件
            }

            //=========多窗口的问题==========================================================
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(view);
                resultMsg.sendToTarget();
                return true;
            }

        };
    }

    public void loadUrl(){
        // webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(mWebView);
            sonicSessionClient.clientReady();
        } else { // default mode
            mWebView.loadUrl(mUrl);
        }
    }

    public WebView getWebView(){
        return  mWebView;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    private static class OfflinePkgSessionConnection extends SonicSessionConnection {

        private final WeakReference<Context> context;

        public OfflinePkgSessionConnection(Context context, SonicSession session, Intent intent) {
            super(session, intent);
            this.context = new WeakReference<Context>(context);
        }

        @Override
        protected int internalConnect() {
            Context ctx = context.get();
            if (null != ctx) {
                try {
                    InputStream offlineHtmlInputStream = ctx.getAssets().open("sonic-demo-index.html");
                    responseStream = new BufferedInputStream(offlineHtmlInputStream);
                    return SonicConstants.ERROR_CODE_SUCCESS;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return SonicConstants.ERROR_CODE_UNKNOWN;
        }

        @Override
        protected BufferedInputStream internalGetResponseStream() {
            return responseStream;
        }

        @Override
        public void disconnect() {
            if (null != responseStream) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getResponseCode() {
            return 200;
        }

        @Override
        public Map<String, List<String>> getResponseHeaderFields() {
            return new HashMap<>(0);
        }

        @Override
        public String getResponseHeaderField(String key) {
            return "";
        }
    }

    //释放 session
    public void destroy(){
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
    }
}

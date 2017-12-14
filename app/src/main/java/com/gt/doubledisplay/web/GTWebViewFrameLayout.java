package com.gt.doubledisplay.web;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.login.LoginActivity;
import com.gt.doubledisplay.utils.Logger;
import com.gt.doubledisplay.utils.commonutil.ConvertUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

/**
 * Created by wzb on 2017/8/9 0009.
 * 带进度条WebView
 */

public class GTWebViewFrameLayout extends FrameLayout {

    public final static String PARAM_URL = "param_url";

    HorizontalProgress mBar;

    private XWalkView mWebView;

    private static final String USERAGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";
    /**
     * 例子里面的框架需要这样写
     */
    private String mUrl;

    private Context mContext;

    public GTWebViewFrameLayout(Context context, String url) {
        super(context);
        this.mContext = context;

        mUrl = url;

        //先执行上面代码 下载一些资源一边初始化 webView
        mBar = new HorizontalProgress(context);
        mWebView = new XWalkView(context);

        FrameLayout.LayoutParams webViewLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        this.addView(mWebView, webViewLp);
        FrameLayout.LayoutParams barLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ConvertUtils.dp2px(3));
        this.addView(mBar, barLp);

        initWebView();

    }

    private GTWebViewFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initWebView() {

        //没有模式或者url为null
        if (TextUtils.isEmpty(mUrl)) {
            showNullTv();
            return;
        }

        XWalkSettings webSettings = mWebView.getSettings();

        // add java script interface
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        webSettings.setJavaScriptEnabled(true);
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");

        mWebView.addJavascriptInterface(new DuofenJSBridge(this.getContext(), mWebView), "doubleAndroid");

        // init webview settings
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString(USERAGENT);

        mWebView.setResourceClient(getWebChromeClient());
        mWebView.setUIClient(getUIClient());
        //拦截url

    }


    private void showNullTv() {
        FrameLayout.LayoutParams tvLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        TextView tv = new TextView(this.getContext());
        tv.setText("访问地址为空或未指定网页加载模式");
        tv.setTextSize(30);
        tv.setGravity(Gravity.CENTER);
        this.addView(tv, tvLp);
    }

    public XWalkUIClient getUIClient() {
        return new XWalkUIClient(mWebView) {
            @Override
            public boolean onJsAlert(XWalkView view, String url, String message, final XWalkJavascriptResult result) {
                showWebDialog(message, result);
                return true;
            }

            @Override
            public boolean onJsConfirm(XWalkView view, String url, String message, final XWalkJavascriptResult result) {
                showWebDialog(message, result);
                return true;
            }

            @Override
            public void onReceivedTitle(XWalkView view, String title) {
                try {
                    //副屏幕不走这里  set设置标题
                    BaseActivity activity = (BaseActivity) GTWebViewFrameLayout.this.getContext();
                    activity.setToolBarTitle(title);
                } catch (ClassCastException e) {
                    super.onReceivedTitle(view, title);
                }
            }
        };
    }

    /**
     * 副屏alert 时 会报错
     */
    private void showWebDialog(String message, final XWalkJavascriptResult result) {
        Context context = this.getContext();
        if (!(context instanceof Activity)) {
            //ToastUtil.getInstance().showToast(message,5000);
            Logger.d("showWebDialog", message);
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("温馨提示")
                    .setMessage(message)
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .show();
        }

    }

    public XWalkResourceClient getWebChromeClient() {
        return new XWalkResourceClient(mWebView) {

            @Override
            public void onProgressChanged(XWalkView view, int newProgress) {
                mBar.setVisibility(View.VISIBLE);
                mBar.setProgress(newProgress);
                if (newProgress >= 100) {
                    mBar.setVisibility(View.GONE);
                    mBar.setProgress(0);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public XWalkWebResourceResponse shouldInterceptLoadRequest(XWalkView view, XWalkWebResourceRequest request) {
                String url = request.getUrl().toString();
                if (HttpConfig.DUOFRIEND_XCM_LOGIN_OUT_URL.equals(url) || HttpConfig.DUOFRIEND_XCM_LOGIN_OUT_URL_2.equals(url)) {
                    Context context = mWebView.getContext();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
                return super.shouldInterceptLoadRequest(view, request);
            }

        };
    }

    public void loadUrl() {

        mWebView.loadUrl(mUrl);
    }

    public XWalkView getWebView() {
        return mWebView;
    }
}

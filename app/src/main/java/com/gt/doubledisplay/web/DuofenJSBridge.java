package com.gt.doubledisplay.web;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.gt.doubledisplay.utils.commonutil.ToastUtil;

/**
 * Created by Administrator on 2017/3/8.
 */

public class DuofenJSBridge {

    private static final String TAG = "DuofenJSBridge";

    private Context context; // 传进来一个context，便于访问各种资源

    // 构造器
    public DuofenJSBridge(Context context){
        this.context = context;
    }
    /**
     * 显示提示框，短时间
     * @param msg
     */
    @JavascriptInterface
    public void showToastShort(String msg){
        ToastUtil.getInstance().showToast(msg);
    }
    /**
     * 重新加载页面
     * @return
     */
    @JavascriptInterface
    public boolean webReload(){
        Log.d(TAG, "webBack");
        ((WebViewActivity) context).getWebView().reload();
        return true;
    }
}

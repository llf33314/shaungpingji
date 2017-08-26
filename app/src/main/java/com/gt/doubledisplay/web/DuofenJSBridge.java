package com.gt.doubledisplay.web;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectSerivce;


/**
 * Created by Administrator on 2017/3/8.
 */

public class DuofenJSBridge {

    private static final String TAG = "DuofenJSBridge";

    private Context context; // 传进来一个context，便于访问各种资源

    private WebView mWebView;

    // 构造器
    public DuofenJSBridge(Context context, WebView webView){
        this.context = context;
        this.mWebView=webView;
    }

    @JavascriptInterface
    public void reload() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mWebView.reload();
            }
        });
    }

    @JavascriptInterface
    public String[] getDeviceInfo() {
        return new String []{MyApplication.USER_ID,MyApplication.DEVICE_ID};
    }



  /**
     * web打印接口
    @return
   -1：未知错误
   0：SUCCESS,
   1：FAILED,
   2：TIMEOUT,
   3：INVALID_DEVICE_PARAMETERS,
   4：DEVICE_ALREADY_OPEN,
   5：INVALID_PORT_NUMBER,
   6：INVALID_IP_ADDRESS,
   7：INVALID_CALLBACK_OBJECT,
   8：BLUETOOTH_IS_NOT_SUPPORT,
   9：OPEN_BLUETOOTH,
   10：PORT_IS_NOT_OPEN,
   11：INVALID_BLUETOOTH_ADDRESS,
   12：PORT_IS_DISCONNECT;
   14：打印机正在连接
   15：打印机没初始化
     */
    /*@JavascriptInterface
    public int printPaper(String message) {
        return PrinterConnectSerivce.printReceiptClicked(message);
    }*/

}

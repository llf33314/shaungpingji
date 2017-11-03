package com.gt.doubledisplay.web;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.MainScreenMsgBean;
import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.AssistantScreenMsgBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;
import com.gt.doubledisplay.printer.extraposition.PrintESCOrTSCUtil;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.LogUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkView;


/**
 * Created by Administrator on 2017/3/8.
 */

public class DuofenJSBridge {

    private static final String TAG = "DuofenJSBridge";

    private Context context; // 传进来一个context，便于访问各种资源

    private XWalkView mWebView;

    private Gson gson=new Gson();

    public DuofenJSBridge(Context context, XWalkView webView){
        this.context = context;
        this.mWebView=webView;
    }

    @JavascriptInterface
    public void reload() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
               // mWebView.reload();
            }
        });
    }

    @JavascriptInterface
    public String getUserId(){
        return MyApplication.USER_ID;
    }
    @JavascriptInterface
    public String getDeviceId(){
        return MyApplication.DEVICE_ID;
    }

    /**
     * 主屏点击扫码支付 显示副副屏二维码
     * @param jsonData 里面字段能标记是否开还是关
     */
    @JavascriptInterface
    public void showPayQRCode(String jsonData){
        LogUtils.d(TAG,"showPayQRCode:"+jsonData);
        RxBus.get().post(new AssistantScreenMsgBean(jsonData));
    }



    @JavascriptInterface
    public void printStoreOrder(String jsonMsg){
        LogUtils.d(TAG,"printStoreOrder:"+jsonMsg);
        StoreOrderBean order=gson.fromJson(jsonMsg,StoreOrderBean.class);
        //打印不干胶
        PrintESCOrTSCUtil.printStoreXCM(order.getOrder_id(),order.getMenus());
        //打印到店
        MyApplication.getPrinter().printStoreOrder(order);
        }


    @JavascriptInterface
    public void printTakeOutOrder(String jsonMsg){
        LogUtils.d(TAG,"printTakeOutOrder:"+jsonMsg);
        TakeOutOrderBean order=gson.fromJson(jsonMsg,TakeOutOrderBean.class);

        //打印不干胶
        PrintESCOrTSCUtil.printTakeOutXCM(order.getOrder_id(),order.getMenus());
        //打印外卖
        MyApplication.getPrinter().printTakeOutOrder(order);
    }

    @JavascriptInterface
    public void openMoneyBox(){
        LogUtils.d(TAG,"openMoneyBox");
        MyApplication.getPrinter().openMoneyBox();
    }

    /**
     * 主屏调用副屏方法
     * @param methodName 方法名
     * @param msg        参数  可以为json
     */
    @JavascriptInterface
    public void callAssistantScreenMethod(String methodName,String msg){
        LogUtils.d(TAG,"methodName:"+methodName+"\nmsg:"+msg);
        RxBus.get().post(new AssistantScreenMsgBean(methodName,msg));
    }
    /**
     * 副屏调用主屏方法
     * @param methodName 方法名
     * @param msg        参数  可以为json
     */
    @JavascriptInterface
    public void callMainScreenMethod(String methodName,String msg){
        LogUtils.d(TAG,"methodName:"+methodName+"\nmsg:"+msg);
        RxBus.get().post(new MainScreenMsgBean(methodName,msg));
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

package com.gt.doubledisplay.web;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.ScreenMsgBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;
import com.gt.doubledisplay.printer.extraposition.PrintESCOrTSCUtil;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.weitoo.printer.MsPrinter;

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

    // 构造器
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
     * @param data 里面字段能标记是否开还是关
     */
    @JavascriptInterface
    public void showPayQRCode(String data){
        Log.d(TAG,"showPayQRCode:"+data);
        RxBus.get().post(new ScreenMsgBean(data));
    }

    /**
     * 打印不干胶
     * {
     "cashier": "",
     "consumption_money": 4.32,
     "fansCurrency_deduction": 1,
     "integral_deduction": 1,
     "member_deduction": 0,
     "menus": [
     {
     "commnt": "",
     "money": 3.3,
     "name": "叉烧包",
     "norms": "微辣  紫色",
     "num": 1
     },
     {
     "commnt": "",
     "menu_no": "1003",
     "money": 1,
     "name": "黄金叉烧",
     "norms": "微辣  小份  白色",
     "num": 1
     },
     {
     "commnt": "",
     "menu_no": "3362",
     "money": 0.02,
     "name": "洋葱炒肉片",
     "norms": "小份  中辣",
     "num": 2
     }
     ],
     "order_code": "DD1505292021472",
     "order_id": "A00001",
     "order_time": "2017-09-13 16:40:21",
     "pay_money": 4.32,
     "pay_time": "2017-09-13 16:40:33",
     "pay_type": 1,
     "print_type": 1,
     "result": 1,
     "shop_adress": "广东省深圳市南山区兰光科技园C座513",
     "shop_name": "谷通科技",
     "shop_phone": "0755-26609632"
     }
     */

     /*  @JavascriptInterface
    public void printTscOrder(String jsonMsg){
        StoreOrderBean order=gson.fromJson(jsonMsg,StoreOrderBean.class);

        //微兔打印内置打印机
        MsPrinter.printStoreOrder(order);
        //打印不干胶
        PrintESCOrTSCUtil.printStoreXCM(order.getOrder_id(),order.getMenus());
    }*/


    @JavascriptInterface
    public void printStoreOrder(String jsonMsg){
        StoreOrderBean order=gson.fromJson(jsonMsg,StoreOrderBean.class);
        //打印不干胶
        PrintESCOrTSCUtil.printStoreXCM(order.getOrder_id(),order.getMenus());
        //微兔打印内置打印机
        if (MyApplication.getPrinter()!=null){
            MyApplication.getPrinter().printStoreOrder(order);
        }else{
            ToastUtil.getInstance().showToast("设备非打印机设备",5000);
        }

    }

    @JavascriptInterface
    public void printTakeOutOrder(String jsonMsg){
        TakeOutOrderBean order=gson.fromJson(jsonMsg,TakeOutOrderBean.class);

        //打印不干胶
        PrintESCOrTSCUtil.printTakeOutXCM(order.getOrder_id(),order.getMenus());
        //微兔打印内置打印机
        if (MyApplication.getPrinter()!=null){
            MyApplication.getPrinter().printTakeOutOrder(order);
        }else{
            ToastUtil.getInstance().showToast("设备非打印机设备",5000);
        }


    }

    @JavascriptInterface
    public void openMoneyBox(){
        MsPrinter.openMoneyBox();
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

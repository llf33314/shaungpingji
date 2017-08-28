package com.gt.doubledisplay.http.socket;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.CallWebViewJS;
import com.gt.doubledisplay.bean.TscOrderPrintBean;
import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.http.retrofit.HttpCall;
import com.gt.doubledisplay.http.rxjava.observable.ResultTransformer;
import com.gt.doubledisplay.http.rxjava.observable.SchedulerTransformer;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectSerivce;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.PhoneUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.gt.doubledisplay.printer.extraposition.PrinterConnectSerivce.PRINTER_NOT_INTI;

/**
 * Description:
 * Created by jack-lin on 2017/7/24 0024.
 */

public class PrintSocketService extends Service {
    public static final String TAG = "PrintSocketService";
    private Socket mSocket;
    private PushBinder binder=new PushBinder();

    private Ringtone mRingtone;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initSocketHttp();
                connectSocket();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy");
            disSocket();
        super.onDestroy();
    }



    // socket连接
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           // String UUID = PhoneUtils.getIMEI();
          //  Log.d(TAG, "auth key : " + HttpConfig.SOCKET_ANDROID_AUTH_KEY + UUID);
           // mSocket.emit(HttpConfig.SOCKET_ANDROID_AUTH, HttpConfig.SOCKET_ANDROID_AUTH_KEY + UUID);
            mSocket.emit(HttpConfig.SOCKET_ANDROID_AUTH,MyApplication.USER_ID);
            Log.d(TAG, "onConnect:"+MyApplication.USER_ID);
        }
    };

    // 接收推送事件
    private Emitter.Listener socketEvent = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            String json=objects[0].toString();
            Log.d(TAG, "socketData:"+json);
            RxBus.get().post(new CallWebViewJS());
            try {
                JSONObject jsonResult=new JSONObject(json);

                String orderId=jsonResult.getString("message");
                HttpCall.getApiService()
                        .getPrintTscOrder(orderId)
                        .compose(SchedulerTransformer.<String>transformer())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull String s) {
                                print(s);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    public static void print(String s){
        JSONObject json= null;
        try {
            json = new JSONObject(s);
            String data=json.getString("data");
            Gson gson=new Gson();
            TscOrderPrintBean bean=gson.fromJson(data,TscOrderPrintBean.class);
            List<TscOrderPrintBean.Menus> menus=bean.getMenus();
            if (menus!=null&&menus.size()>0){
                for (TscOrderPrintBean.Menus m:menus){
                    String size=m.getNorms()+" x1";
                    for (int i=0;i<m.getNum();i++){

                    int res=PrinterConnectSerivce.printReceiptClicked(m.getMenu_no(),m.getName(),size,m.getCommnt());

                    if (res==PRINTER_NOT_INTI){//打印机未初始化
                        break;
                    }
                    if (res==-2){
                        ToastUtil.getInstance().showToast("打印机非不干胶类型，请连接正确打印机");
                        break;
                    }
                }
                }
            }

        } catch (JSONException e) {
            //后面处理
            e.printStackTrace();
        }
    }

    // socket disConnect
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "diconnected");
        }
    };

    // socket connectError
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "Error connecting");
        }
    };

    /**
     * 初始化Socket,Http的连接方式
     */
    public void initSocketHttp() {
        try {
            mSocket = IO.socket(HttpConfig.SOCKET_SERVER_URL); // 初始化Socket
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"initSocketHttp");

    }

    /**
     * 初始化socket，并建立连接
     */
    public void connectSocket() {
        if (mSocket==null){
            ToastUtil.getInstance().showToast("打印机连接服务器异常");
            return;
        }
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("chatevent", socketEvent);
        mSocket.connect();
    }
    /**
     * 关闭所有socket链接
     */
    public void disSocket() {
        mSocket.disconnect();
        mSocket.off("chatevent", socketEvent);
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
    }

    public class PushBinder extends Binder{

    }
}

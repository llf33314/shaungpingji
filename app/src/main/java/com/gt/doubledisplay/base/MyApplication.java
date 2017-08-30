package com.gt.doubledisplay.base;

import android.app.Application;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Process;
import android.view.Display;

import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.login.LoginActivity;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.gt.doubledisplay.web.WebViewDiffDisplayPresentation;
import com.orhanobut.hawk.Hawk;



/**
 * Created by wzb on 2017/7/11 0011.
 */

public class MyApplication extends Application {

    private static Context applicationContext;
    //登录接口获取
    public static String USER_ID="";
    //登录后获取user_id 请求接口获取  user_id即是 bus_id
    public static String DEVICE_ID="";

    WebViewDiffDisplayPresentation mp;
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext=getApplicationContext();
        Hawk.init(applicationContext).build();
        showScreen();
    }
    public static Context getAppContext(){
        if (applicationContext==null){ //后台挂着 applicationContext被回收
            appExit();
        }
        return  applicationContext;
    }

    private void showScreen(){
        DisplayManager mDisplayManager;//屏幕管理类
        mDisplayManager = (DisplayManager)this.getSystemService(Context.DISPLAY_SERVICE);
        //这里获取出来副屏是  1024*768  但是实际布局是1366*768
        Display[] displays =mDisplayManager.getDisplays();
        if (displays.length<2){
            ToastUtil.getInstance().showToast("副屏未连接");
            return;
        }
        if(mp==null){
            mp= new WebViewDiffDisplayPresentation(this,displays[1], HttpConfig.ADVERTISING_RUL);
        }
        mp.show();
    }
    public static void appExit(){
        //getAppContext().stopService(portIntent);
        Process.killProcess(Process.myPid());
    }
}

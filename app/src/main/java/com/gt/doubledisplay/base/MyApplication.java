package com.gt.doubledisplay.base;

import android.app.Application;
import android.content.Context;
import android.os.Process;

import com.gt.doubledisplay.login.LoginActivity;


/**
 * Created by wzb on 2017/7/11 0011.
 */

public class MyApplication extends Application {

    private static Context applicationContext;
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext=getApplicationContext();

    }
    public static Context getAppContext(){
        if (applicationContext==null){ //后台挂着 applicationContext被回收
            appExit();
        }
        return  applicationContext;
    }

    public static void appExit(){
        getAppContext().stopService(LoginActivity.portIntent);
        Process.killProcess(Process.myPid());
    }

}

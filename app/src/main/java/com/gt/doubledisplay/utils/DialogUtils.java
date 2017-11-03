package com.gt.doubledisplay.utils;

import android.view.View;
import android.view.WindowManager;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.MoreFunctionDialog;
import com.gt.doubledisplay.base.MyApplication;
import com.orhanobut.hawk.Hawk;

/**
 * Created by wzb on 2017/10/27 0027.
 */

public class DialogUtils {

    private static MoreFunctionDialog hintNotConnectDialog;
    private static boolean showNotConnectHint=true;
    private static boolean showNotConnectHintRm=true;
    private static final String SP_BGX_KEY="spBgjKey";

    /**
     * 不干胶提示专用
     */
    public static void showHintNotConnectDialog(){
        if (hintNotConnectDialog==null){
            hintNotConnectDialog=new MoreFunctionDialog(MyApplication.getAppContext(),"不干胶打印机未连接请连接后再打印\n若连接后仍不能使用请尝试重启设备", R.style.HttpRequestDialogStyle);
            hintNotConnectDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        if (!(Hawk.get(SP_BGX_KEY,0)==1)&&showNotConnectHint){
            hintNotConnectDialog.showButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Hawk.put(SP_BGX_KEY,1);
                    hintNotConnectDialog.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNotConnectHint=false;
                    hintNotConnectDialog.dismiss();
                }
            });
        }
    }
    /**
     * 热敏提示专用
     */
    public static void showHintNotConnectDialogRm(){
        final MoreFunctionDialog   hintNotConnectDialogRm=new MoreFunctionDialog(MyApplication.getAppContext(),"热敏打印机未连接请连接后再打印\n若连接后仍不能使用请尝试重启设备", R.style.HttpRequestDialogStyle);
        hintNotConnectDialogRm.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        if (showNotConnectHintRm){
            hintNotConnectDialogRm.showButton(null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNotConnectHintRm=false;
                    hintNotConnectDialogRm.dismiss();
                }
            });
        }
    }

    public static void showHint(String hintStr){
        final MoreFunctionDialog   hintNotConnectDialog=new MoreFunctionDialog(MyApplication.getAppContext(),hintStr, R.style.HttpRequestDialogStyle);
        hintNotConnectDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        hintNotConnectDialog.show();
    }
}

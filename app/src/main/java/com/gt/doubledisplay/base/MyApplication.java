package com.gt.doubledisplay.base;

import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.hardware.usb.UsbManager;
import android.os.Process;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.login.LoginActivity;
import com.gt.doubledisplay.printer.policy.PrinterPolicy;
import com.gt.doubledisplay.printer.policy.WeituPrinter;
import com.gt.doubledisplay.setting.SettingActivity;
import com.gt.doubledisplay.utils.commonutil.DeviceUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.gt.doubledisplay.web.WebViewDiffDisplayPresentation;
import com.orhanobut.hawk.Hawk;
import com.printsdk.usbsdk.UsbDriver;


/**
 * Created by wzb on 2017/7/11 0011.
 */

public class MyApplication extends Application {

    //微兔
    private static com.printsdk.usbsdk.UsbDriver mMsUsbDriver;
    public static final int SERIAL_BAUDRATE = com.printsdk.usbsdk.UsbDriver.BAUD115200;
    //不改他原来代码
    public static SharedPreferences mSharedPreferences;

    private static Context applicationContext;
    //登录接口获取
    public static String USER_ID="";
    //登录后获取user_id 请求接口获取  user_id即是 bus_id
    public static String DEVICE_ID="";

    WebViewDiffDisplayPresentation mp;

    private static PrinterPolicy printer;
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext=getApplicationContext();
        mSharedPreferences = this.getSharedPreferences("printer_box_test",Context.MODE_PRIVATE);
        Hawk.init(applicationContext).build();
        if ((getSettingCode()&(SettingActivity.DEVICE_SETTING_USE_PRINTER|SettingActivity.DEVICE_SETTING_USE_MONEY_BOX))!=0){
            getUsbDriverService();
        }
        initPrinter();
        showScreen();
    }

    private void initPrinter(){
        if ("NATIVE".equals(DeviceUtils.getModel())){//微兔设备
            printer=new WeituPrinter();
        }
    }

    public static PrinterPolicy getPrinter(){
        return printer;
    }


    public static Context getAppContext(){
        if (applicationContext==null){ //后台挂着 applicationContext被回收
            appExit();
        }
        return  applicationContext;
    }

    public static int getSettingCode(){
        return Integer.valueOf(Integer.toHexString(Hawk.get(SettingActivity.DEVICE_SETTING,SettingActivity.DEVICE_SETTING_USE_PRINTER|SettingActivity.DEVICE_SETTING_USE_MONEY_BOX)));
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

    // Get UsbDriver(UsbManager) service
    public static void getUsbDriverService() {
        UsbManager mUsbManager = (UsbManager) getAppContext().getSystemService(Context.USB_SERVICE);
        mMsUsbDriver = new com.printsdk.usbsdk.UsbDriver(
                (UsbManager)getAppContext(). getSystemService(Context.USB_SERVICE), getAppContext());

        PendingIntent permissionIntent = PendingIntent.getBroadcast(getAppContext(), 0,
                new Intent("com.usb.sample.USB_PERMISSION"), 0);
        mMsUsbDriver.setPermissionIntent(permissionIntent);
        // Broadcast listen for new devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        getAppContext().registerReceiver(mUsbReceiver, filter);

        // 打开设备
        if (mMsUsbDriver.openUsbDevice(SERIAL_BAUDRATE)) {
            /*Toast.makeText(this, "USB driver success",
                    Toast.LENGTH_SHORT).show();*/
            //SPGlobalUtils.put("printerVersion", "2");
            mSharedPreferences.edit().putString("printerVersion", "2");
        } else {
            ToastUtil.getInstance().showToast("内置打印机连接失败");
            //SPGlobalUtils.put("printerVersion", "1");
            mSharedPreferences.edit().putString("printerVersion", "1");
        }
    }

    /*
   *  BroadcastReceiver when insert/remove the device USB plug into/from a USB port
   *  创建一个广播接收器接收USB插拔信息：当插入USB插头插到一个USB端口，或从一个USB端口，移除装置的USB插头
   */
  public static  BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.e("printer", "ACTION_USB_DEVICE_ATTACHED");
                mMsUsbDriver.usbAttached(intent);
                mMsUsbDriver.openUsbDevice(SERIAL_BAUDRATE);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.e("printer", "ACTION_USB_DEVICE_DETACHED");
                mMsUsbDriver.closeUsbDevice();
                mMsUsbDriver.usbDetached(intent);
            }
        }
    };

    public static UsbDriver getMsUsbDriver() {
        return mMsUsbDriver;
    }
}

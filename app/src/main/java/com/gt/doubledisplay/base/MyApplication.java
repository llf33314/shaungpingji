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
import android.os.Build;
import android.os.Process;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.cashier.electricscale.manager.ElectricScaleManager;
import com.gt.doubledisplay.BuildConfig;
import com.gt.doubledisplay.bean.LoginBean;
import com.gt.doubledisplay.bean.LoginSignBean;
import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.login.LoginActivity;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectService;
import com.gt.doubledisplay.printer.policy.PrinterPolicy;
import com.gt.doubledisplay.printer.policy.WeituPrinter;
import com.gt.doubledisplay.printer.policy.ZeroSixFivePrinter;
import com.gt.doubledisplay.setting.SettingActivity;
import com.gt.doubledisplay.utils.Logger;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.DeviceUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.gt.doubledisplay.web.WebViewDiffDisplayPresentation;
import com.orhanobut.hawk.Hawk;
import com.printsdk.usbsdk.UsbDriver;
import com.tendcloud.tenddata.TCAgent;


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

    private static PrinterPolicy printerType;

    /**
     * 保存登录返回信息
     */
    private static LoginSignBean loginBean;

    public static Intent portIntent;
    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext=getApplicationContext();
        mSharedPreferences = this.getSharedPreferences("printer_box_test",Context.MODE_PRIVATE);
        Hawk.init(applicationContext).build();

        initLog();
        initPrinter();
        showScreen();
        startGpSerivce();
        initTalkingData();
        //initScale();
    }

    private void initLog(){
        //初始化log
        if (BuildConfig.DEBUG){
            Logger.LOG_LEVEL=5;
        }else{
            Logger.LOG_LEVEL=1;
        }
    }

    private void startGpSerivce(){
        portIntent = new Intent(this, PrinterConnectService.class);
        startService(portIntent);
    }
    /**
     * 初始化电子秤
     */
    private void initScale(){
        ElectricScaleManager.getInstance().findElectricScale(this);
        //ElectricScaleManager.getInstance().findElectricScaleReconnect(this);
       // ElectricScaleManager.getInstance().initElectricScaleLabelSize(this);
        ElectricScaleManager.getInstance().setOnBaseElectronicScaleOperationListen(new ElectricScaleManager.OnBaseElectronicScaleOperationListen() {
            @Override
            public void electronicScaleConnectSuccess() {
                Logger.d("Scale","electronicScaleConnectSuccess");
            }

            @Override
            public void electronicScaleConnectFailed() {
                Logger.d("Scale","electronicScaleConnectFailed");
            }

            @Override
            public void electronicScaleReconnectSuccess() {
                Logger.d("Scale","electronicScaleReconnectSuccess");

            }

            @Override
            public void electronicScaleReconnectFailed() {
                Logger.d("Scale","electronicScaleReconnectFailed");

            }

            @Override
            public void electronicScaleOperationSuccessful(String s) {
                Logger.d("Scale","electronicScaleOperationSuccessful");

            }

            @Override
            public void electronicScaleOperationFailed() {
                Logger.d("Scale","electronicScaleOperationFailed");

            }

            @Override
            public void electronicScaleMessageSuccessful() {
                Logger.d("Scale","electronicScaleMessageSuccessful");

            }
        });
    }

    private void initWeituPrinter(){
        if ((getSettingCode()&(SettingActivity.DEVICE_SETTING_USE_PRINTER|SettingActivity.DEVICE_SETTING_USE_MONEY_BOX))!=0){
            getUsbDriverService();
        }
    }

    private void initPrinter(){
        String deviceName=DeviceUtils.getModel();
        if ("NATIVE".equals(deviceName)){//微兔设备
            initWeituPrinter();
            printerType=new WeituPrinter();
        }else {

            printerType=new ZeroSixFivePrinter();
        }
    }

    public static PrinterPolicy getPrinter(){
        return printerType;
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
        //0921 微站调试用 因为除去了登录页面
       // RxBus.get().post(new DeviceBean());
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
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
               // Log.e("printer", "ACTION_USB_DEVICE_ATTACHED");
                mMsUsbDriver.usbAttached(intent);
                mMsUsbDriver.openUsbDevice(SERIAL_BAUDRATE);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
               // Log.e("printer", "ACTION_USB_DEVICE_DETACHED");
                mMsUsbDriver.closeUsbDevice();
                mMsUsbDriver.usbDetached(intent);
            }
        }
    };
    private void initTalkingData(){
        TCAgent.LOG_ON=true;
        TCAgent.init(getAppContext(),"2F64865688104270A2DB396E43626294", "DoubleScreen");
        TCAgent.setReportUncaughtExceptions(true);
    }
    public static LoginSignBean getLoginBean() {
        return loginBean;
    }

    public static void setLoginBean(LoginSignBean loginBean) {
        MyApplication.loginBean = loginBean;
    }

    public static UsbDriver getMsUsbDriver() {
        return mMsUsbDriver;
    }
}

package com.gt.doubledisplay.printer.extraposition;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.WindowManager;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.LabelCommand;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;
import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.MoreFunctionDialog;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.printer.extraposition.bluetooth.GPBluetoothUtil;
import com.gt.doubledisplay.printer.extraposition.bluetooth.OpenPrinterPortMsg;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

import org.apache.commons.lang.ArrayUtils;

import java.util.Vector;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by wzb on 2017/7/21 0021.
 * 蓝牙与USB能互相切换 主要通过广播操作
 * 一台蓝牙打印机只能跟一部设备配对
 */

public class PrinterConnectSerivce extends Service {

    private static final String ACTION_USB_PERMISSION ="com.android.example.USB_PERMISSION";

    public static final String CONNECTION_ACTION="action.connect.status";

    private static final int [] INTERNAL_USB={300017,1,46880,14370};

    public static final int PRINTER_CONNECTING=14;
    public static final int PRINTER_NOT_INTI=15;

    BluetoothAdapter mBluetoothAdapter ;

    Intent intentGpPrintService;

    public static GpService mGpService = null;

    private PrinterServiceConnection conn = null;
    private static int mPrinterIndex = 0;

    UsbManager mUsbManager ;

    private static UsbDevice mUsbDevice;

    private static MoreFunctionDialog hintNotConnectDialog;

    /**
     * 端口连接状态广播
     */
    private PortConnectionStateBroad mPortConnectionStateBroad;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter =BluetoothAdapter.getDefaultAdapter();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        //打开、关闭端口
        RxBus.get().toObservable(OpenPrinterPortMsg.class).subscribe(new Consumer<OpenPrinterPortMsg>() {
            @Override
            public void accept(@NonNull OpenPrinterPortMsg openPrinterPortMsg) throws Exception {

                switch (openPrinterPortMsg.getBluetoothState()){
                    case OpenPrinterPortMsg.CLOSE_PROT:
                            closeProt();
                        break;
                    case OpenPrinterPortMsg.OPEN_PROT:

                      //  if (mUsbDevice==null){//usb没有连接
                           if (!isConnceted()){//usb没有连接
                            BluetoothDevice d=openPrinterPortMsg.getBluetoothDevice();
                            openBluetoothProtFromDevice(d);
                        }
                        break;
                }
            }
        });

        //开启GP bindserivce
        connection();

        //注册蓝牙广播 操作打开关闭时候打印机逻辑
        registerBoothCloseBroadcast();

        //注册usb广播 拔出插入逻辑
        registerUsbBroad();

    }

    private void connection() {
        if (mGpService==null){
        conn = new PrinterServiceConnection();
        intentGpPrintService = new Intent(this, GpPrintService.class);
        bindService(intentGpPrintService, conn, Context.BIND_AUTO_CREATE);
        }
    }

    public void closeProt(){
        try {
            int state=mGpService.getPrinterConnectStatus(mPrinterIndex);
            if (state== GpDevice.STATE_CONNECTING||state== GpDevice.STATE_CONNECTED ){
                mGpService.closePort(mPrinterIndex);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void openBluetoothProtFromDevice(BluetoothDevice bluetoothDevice){
        try {
            int rel = mGpService.openPort(mPrinterIndex, PortParameters.BLUETOOTH ,bluetoothDevice.getAddress(),0);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            //ToastUtil.getInstance().showToast("result :" + String.valueOf(r));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打开端口连接
     * 优先选择蓝牙
     */
    public void openBluetoothOrUsbProt() {
        BluetoothDevice printDevice= GPBluetoothUtil.getConnectingBluetooth(mBluetoothAdapter);
        if (printDevice!=null){
            openBluetoothProtFromDevice(printDevice);
        }else if(isHasUsbDevice()){
            openUsbProt();
        }
    }

    //这个方法很关键  根据判断是否会去自动连接 蓝牙或者usb productId如果改变了会影响连接
    private boolean isHasUsbDevice(){
        mUsbDevice  = GPUsbUtil.getExtrapositionUsbDevice(mUsbManager);
       // ToastUtil.getInstance().showToast( mUsbDevice!=null?"有USB":"没USB");
        return  mUsbDevice!=null;
    }

    private void registerBoothCloseBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBluetoothReceiver, filter);
    }

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mGpService==null){
                return;
            }
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                try {
                    mGpService.closePort(mPrinterIndex);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else
            if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {

                try {
                    mGpService.closePort(mPrinterIndex);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
            if (mPortConnectionStateBroad==null){
                mPortConnectionStateBroad=new PortConnectionStateBroad();
                IntentFilter intentFilter=new IntentFilter(CONNECTION_ACTION);
                PrinterConnectSerivce.this.registerReceiver(mPortConnectionStateBroad,intentFilter);
            }
            //打开端口
            openBluetoothOrUsbProt();
        }
    }

    private static int openUsbProt( ){
        if (mUsbDevice==null){
            ToastUtil.getInstance().showToast("请连接USB打印机");
            return -1;
        }
        int rel = -1;
        try {
            int state=mGpService.getPrinterConnectStatus(mPrinterIndex);
            if (state== GpDevice.STATE_NONE ||state== GpDevice.STATE_LISTEN ||state==GpDevice.STATE_CONNECTING){
                rel = mGpService.openPort(mPrinterIndex, PortParameters.USB, mUsbDevice.getDeviceName(), 0);
                GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
             //  ToastUtil.getInstance().showToast("result :" + String.valueOf(r));
            }
            return rel;

        } catch (RemoteException e) {
            e.printStackTrace();
            return rel;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn); // unBindService
        }
        try {
            unregisterReceiver(mBluetoothReceiver);
        }catch (Exception e){

        }
        //关闭端口
        try {
            mGpService.closePort(mPrinterIndex);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (mPortConnectionStateBroad!=null){
            this.unregisterReceiver(mPortConnectionStateBroad);
        }

        this.unregisterReceiver(mUsbReceiver);

    }

    private static void showHintNotConnectDialog(){
        if (hintNotConnectDialog==null){
            //0824 客户急着要
            hintNotConnectDialog=new MoreFunctionDialog(MyApplication.getAppContext(),"不干胶打印机未连接请连接后再打印", R.style.HttpRequestDialogStyle);
            hintNotConnectDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        hintNotConnectDialog.show();
    }

    /**
     * 打印机是否连接 或者正在连接
     */
    private boolean isConnceted() {
        try {
            //连接外置打印机佳博时候  打开这个service 回去连接打印机
            if (/*mGpService.getPrinterConnectStatus(mPrinterIndex) == GpDevice.STATE_CONNECTING
                    ||*/ mGpService.getPrinterConnectStatus(mPrinterIndex) == GpDevice.STATE_CONNECTED) {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 打印不干胶调用的方法
     */
    public static int  printReceiptClicked(String number,String name,String size,String remark) {

        if (mGpService==null){
            showHintNotConnectDialog();
            return PRINTER_NOT_INTI;
        }

        try {//拔插的时候这个sdk有毒  要这么处理
            int state=mGpService.getPrinterConnectStatus(mPrinterIndex);
           // ToastUtil.getInstance().showToast("state："+state);
            if (state== GpDevice.STATE_CONNECTING){  //在065上有BUG  佳博本身的SDK问题
                ToastUtil.getInstance().showToast("打印机未连接或连接中");
                return PRINTER_CONNECTING;
            }
            if (state== GpDevice.STATE_NONE){
                //mGpService.closePort(mPrinterIndex);
                if (mUsbDevice!=null){
                    openUsbProt();
                }else{//蓝牙跟USB都没连接
                    showHintNotConnectDialog();
                }
            }
            //这里很关键   打印机类型是ESC 还是TSC     佳博有毒 同一台打印机获取出来的  有时候是ESC 有时候是TSC
            //暂时不支持   一般热敏打印机
            int type = mGpService.getPrinterCommandType(mPrinterIndex);
            /*if (type == GpCom.ESC_COMMAND) {
              //return sendESCReceipt("");
              return -2;
            }else if (type == GpCom.LABEL_COMMAND){ //TSC*/
                return sendLabelReceipt(number,name,size,remark);
            //}
        } catch (RemoteException e1) {
            e1.printStackTrace();
            ToastUtil.getInstance().showToast("打印机异常，请尝试重启打印机跟收银设备");
        }
        return -1;
    }


    private static int sendESCReceipt(String money) {
        EscCommand esc=PrintESCOrTSCUtil.getPrintEscCommand(money);

        Vector<Byte> datas = esc.getCommand(); // 发送数据

        return printEscAndTsc(datas,1);
    }


    private static int sendLabelReceipt(String number,String name,String size,String remark) {
        LabelCommand tsc=PrintESCOrTSCUtil.getTscCommand(number,name,size,remark);

        Vector<Byte> datas = tsc.getCommand(); // 发送数据

        return printEscAndTsc(datas,2);
    }

    /**
     * @param datas
     * @param type  1 ESC 2TSC
     * @return
     */
    private static int printEscAndTsc(Vector<Byte> datas,int type){
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs=-1;
        try {
            if (type==1){
                rs = mGpService.sendEscCommand(mPrinterIndex, sss);
            }else{
                rs=mGpService.sendLabelCommand(mPrinterIndex,sss);
            }

            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                // ToastUtil.getInstance().showToast(GpCom.getErrorText(r));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return rs;

    }

    /**
     * 佳博sdk端口监听广播
     */
    private class PortConnectionStateBroad extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
                    int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                    switch (type){
                        case GpDevice. STATE_NONE: //连接断开
                         //   ToastUtil.getInstance().showToast("打印机连接断开");
                            //蓝牙连接状态断开后判断是否有usb打印机 连接usb打印机
                            if (isHasUsbDevice()){
                                openUsbProt();
                            }
                            break;
                        case GpDevice. STATE_LISTEN : //监听状态
                            break;
                        case  GpDevice. STATE_CONNECTING : //正在连接
                            break;
                        case  GpDevice. STATE_CONNECTED : //已连接
                            //ToastUtil.getInstance().showToast("已连接设备");
                             break;
                        case  GpDevice. STATE_INVALID_PRINTER : //无效的打印机
                         //  ToastUtil.getInstance().showToast("无效的打印机");
                              break;
                        case  GpDevice. STATE_VALID_PRINTER : //有效的打印机
                            ToastUtil.getInstance().showToast("已连接打印机");
                              break;

                    }
                }
        }

    private void registerUsbBroad(){
        IntentFilter intentFilter=new IntentFilter();
        //intentFilter.addAction(ACTION_USB_PERMISSION);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        this.registerReceiver(mUsbReceiver,intentFilter);
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_USB_PERMISSION)) {
                //sdk这条广播会去连接  SB SDK
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbDevice != null) {//申请权限成功 打开端口
                                ToastUtil.getInstance().showToast("usb端口打开中...");
                        }else{
                            ToastUtil.getInstance().showToast("usb打印机连接失败");
                        }
                    } else {
                        ToastUtil.getInstance().showToast("usb打印机连接被拒绝");
                    }
                }
            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                // ToastUtil.getInstance().showToast("usb打印机断开...");
                mUsbDevice=null;
                //如果蓝牙已经连接 则打开蓝牙端口
                closeProt();
                BluetoothDevice device=GPBluetoothUtil.getConnectingBluetooth(mBluetoothAdapter);
                if (device!=null){
                    openBluetoothProtFromDevice(device);
                }

            }else if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
                    if (!isConnceted()){//如果打印机状态是蓝牙已经连接中 则什么都不干
                      //  ToastUtil.getInstance().showToast("usb打印机");
                        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        mUsbDevice=usbDevice;
                        openUsbProt();
                    }
            }
        }
    };
    }


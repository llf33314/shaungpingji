package com.gt.doubledisplay.printer.extraposition;

import android.app.Dialog;
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
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

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
import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.printer.extraposition.bluetooth.GPBluetoothUtil;
import com.gt.doubledisplay.printer.extraposition.bluetooth.OpenPrinterPortMsg;
import com.gt.doubledisplay.printer.policy.ZeroSixFivePrinter;
import com.gt.doubledisplay.utils.DialogUtils;
import com.gt.doubledisplay.utils.Logger;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.LogUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.orhanobut.hawk.Hawk;

import org.apache.commons.lang.ArrayUtils;

import java.util.List;
import java.util.Vector;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by wzb on 2017/7/21 0021.
 * 蓝牙与USB能互相切换 主要通过广播操作
 *
 * 连接打印机阻塞UI线程没办法修改，GP封装在里面
 *
 * 这个类是连接佳博打印机的  包括不干胶跟普通热敏打印机
 * 1028  热敏打印机只支持蓝牙  不干胶打印机只支持usb
 */

public class PrinterConnectService extends Service {

    private static final String ACTION_USB_PERMISSION ="com.android.example.USB_PERMISSION";

    public static final String CONNECTION_ACTION="action.connect.status";


    public static final int PRINTER_CONNECTING=14;
    public static final int PRINTER_NOT_INTI=15;

    BluetoothAdapter mBluetoothAdapter ;

    Intent intentGpPrintService;

    public static GpService mGpService = null;

    private PrinterServiceConnection conn = null;

    /**
     * 打印机标记
     * 0:不干胶打印机  只支持USB
     * 1：热敏打印机   只支持蓝牙
     */
    private static int [] PRINTER_INDEX={0,1};


    /**
     * 只支持俩台打印机  因为连接之前不知道是先插入不干胶还是热敏 用这个记录先连接的是什么类型
     * 或者每次打印时候就遍历这个数组类型去打印 但是佳博的sdk或者打印机类型会有误 所以最好别这样
     *
     */
    private static SparseArray<Integer> printerArray=new SparseArray<>();



    UsbManager mUsbManager ;

    /**
     * Usb设备
     */
    private static List<UsbDevice> mUsbDeviceList;


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

        //蓝牙连接、断开后   打开、关闭端口
        RxBus.get().toObservable(OpenPrinterPortMsg.class).subscribe(new Consumer<OpenPrinterPortMsg>() {
            @Override
            public void accept(@NonNull OpenPrinterPortMsg openPrinterPortMsg) throws Exception {

                switch (openPrinterPortMsg.getBluetoothState()){
                    case OpenPrinterPortMsg.CLOSE_PROT:
                            closeProt(PRINTER_INDEX[1]);
                        break;
                    case OpenPrinterPortMsg.OPEN_PROT:

                           //if (!isConnceted()){//usb没有连接
                            BluetoothDevice d=openPrinterPortMsg.getBluetoothDevice();
                            openBluetoothProtFromDevice(d);
                       // }
                        break;
                    default :
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

    public void closeProt(int printerIndex){
        try {
            int state=mGpService.getPrinterConnectStatus(printerIndex);
            if (state== GpDevice.STATE_CONNECTING||state== GpDevice.STATE_CONNECTED ){
                mGpService.closePort(printerIndex);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void openBluetoothProtFromDevice(BluetoothDevice bluetoothDevice){
        try {
            int rel = mGpService.openPort(PRINTER_INDEX[1], PortParameters.BLUETOOTH ,bluetoothDevice.getAddress(),0);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            Logger.d("openBluetooth",String.valueOf(r));
            // ToastUtil.getInstance().showToast("result :" + String.valueOf(r));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开端口连接
     * 优先选择蓝牙
     */
    public void openBluetoothOrUsbProt() {
        BluetoothDevice bluetoothDevice= GPBluetoothUtil.getConnectingBluetooth(mBluetoothAdapter);
        if (bluetoothDevice!=null){//有连接蓝牙
            openBluetoothProtFromDevice(bluetoothDevice);
        }
        if(isHasUsbDevice()){
            //0918 暂时只支持GP不干胶打印机
            openAllUsbProt();
        }
    }

    //这个方法很关键  根据判断是否会去自动连接 蓝牙或者usb productId如果改变了会影响连接
    private boolean isHasUsbDevice(){
        mUsbDeviceList  = GPUsbUtil.getGPExtrapositionUsbDevice(mUsbManager);
       // ToastUtil.getInstance().showToast( mUsbDevice!=null?"有USB":"没USB");
        return mUsbDeviceList.size()>0;
    }

    private void registerBoothCloseBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        registerReceiver(mBluetoothReceiver, filter);
    }

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mGpService==null){
                return;
            }
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                try {
                    mGpService.closePort(PRINTER_INDEX[1]);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                try {
                    int rel = mGpService.openPort(PRINTER_INDEX[1], PortParameters.BLUETOOTH ,bluetoothDevice.getAddress(),0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                try {
                    mGpService.closePort(PRINTER_INDEX[1]);
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
            //注册佳博打印机连接广播
            if (mPortConnectionStateBroad==null){
                mPortConnectionStateBroad=new PortConnectionStateBroad();
                IntentFilter intentFilter=new IntentFilter(CONNECTION_ACTION);
                PrinterConnectService.this.registerReceiver(mPortConnectionStateBroad,intentFilter);
            }
            //打开端口
            openBluetoothOrUsbProt();
        }
    }

    /**
     * 打开俩个类型打印机打印机
     * @return
     */
    private static int openAllUsbProt(){
        //十进制 前面俩位代表不干胶 后面代表热敏 为0代表都成功
        int result=-1;
        for (int i=0;i<mUsbDeviceList.size();i++){
            UsbDevice device=mUsbDeviceList.get(i);
            if (device!=null){
                int rel=openUsbProt(i,device.getDeviceName());
                if (i==0){
                    result=rel*100;
                }else{
                    if (result==-1){
                        result=rel;
                    }else{
                        result=result+rel;
                    }
                }
            }
        }
        return  result;
    }
    /**
     * 连接成功会通过广播接受做操作
     *
     * GP sdk有bug 没有初始化成功 打开usb端口会报null  所以在插入的时候要加个延迟？
     * @return
     * printTypeId  0 是不干胶打印机 1 是热敏打印机
     * mUsbDeviceList.get(printTypeId).getDeviceName()
     */
    private static int openUsbProt(int printTypeId,String deviceName){
        if (mUsbDeviceList.size()<1){
            ToastUtil.getInstance().showToast("请连接USB打印机");
            return -1;
        }
        int rel = -1;
        try {
            int state=mGpService.getPrinterConnectStatus(PRINTER_INDEX[printTypeId]);
            if (state== GpDevice.STATE_NONE ||state== GpDevice.STATE_LISTEN ||state==GpDevice.STATE_CONNECTING){
                rel = mGpService.openPort(PRINTER_INDEX[printTypeId], PortParameters.USB, deviceName, 0);
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
            mGpService.closePort(PRINTER_INDEX[0]);
            mGpService.closePort(PRINTER_INDEX[1]);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (mPortConnectionStateBroad!=null){
            this.unregisterReceiver(mPortConnectionStateBroad);
        }

        this.unregisterReceiver(mUsbReceiver);

    }


    /**
     * 打印机是否连接 或者正在连接
     * 1028俩个打印机后 这个貌似不用判断 待测试验证
     */
    @Deprecated
    private boolean isConnceted() {
        try {
            //连接外置打印机佳博时候  打开这个service 回去连接打印机
            if (/*mGpService.getPrinterConnectStatus(mPrinterIndex) == GpDevice.STATE_CONNECTING
                    ||*/ mGpService.getPrinterConnectStatus(PRINTER_INDEX[0]) == GpDevice.STATE_CONNECTED) {
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
            DialogUtils.showHintNotConnectDialog();
            return PRINTER_NOT_INTI;
        }

        try {//拔插的时候这个sdk有毒  要这么处理
            int state=mGpService.getPrinterConnectStatus(PRINTER_INDEX[0]);
           // ToastUtil.getInstance().showToast("state："+state);
            if (state== GpDevice.STATE_CONNECTING){  //在065上有BUG  佳博本身的SDK问题
                ToastUtil.getInstance().showToast("打印机未连接或连接中");
                return PRINTER_CONNECTING;
            }
            if (state== GpDevice.STATE_NONE){
                /*//mGpService.closePort(mPrinterIndex);
                if (mUsbDevice!=null){
                    openUsbProt();
                }else{//蓝牙跟USB都没连接
                    showHintNotConnectDialog();
                }*/
                DialogUtils.showHintNotConnectDialog();
                return state;
            }
            //暂时不支持   一般热敏打印机
            return sendLabelReceipt(number,name,size,remark);
        } catch (RemoteException e1) {
            e1.printStackTrace();
            ToastUtil.getInstance().showToast("打印机异常，请尝试重启打印机跟收银设备");
        }
        return -1;
    }



   /* public static int sendESCTest() {
        EscCommand esc=PrintESCOrTSCUtil.getPrintEscCommand();

        Vector<Byte> datas = esc.getCommand(); // 发送数据

        return printEscAndTsc(datas,1);
    }*/


    public static int printStoreOrTakeOutOrder(){

        //这里只有065设备才会进来
        if (MyApplication.getPrinter() instanceof ZeroSixFivePrinter){
            ZeroSixFivePrinter zeroSixFivePrinter= (ZeroSixFivePrinter) MyApplication.getPrinter();
            EscCommand esc=zeroSixFivePrinter.getEsc();
            Vector<Byte> datas = esc.getCommand(); // 发送数据

            return printEscAndTsc(datas,1);
        }else{
            ToastUtil.getInstance().showToast("打印机初始化有误，请重启设备后再尝试");
            return -1;
        }

    }


    private static int sendLabelReceipt(String number,String name,String size,String remark) {
        LabelCommand tsc=PrintESCOrTSCUtil.getTscCommand(number,name,size,remark);

        Vector<Byte> datas = tsc.getCommand(); // 发送数据

        return printEscAndTsc(datas,0);
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
                rs = mGpService.sendEscCommand(type, sss);
                if (rs==10||rs==12){//蓝牙没有配置||配置了没有连接成功
                    DialogUtils.showHintNotConnectDialogRm();
                }
            }else{
                rs=mGpService.sendLabelCommand(type,sss);
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
                     Logger.d("StateBroad",type+"");
                    switch (type){
                        //卧槽  关机插入usb 再开机佳博也发这条广播  神坑
                        case GpDevice. STATE_NONE: //连接断开
                            //蓝牙连接状态断开后判断是否有usb打印机 连接usb打印机
                            if (isHasUsbDevice()){  //这个可要可不要
                                if (mGpService==null){  //这么处理 概率性mGpService为null  因为神坑
                                    connection();
                                }else{
                                    openAllUsbProt();
                                }
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
                               //ToastUtil.getInstance().showToast("无效的打印机");
                              break;
                        case  GpDevice. STATE_VALID_PRINTER : //有效的打印机
                              ToastUtil.getInstance().showToast("外置打印机已连接");
                              break;
                        default:
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
            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {//断开USB
                // ToastUtil.getInstance().showToast("usb打印机断开...");
                //刷新list 并且关闭对应的usb端口
                mUsbDeviceList= GPUsbUtil.getGPExtrapositionUsbDevice(mUsbManager);

                for (int i=0;i<mUsbDeviceList.size();i++){
                    UsbDevice device=mUsbDeviceList.get(i);
                    if (device==null){
                        closeProt(PRINTER_INDEX[i]);
                    }
                }

                //如果蓝牙已经连接 则打开蓝牙端口
                BluetoothDevice device=GPBluetoothUtil.getConnectingBluetooth(mBluetoothAdapter);
                if (device!=null){
                    openBluetoothProtFromDevice(device);
                }

            }else if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){ //插入USB

                      //  ToastUtil.getInstance().showToast("usb打印机");
                        //打开刚插入usb的端口
                 int printTypeId=-1;
                 UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                 mUsbDeviceList= GPUsbUtil.getGPExtrapositionUsbDevice(mUsbManager);
                 for (int i=0;i<mUsbDeviceList.size();i++){
                     //只有热敏的时候这个会为null
                     if (mUsbDeviceList.get(i)==null){
                         continue;
                     }
                     if (mUsbDeviceList.get(i).getProductId()==usbDevice.getProductId()){
                         printTypeId=i;
                     }
                 }
                 if (printTypeId!=-1){
                     openUsbProt(printTypeId,usbDevice.getDeviceName());
                 }
            }
        }
    };
    }


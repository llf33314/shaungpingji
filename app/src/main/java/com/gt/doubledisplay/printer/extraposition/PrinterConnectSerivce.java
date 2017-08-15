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
import com.gt.doubledisplay.printer.extraposition.bluetooth.OpenPrinterPortMsg;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.StringUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by wzb on 2017/7/21 0021.
 * 蓝牙与USB能互相切换 主要通过广播操作
 * 一台蓝牙打印机只能跟一部设备配对
 */

public class PrinterConnectSerivce extends Service {

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    public static final String CONNECTION_ACTION="action.connect.status";

    private static final String [] INTERNAL_USB={"/dev/bus/usb/003/012","/dev/bus/usb/003/003","/dev/bus/usb/001/002","/dev/bus/usb/003/011"};

    public static final int PRINTER_CONNECTING=14;
    public static final int PRINTER_NOT_INTI=15;

    BluetoothAdapter mBluetoothAdapter ;

    Intent intentGpPrintService;


    public static GpService mGpService = null;

    private PrinterServiceConnection conn = null;
    private static int mPrinterIndex = 0;

    UsbManager mUsbManager ;

    private static UsbDevice mUsbDevice;

    private  static MoreFunctionDialog hintNotConnectDialog;

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

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
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

        BluetoothDevice printDevice=getConnectingBluetooth();
        if (printDevice!=null){
            openBluetoothProtFromDevice(printDevice);
        }else if(isHasUsbDevice()){
            openUsbProt();
        }
    }

    private boolean isHasUsbDevice(){
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice d=deviceIterator.next();
            //双屏内置有4个usb设备 排除这4个设备名称

            if (!StringUtils.useList(INTERNAL_USB,d.getDeviceName())){
                mUsbDevice  = d;
                break;
            }

        }
        return  mUsbDevice!=null;
    }

    private boolean isHasBluetoothDevice(){

        if (!mBluetoothAdapter.isEnabled()){
            return false;
        }

        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> devices=new ArrayList<BluetoothDevice>(bondedDevices);
        BluetoothDevice printDevice=null;
        for (BluetoothDevice b:devices){
            if (b.getType()==3){
                printDevice=b;
                break;
            }
        }
        return  printDevice!=null;
    }

    /**
     * 没有更好的方法获取当前蓝牙的连接状态了
     * @return
     */
    private BluetoothDevice getConnectingBluetooth(){

        if (!mBluetoothAdapter.isEnabled()){
            return null;
        }
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

        for(BluetoothDevice device : devices){
            //测试用  因为0D30显示的类型不是打印机？
            //过滤掉内置打印机
            if (/*device.getType()==3&&*/!"88:D1:31:71:2D:10".equals(device.getAddress())){
                return device;
            }
        }
        return null;

       /* Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到蓝牙状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(mBluetoothAdapter, (Object[]) null);

      //      if(state == BluetoothAdapter.STATE_CONNECTED){

                Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

                for(BluetoothDevice device : devices){

                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);

                    if(isConnected){
                        return device;
                    }

             //   }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;*/
    }

    private void registerBoothCloseBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBluetoothReceiver, filter);
    }

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                try {
                    mGpService.closePort(mPrinterIndex);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
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

    public boolean[] getConnectState() {
        boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            state[i] = false;
        }
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            try {
                if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
                    state[i] = true;
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return state;
    }

    /**
     * 打印机状态
     */
    public static String getPrinterStatus() {
        if (mGpService==null){
            return "打印机未连接";
        }
        String str = "";
        try {
            int status = mGpService.queryPrinterStatus(mPrinterIndex, 500);

            if (status == GpCom.STATE_NO_ERR) {
                str = "打印机正常";
            } else {
                str = "打印机 ";
                if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                    str += "脱机";
                }
                if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                    str += "缺纸";
                }
                if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                    str += "打印机开盖";
                }
                if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                    str += "打印机出错";
                }
                if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {//wzb 自己修改的 因为正常的时候也显示打印机超时
                    str = "正常";
                }
            }
           // ToastUtil.getInstance().showToast("打印机：" + mPrinterIndex + " 状态：" + str);
        } catch (RemoteException e1) {
            e1.printStackTrace();
            return "打印机状态未知错误";
        }
        return str;
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
            hintNotConnectDialog=new MoreFunctionDialog(MyApplication.getAppContext(),"打印机未连接请连接后再打印", R.style.HttpRequestDialogStyle);
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
     * 打印调用的方法
     */
    public static int  printReceiptClicked(String money) {

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
            //这里很关键   打印机类型是ESC 还是TSC
            int type = mGpService.getPrinterCommandType(mPrinterIndex);
            if (type == GpCom.ESC_COMMAND) {
              return sendESCReceipt(money);
            }else if (type == GpCom.LABEL_COMMAND){ //TSC
                return sendLabelReceipt();
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        return -1;
    }

    private static int sendESCReceipt(String money) {
        EscCommand esc = new EscCommand();
        esc.addPrintAndFeedLines((byte) 1);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("多粉餐厅（赛格）\n"); // 打印文字
        esc.addPrintAndLineFeed();

		// 打印文字 *//*
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText("--------------------------------\n");// 打印文字
        esc.addText("单号：12345678987445775\n"); // 打印文字
        esc.addText("--------------------------------\n");
        esc.addText("消费总额："+money+"\n");
        esc.addText("--------------------------------\n");
        esc.addText("抵扣方式：100粉币（-10.00）\n");
        esc.addText("实付金额：46.10\n");
        esc.addText("支付方式：微信支付\n");
        esc.addText("会员折扣：8.5\n");
        esc.addText("--------------------------------\n");
        esc.addText("开单时间：2017-07-21 14:23\n");
        esc.addText("收银员：多粉\n");
        esc.addText("--------------------------------\n");
        esc.addText("联系电话：0752-3851585\n");
        esc.addText("地址：惠州市惠城区赛格假日广场1007室\n");
        esc.addText("--------------------------------\n");
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("欢迎再次光临！\n"); // 打印文字
        esc.addPrintAndFeedLines((byte)5);


        Vector<Byte> datas = esc.getCommand(); // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs=-1;
        try {
            rs = mGpService.sendEscCommand(mPrinterIndex, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
               // ToastUtil.getInstance().showToast(GpCom.getErrorText(r));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rs;
    }

    private static int sendLabelReceipt() {
         int LEFT=15;
        String name="¥";

        //总共320*240
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(40, 30); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(3); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD , LabelCommand.MIRROR.NORMAL);// 设置打印方向
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区
        // 绘制简体中文

        if (name.length()<=6){//中文的小于等于6 则字体变大打印一行
            tsc.addText(LEFT,LEFT , LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                    "0279");
            tsc.addText(LEFT,75, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                    name);
            tsc.addText(LEFT, 140, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    "大杯，热 x1");
            tsc.addText(LEFT, 170, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    "备注：少冰，加糖，果粒");
        }else{

            String oneLine=name.substring(0,6);
            String twoLine=name.substring(6,12);//最多只能打12个字
            tsc.addText(LEFT,LEFT , LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                    "0279");
            tsc.addText(LEFT,70, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                    oneLine);
            tsc.addText(LEFT,125, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                    twoLine);
            tsc.addText(LEFT,180, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    "大杯，热 x1");
            tsc.addText(LEFT, 210, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    "备注：123456");
        }


        tsc.addPrint(1, 1); // 打印标签
        // tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
        //打印机打印密度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY10);
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand(); // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT );
        int rel=-1;
        try {
            rel = mGpService.sendLabelCommand(mPrinterIndex, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                //Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  rel;
    }



    /*private static int sendLabelReceipt() {
        //总共320*240
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(40, 30); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(3); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD , LabelCommand.MIRROR.NORMAL);// 设置打印方向
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区
        // 绘制简体中文
        tsc.addText(60, 12, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                "多粉餐饮");
        tsc.addReverse(45,5,320-95,63);


        tsc.addText(15,75, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                "海贼王连锁店");
        tsc.addText(10, 130, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "加糖拿铁 小杯");
        tsc.addText(10, 160, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "价格：15.00元");
        tsc.addText(10, 195, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "外卖：123456");

        //二维码
        tsc.addQRCode(190, 130, LabelCommand.EEC.LEVEL_L, 4, LabelCommand.ROTATION.ROTATION_0, " http://www.duofriend.com/");


        // 绘制图片
     *//*   Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.gprinter);
        tsc.addBitmap(20, 50, BITMAP_MODE.OVERWRITE, b.getWidth() * 2, b);
*//*

        // 绘制一维条码
       // tsc.add1DBarcode(20, 250, LabelCommand.BARCODETYPE.CODE128, 100, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "Gprinter");
        tsc.addPrint(1, 1); // 打印标签
       // tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
        //打印机打印密度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY10);
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand(); // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel=-1;
        try {
            rel = mGpService.sendLabelCommand(mPrinterIndex, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                //Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  rel;
    }*/

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

                             /*   int rel = 0;
                                try {
                                    rel = mGpService.openPort(mPrinterIndex, PortParameters.USB, usbDevice.getDeviceName(), 0);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
*/
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
                //如果蓝牙已经连接则打开蓝牙端口
                closeProt();
                BluetoothDevice device=getConnectingBluetooth();
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


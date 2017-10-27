package com.weitoo.printer;

import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;
import com.gt.doubledisplay.printer.policy.WeituPrinter;
import com.gt.doubledisplay.setting.SettingActivity;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.printsdk.usbsdk.UsbDriver;

/**
 * 操作微兔硬件相关的放在这里
 */
public class MsPrinter {

 /*   public static void printTicket() {
        //新打印机
        Log.d("wbl", "打印>>>新打印机");
        UsbDriver msUsbDriver = MyApplication.getMsUsbDriver();
        MsNormalPrintModel model = new MsNormalPrintModel();
        model.packPrintData1();
        msUsbDriver.write(model.getPrintData());
        //打印二维码无法复用，必须这样写，不然二维码总是有残缺！
        msUsbDriver.write(PrintCmd.SetAlignment(1));
        msUsbDriver.write(PrintCmd.PrintQrcode("http://www.weitoo.com/wap/", 22, 8, 1));
        msUsbDriver.write(PrintCmd.PrintFeedline(3));// 走纸换行
    }*/

    private static boolean checkCanPrintOrder(Object orderObject){
        //用户设置不适用内置打印机
        if (MyApplication.getMsUsbDriver()==null
                ||(MyApplication.getSettingCode()&SettingActivity.DEVICE_SETTING_USE_PRINTER)==0){//不使用内置打印机
            return false;
        }

        if (orderObject==null){
            ToastUtil.getInstance().showToast("无订单数据");
            return false;
        }
        int status=UsbPrinterUtil.getPrinterStatus();
        //打印机不正常
        if (status!=0){
            ToastUtil.getInstance().showToast(UsbPrinterUtil.getStatusMsg(status));
            return false;
        }
        return true;
    }

    public static void printStoreOrder(StoreOrderBean storeOrderBean){

        if (checkCanPrintOrder(storeOrderBean)){//检查是否可用打印
            UsbDriver msUsbDriver = MyApplication.getMsUsbDriver();
            MsTicketPrintModel model=MsTicketPrintModel.getInstance();
            //他们打印机有BUG 打二维码的时候要分开打
            model.printStoreOrder(msUsbDriver,storeOrderBean);
        }
    }

    public static void printTakeOutOrder(TakeOutOrderBean takeOutOrderBean){
        if (checkCanPrintOrder(takeOutOrderBean)){//检查是否可用打印
            UsbDriver msUsbDriver = MyApplication.getMsUsbDriver();
            MsTicketPrintModel model=MsTicketPrintModel.getInstance();
            //他们打印机有BUG 打二维码的时候要分开打
            model.printTakeOutOrder(msUsbDriver,takeOutOrderBean);
        }
    }



    public static void openMoneyBox(){
        //非微兔设备无前线操作

        UsbDriver msUsbDriver =MyApplication.getMsUsbDriver();
        if (msUsbDriver==null||(MyApplication.getSettingCode()&SettingActivity.DEVICE_SETTING_USE_MONEY_BOX)==0){//不使用钱箱
            return;
        }

        /**这一行作用是打开钱箱,电子锁指令1B,70,00,N1,N2  （其中N1、N2单位为MS，建议指令为1B 70 00 50 50）*/
        msUsbDriver.write(new byte[]{0x1B,0x70,0x00,0x50,0x50});
    }
}

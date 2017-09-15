package com.weitoo.printer;

import com.gt.doubledisplay.base.MyApplication;
import com.printsdk.cmd.PrintCmd;

/**
 * @描述 USB打印机工具类
 * @作者 wangweifeng
 * @时间 2017/7/5 0005
 */
public class UsbPrinterUtil {

    //0 打印机正常 、1 打印机未连接或未上电、2 打印机和调用库不匹配
    //3 打印头打开 、4 切刀未复位 、5 打印头过热 、6 黑标错误 、7 纸尽 、8 纸将尽
    // 检测打印机状态
    public static int getPrinterStatus() {
        int iRet = -1;

        byte[] bRead1 = new byte[1];
        byte[] bWrite1 = PrintCmd.GetStatus1();
        if(MyApplication.getMsUsbDriver().read(bRead1,bWrite1)>0)
        {
            iRet = PrintCmd.CheckStatus1(bRead1[0]);
        }

        if(iRet!=0)
            return iRet;

        byte[] bRead2 = new byte[1];
        byte[] bWrite2 = PrintCmd.GetStatus2();
        if(MyApplication.getMsUsbDriver().read(bRead2, bWrite2)>0)
        {
            iRet = PrintCmd.CheckStatus2(bRead2[0]);
        }

        if(iRet!=0)
            return iRet;

        byte[] bRead3 = new byte[1];
        byte[] bWrite3 = PrintCmd.GetStatus3();
        if(MyApplication.getMsUsbDriver().read(bRead3, bWrite3)>0)
        {
            iRet = PrintCmd.CheckStatus3(bRead3[0]);
        }

        if(iRet!=0)
            return iRet;

        byte[] bRead4 = new byte[1];
        byte[] bWrite4 = PrintCmd.GetStatus4();
        if(MyApplication.getMsUsbDriver().read(bRead4, bWrite4)>0)
        {
            iRet = PrintCmd.CheckStatus4(bRead4[0]);
        }
        return iRet;
    }

    //0 打印机正常 、1 打印机未连接或未上电、2 打印机和调用库不匹配
    //3 打印头打开 、4 切刀未复位 、5 打印头过热 、6 黑标错误 、7 纸尽 、8 纸将尽
    public static String getStatusMsg(int status){
        String msg = "";
        switch (status){
            case 0:
                msg = "内置打印机正常";
                break;
            case 1:
                msg = "内置打印机未连接或未上电";
                break;
            case 2:
                msg = "内置打印机和调用库不匹配";
                break;
            case 3:
                msg = "内置打印机打印头打开";
                break;
            case 4:
                msg = "内置打印机切刀未复位";
                break;
            case 5:
                msg = "内置打印机打印头过热";
                break;
            case 6:
                msg = "内置打印机黑标错误";
                break;
            case 7:
                msg = "内置打印机纸尽";
                break;
            case 8:
                msg = "内置打印机纸将尽";
                break;
            default:
                msg="内置打印机状态未知";
                break;
        }
        return msg;
    }
    //0 打印机正常 、1 打印机未连接或未上电、2 打印机和调用库不匹配
    //3 打印头打开 、4 切刀未复位 、5 打印头过热 、6 黑标错误 、7 纸尽 、8 纸将尽
    public static String getStatusMsg(){
        int status= getPrinterStatus();
        String msg = "";
        switch (status){
            case 0:
                msg = "打印机正常";
                break;
            case 1:
                msg = "打印机未连接或未上电";
                break;
            case 2:
                msg = "打印机和调用库不匹配";
                break;
            case 3:
                msg = "打印机打印头打开";
                break;
            case 4:
                msg = "打印机切刀未复位";
                break;
            case 5:
                msg = "打印机打印头过热";
                break;
            case 6:
                msg = "打印机黑标错误";
                break;
            case 7:
                msg = "打印机纸尽";
                break;
            case 8:
                msg = "打印机纸将尽";
                break;
            default:
                msg="打印机状态未知";
                break;
        }
        return msg;
    }
}

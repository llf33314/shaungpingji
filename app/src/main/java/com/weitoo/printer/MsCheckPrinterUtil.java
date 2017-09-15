package com.weitoo.printer;

import com.printsdk.cmd.PrintCmd;

/**
 * Created by Lunger on 2017/02/09.
 */
public class MsCheckPrinterUtil {

    // 检测打印机状态
    public static int getPrinterStatus(com.printsdk.usbsdk.UsbDriver mUsbDriver) {
        int iRet = -1;

        byte[] bRead1 = new byte[1];
        byte[] bWrite1 = PrintCmd.GetStatus1();
        if(mUsbDriver.read(bRead1,bWrite1)>0)
        {
            iRet = PrintCmd.CheckStatus1(bRead1[0]);
        }

        if(iRet!=0)
            return iRet;

        byte[] bRead2 = new byte[1];
        byte[] bWrite2 = PrintCmd.GetStatus2();
        if(mUsbDriver.read(bRead2,bWrite2)>0)
        {
            iRet = PrintCmd.CheckStatus2(bRead2[0]);
        }

        if(iRet!=0)
            return iRet;

        byte[] bRead3 = new byte[1];
        byte[] bWrite3 = PrintCmd.GetStatus3();
        if(mUsbDriver.read(bRead3,bWrite3)>0)
        {
            iRet = PrintCmd.CheckStatus3(bRead3[0]);
        }

        if(iRet!=0)
            return iRet;

        byte[] bRead4 = new byte[1];
        byte[] bWrite4 = PrintCmd.GetStatus4();
        if(mUsbDriver.read(bRead4,bWrite4)>0)
        {
            iRet = PrintCmd.CheckStatus4(bRead4[0]);
        }


        return iRet;
    }


}

package com.weitoo.printer;

import android.content.Context;

import com.gt.doubledisplay.base.MyApplication;
import com.weitoo.util.HexUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * Created by Awen on 2016/9/7.
 * 调用打印机打印小票
 */
@Deprecated
public class TicketPrinter {

    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;

    protected Context mContext;
    private static volatile TicketPrinter printer;

    private TicketPrinter(Context mContext) {
        this.mContext = mContext;
    }

    public static TicketPrinter getInstance() {
        if (printer == null) {
            synchronized (TicketPrinter.class) {
                if (printer == null) {
                    printer = new TicketPrinter(MyApplication.getAppContext());
                }
            }
        }
        return printer;
    }

    public void print(AbstractTicketModel model) {
        HexUtil.printHex(model.getPrintData());
        try {
            mSerialPort = new SerialPort(new File("/dev/ttyS3"), 115200, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mOutputStream.write(model.getPrintData());
            mOutputStream.close();
            mSerialPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

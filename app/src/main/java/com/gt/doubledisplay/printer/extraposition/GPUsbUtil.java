package com.gt.doubledisplay.printer.extraposition;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by wzb on 2017/8/16 0016.
 */

public class GPUsbUtil {
    public static UsbDevice getExtrapositionUsbDevice(UsbManager usbManager){
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice d=deviceIterator.next();
            //双屏内置有4个usb设备 排除这4个设备名称
            // int [] INTERNAL_USB={300017,1,46880,14370};
            int id=d.getProductId();
            if (id!=30017&&id!=1&&id!=46880&&id!=14370){
                return d;
            }
        }
        return null;
    }
}

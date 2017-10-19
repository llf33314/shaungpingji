package com.gt.doubledisplay.printer.extraposition;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.Log;

import com.gt.doubledisplay.utils.commonutil.DeviceUtils;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 判断多个USB 不是打印机
 * @see
 */
public class GPUsbUtil {

    private static final String TAG=GPUsbUtil.class.getSimpleName();

    //不干胶打印机 ID 1280   名称 GP-58
    private static final int GP_PRODEUCT_ID=1280;

    public static UsbDevice getGPExtrapositionUsbDevice(UsbManager usbManager){
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice d=deviceIterator.next();
            //065双屏内置有4个usb设备 排除这4个设备名称
            // int [] INTERNAL_USB={300017,1,46880,14370};
            int id=d.getProductId();
            Log.i(TAG,id+"");
           if (id==GP_PRODEUCT_ID){
               return d;
           }
        }
        return null;
    }

    /*//065设备 内置的USB接口  为了区分不干胶打印机
    private static final int [] DEVICE_065={1,30017,46880,14370};
    private static final int [] DEVICE_WEITU={8211};

    private static String deviceModel;

    public static UsbDevice getExtrapositionUsbDevice(UsbManager usbManager){
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice d=deviceIterator.next();
            //065双屏内置有4个usb设备 排除这4个设备名称
            // int [] INTERNAL_USB={300017,1,46880,14370};
            int id=d.getProductId();
            if (getDeviceModel().equals("065")){//065设备
                if (!isInArr(id,DEVICE_065)){//在这个数组里面
                    return d;
                }

            }else if(getDeviceModel().equals("NATIVE")){//微兔设备
                if (!isInArr(id,DEVICE_WEITU)){//不在这个数组里面
                    return d;
                }
            }

        }
        return null;
    }

    private static String getDeviceModel(){
        if (TextUtils.isEmpty(deviceModel)){
            deviceModel=DeviceUtils.getModel();
        }
        return deviceModel;
    }
    //判断元素是否在数组里面
    private static boolean isInArr(int tag,int [] arr){
        for(int i: arr){
            if(i==tag)
                return true;
        }
        return false;
    }*/
}

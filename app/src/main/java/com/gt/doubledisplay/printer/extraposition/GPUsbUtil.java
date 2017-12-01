package com.gt.doubledisplay.printer.extraposition;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.gt.doubledisplay.BuildConfig;
import com.gt.doubledisplay.utils.Logger;
import com.gt.doubledisplay.utils.commonutil.DeviceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 判断多个USB 不是打印机
 * @see
 */
public class GPUsbUtil {

    private static final String TAG=GPUsbUtil.class.getSimpleName();

    //不干胶打印机 ID 1280   名称 GP-58   其他打印机不知道是不是都是这样
    private static final int GP_PRODUCT_ID_BGJ=1280;
    private static final int GP_PRODUCT_ID_RM=512;
    //暂时不用这个 或者改成最少api是21
    private static final String GP_PRODUCT_NAME="GP-58";

   /* public static UsbDevice getGPExtrapositionUsbDevice(UsbManager usbManager){
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice d=deviceIterator.next();
            //065双屏内置有4个usb设备 排除这4个设备名称
            // int [] INTERNAL_USB={300017,1,46880,14370};
            int id=d.getProductId();
            Logger.i(TAG,id+"");
           if (id==GP_PRODUCT_ID_BGJ){
               return d;
           }
        }
        return null;
    }*/

    public static List<UsbDevice> getGPExtrapositionUsbDevice(UsbManager usbManager){
        List<UsbDevice> deviceList=new ArrayList<>();
        HashMap<String, UsbDevice> deviceMap = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceMap.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice d=deviceIterator.next();
            //065双屏内置有4个usb设备 排除这4个设备名称
            // int [] INTERNAL_USB={300017,1,46880,14370};
            int id=d.getProductId();
            if (GP_PRODUCT_ID_BGJ==id){
                if (deviceList.size()>0){
                    deviceList.set(0,d);
                }else{
                    deviceList.add(0,d);
                }

            }else if (GP_PRODUCT_ID_RM==id){
                if (deviceList.size()<1){
                    deviceList.add(0,null);
                }
                deviceList.add(1,d);
            }

           /* if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                String name=d.getProductName();
                if (GP_PRODUCT_NAME.equals(name)){
                    if (GP_PRODUCT_ID_BGJ==id){
                        deviceList.add(0,d);
                    }else if (GP_PRODUCT_ID_RM==id){
                        deviceList.add(1,d);
                    }
                }
            }else{

            }*/
        }
        return deviceList;
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

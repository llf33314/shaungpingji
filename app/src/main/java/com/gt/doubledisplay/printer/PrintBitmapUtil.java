package com.gt.doubledisplay.printer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.printer.internal.ESCUtil;

import java.io.IOException;
import java.util.Arrays;

/**
 * 暂时留着
 */

@Deprecated
public class PrintBitmapUtil {

    final static int Max_Dot=650;

    private static View internalView ;
    private static View extrapositionView;

    public synchronized static byte [] getInternalByte(){
            //裁剪
            byte[] breakPartial = ESCUtil.feedPaperCutPartial();
            byte [] bmp=null;
            try {
                bmp=getPrintBmp(0, getInternalBitmap());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ESCUtil.byteMerger2(bmp,breakPartial);
    }


    public synchronized  static Bitmap getInternalBitmap(){
        if (internalView==null){
            internalView=getPrintView(R.layout.print_internal_view);
        }
        //set view content

         return getBitmapFromView(internalView);
    }
    public synchronized static Bitmap getExtrapositionBitmap(){
        if (extrapositionView==null){
            extrapositionView=getPrintView(R.layout.print_extrposition);
        }
        //set view content

        return getBitmapFromView(extrapositionView);
    }

    public static View getPrintView(@LayoutRes int layoutid){
        LayoutInflater factory = LayoutInflater.from(MyApplication.getAppContext());
        return factory.inflate(R.layout.print_internal_view, null);
    }

    private static Bitmap getBitmapFromView(View view){
        view.setDrawingCacheEnabled(true);
        //调用下面这个方法非常重要，如果没有调用这个方法，得到的bitmap为null
        view.measure(View.MeasureSpec.makeMeasureSpec(650, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY));
        //这个方法也非常重要，设置布局的尺寸和位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //获得绘图缓存中的Bitmap
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }


    private static byte[] getPrintBmp(int startx, Bitmap bitmap) throws IOException {
        // byte[] start1 = { 0x0d,0x0a};
        byte[] start2 = { 0x1D, 0x76, 0x30, 0x30, 0x00, 0x00, 0x01, 0x00 };

        int width = bitmap.getWidth() + startx;
        int height = bitmap.getHeight();
        Bitmap.Config m =bitmap.getConfig();
        // 332  272  ARGB_8888
        if (width > Max_Dot)
            width = Max_Dot;
        int tmp = (width + 7) / 8;
        byte[] data = new byte[tmp];
        byte xL = (byte) (tmp % 256);
        byte xH = (byte) (tmp / 256);
        start2[4] = xL;
        start2[5] = xH;
        start2[6] = (byte) (height % 256);
        start2[7] = (byte) (height / 256);
        byte SendBuf[] = new byte[start2.length+data.length*height];
        Arrays.fill(SendBuf,(byte)0);
        System.arraycopy(start2,0,SendBuf,0, start2.length);
        //mOutputStream.write(start2);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {

        }
        //System.arraycopy(src,0,byteNumCrc,0,4);
        //System.arraycopy(b,0,SendBuf,0, count);


        for (int i = 0; i < height; i++) {

            for (int x = 0; x < tmp; x++)
                data[x] = 0;

            for (int x = startx; x < width; x++) {
                int pixel = bitmap.getPixel(x - startx, i);
                if (Color.red(pixel) == 0 || Color.green(pixel) == 0
                        || Color.blue(pixel) == 0) {
                    data[x / 8] += 128 >> (x % 8);// (byte) (128 >> (y % 8));
                }
            }
            //mOutputStream.write(data);
            System.arraycopy(data,0,SendBuf,(start2.length+data.length*i), data.length);
            //  try { Thread.sleep(50); } catch (InterruptedException e) { }
        }
        //mOutputStream.write(SendBuf);
        return SendBuf;
        //SendLongDataToUart(SendBuf,SendBuf.length);
    }
}

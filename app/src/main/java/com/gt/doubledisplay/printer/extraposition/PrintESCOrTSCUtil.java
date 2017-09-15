package com.gt.doubledisplay.printer.extraposition;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;
import com.gt.doubledisplay.bean.OrderPrintBean;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gt.doubledisplay.printer.extraposition.PrinterConnectSerivce.PRINTER_NOT_INTI;

/**
 * Created by wzb on 2017/8/16 0016.
 */

public class PrintESCOrTSCUtil {

    public static EscCommand getPrintEscCommand(String money){
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
        return esc;
    }

    public static LabelCommand getTscCommand(String number,String name,String size,String remark){
        int LEFT=15;

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
                    number==null?"":number);
            tsc.addText(LEFT,75, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                    name);
            tsc.addText(LEFT, 140, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    size);
            if (remark.length()>9){
                remark.substring(0,8);
            }
            tsc.addText(LEFT, 170, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    "备注："+remark);
        }else{

            String oneLine=name.substring(0,6);
            String twoLine;
            if (name.length(

            )>=12){
                twoLine=name.substring(6,12);//最多只能打12个字
            }else{
                twoLine=name.substring(6,name.length());//最多只能打12个字
            }
            tsc.addText(LEFT,LEFT , LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                    number==null?"":number);
            tsc.addText(LEFT,70, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                    oneLine);
            tsc.addText(LEFT,125, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                    twoLine);
            tsc.addText(LEFT,180, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    size);
            if (remark.length()>9){
                remark.substring(0,8);
            }
            tsc.addText(LEFT, 210, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    "备注："+remark);
        }




      /*  Bitmap b=view2Bitmap();
        tsc.addBitmap(0, 0, LabelCommand.BITMAP_MODE.OVERWRITE, b.getWidth(), b);*/
        //打印机打印密度

        tsc.addDensity(LabelCommand.DENSITY.DNESITY10);

        tsc.addPrint(1, 1); // 打印标签
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        return tsc;
    }

    public static void printXCM(String orderId,List<OrderPrintBean.MenusBean> menus){
        //JSONObject json= null;
           // json = new JSONObject(s);
           // String orderId=json.getString("order_id");
           // String jsonMenus=json.getString("menus");
           // Gson gson=new Gson();
            if (menus!=null&&menus.size()>0){
                for (OrderPrintBean.MenusBean m:menus){
                    String size=m.getNorms()+" x1";
                    for (int i=0;i<m.getNum();i++){

                       // int res=PrinterConnectSerivce.printReceiptClicked(m.getMenu_no(),m.getName(),size,m.getCommnt());
                        int res=PrinterConnectSerivce.printReceiptClicked(orderId,m.getName(),size,m.getCommnt());

                        if (res==PRINTER_NOT_INTI){//打印机未初始化
                            break;
                        }
                        if (res==-2){
                            ToastUtil.getInstance().showToast("打印机非不干胶类型，请连接正确打印机");
                            break;
                        }
                    }
                }
            }
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
            e.printStackTrace();
        }
        return  rel;
    }*/



}

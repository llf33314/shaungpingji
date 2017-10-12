package com.weitoo.printer;

import android.text.TextUtils;

import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;
import com.gt.doubledisplay.utils.commonutil.StringUtils;
import com.gt.doubledisplay.utils.commonutil.TimeUtils;
import com.printsdk.cmd.PrintCmd;
import com.printsdk.usbsdk.UsbDriver;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 打印数据关键类  通过PrintCmd  封装的方法拼接data  这里应该使用单例
 */
public  class MsTicketPrintModel {
    final StringBuilder data = new StringBuilder();
    public final String PRINTER_ENCODING = "GBK";
    //调整位置用
    public static final String SPCE="   ";
    protected MsTicketPrintHelper mTicketPrintHelper;

    private final int LINE_SPCE=40;

    private static MsTicketPrintModel instance;

    public static MsTicketPrintModel getInstance(){
        if (instance==null){
            synchronized (MsTicketPrintModel.class){
                instance=new MsTicketPrintModel();
            }
        }
        return instance;
    }

    private MsTicketPrintModel() {
        /*
        // 小票标题
        mUsbDriver.write(PrintCmd.SetBold(0));
        mUsbDriver.write(PrintCmd.SetAlignment(1));
        //mUsbDriver.write(PrintCmd.SetSizetext(1, 1)); 放大字体
        mUsbDriver.write(PrintCmd.PrintString("微兔便利店", 0));//第二个参数表示要不要换行0换行，1不换行 //mUsbDriver.write(PrintCmd.PrintString(title, 0));

        // 小票主要内容
        CleanPrinter();// 清理缓存，缺省模式
        mUsbDriver.write(PrintCmd.PrintFeedline(1)); // 打印走纸1行

        mUsbDriver.write(PrintCmd.SetAlignment(0));
        mUsbDriver.write(PrintCmd.PrintString(mTicketPrintHelper.getLineForPrint("订单号：", "0011707301141"), 1));

        SetFeedCutClean(cutter); // 走纸换行、切纸、清理缓存*/
        mTicketPrintHelper = MsTicketPrintHelper.getInstance();
        printFromCenter();
        //appendCenterData("伦哥专用");
        //CleanPrinter();
       // appendRunLine(1);

    }

    //public abstract void packPrintData(String orderNo, int payType, BigDecimal receiveCash, BigDecimal reducePrice,  byte[] extraData);

    public byte[] getPrintData() {
        if (data == null)
            return getCustomByte("无可打印数据");
        return getCustomByte(data.toString());
    }

    /**
     * 打印到店订单   由于微兔打印机BUG 需要将二维码分开打
     */
    public void printStoreOrder(UsbDriver msUsbDriver,StoreOrderBean storeOrderBean){
        CleanPrinter();
        msUsbDriver.write(getStoreOrderData1(storeOrderBean));
        printQRCode(msUsbDriver,storeOrderBean.getQrUrl(),18);
        msUsbDriver.write(getPrintOtherMsg(storeOrderBean.getShop_name(),storeOrderBean.getShop_phone(),storeOrderBean.getShop_adress()));
    }
    /**
     * 打印外卖订单
     */
    public void printTakeOutOrder(UsbDriver msUsbDriver,TakeOutOrderBean takeOutOrderBean){
        CleanPrinter();
        msUsbDriver.write(getTakeOutOrderData1(takeOutOrderBean));
        printQRCode(msUsbDriver,takeOutOrderBean.getQrUrl(),16);
        msUsbDriver.write(getPrintOtherMsg(takeOutOrderBean.getResName(),takeOutOrderBean.getResPhone(),takeOutOrderBean.getResAddress()));
    }

    public byte[] getStoreOrderData1(StoreOrderBean storeOrderBean){
        data.setLength(0);
        //appendData(PrintCmd.SetBold(1));
        appendData(PrintCmd.SetSizechar(1,1,0,12*24));
        appendCenterData("编号："+storeOrderBean.getOrder_id());
        //appendData(PrintCmd.SetBold(0));
        appendData(PrintCmd.SetSizechar(0,0,0,0));
        //汉字模式`
       // appendData(PrintCmd.SetReadZKmode(0));

        //行间距
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        appendData(PrintCmd.SetAlignment(0));
        appendDividingLine();
        appendLeftData(storeOrderBean.getShop_name());
        appendDividingLine();

        appendLeftData("下单时间："+storeOrderBean.getOrder_time());
        appendData(PrintCmd.SetLinespace(LINE_SPCE*2));
        appendLeftData("打印时间："+ TimeUtils.getNowString());
        appendStoreOrderMenu(storeOrderBean.getMenus());

        if (storeOrderBean.getMember_deduction()!=0){
            appendLeftRightData("会员折扣：","-"+String.valueOf(storeOrderBean.getMember_deduction()));
        }
        if (storeOrderBean.getYhq_deduction()!=0){
            appendLeftRightData("优惠券：","-"+String.valueOf(storeOrderBean.getYhq_deduction()));
        }

        if (storeOrderBean.getFansCurrency_deduction()!=0){
            appendLeftRightData("粉币：","-"+String.valueOf(storeOrderBean.getFansCurrency_deduction()));
        }

        if (storeOrderBean.getIntegral_deduction()!=0){
            appendLeftRightData("积分：","-"+String.valueOf(storeOrderBean.getIntegral_deduction()));
        }

          //appendLeftRightData("总计：",String.valueOf(storeOrderBean.getPay_money()));

        appendLeftBigNumberRightData("总计：",String.valueOf(storeOrderBean.getPay_money()));


        if (!TextUtils.isEmpty(storeOrderBean.getRemark())){
            appendLeftData("备注："+storeOrderBean.getRemark());
        }

        appendLeftData("支付方式："+storeOrderBean.getPayWay());
        appendLeftData(""); //换行

        appendDividingLine();

        //二维码不能这么添加打  他们打印机BUG
        //appendQRCode();
        return getCustomByte(data.toString());
    }

    private byte[] getTakeOutOrderData1(TakeOutOrderBean takeOutOrderBean){
        data.setLength(0);
        //汉字模式
       // appendData(PrintCmd.SetReadZKmode(0));

        appendData(PrintCmd.SetBold(1));
        appendData(PrintCmd.SetSizechinese(1,1,0,24*24));
        appendCenterData(takeOutOrderBean.getResName());
        appendData(PrintCmd.SetBold(0));
        appendData(PrintCmd.SetSizechinese(0,0,0,24*24));
        appendData(PrintCmd.SetAlignment(0));
        appendDividingLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        appendLeftData("流水号："+takeOutOrderBean.getOrder_no());

        appendLeftData("下单时间："+takeOutOrderBean.getOrder_time());
        appendData(PrintCmd.SetLinespace(LINE_SPCE*2));
        appendLeftData("打印时间："+ TimeUtils.getNowString());
        appendTakeOutOrderMenu(takeOutOrderBean.getMenus());

        if (takeOutOrderBean.getMember_deduction()!=0){
            appendLeftRightData("会员折扣：","-"+String.valueOf(takeOutOrderBean.getMember_deduction()));
        }

        if (takeOutOrderBean.getYhq_deduction()!=0){
            appendLeftRightData("优惠券：","-"+String.valueOf(takeOutOrderBean.getYhq_deduction()));
        }

        if (takeOutOrderBean.getFansCurrency_deduction()!=0){
            appendLeftRightData("粉币：","-"+String.valueOf(takeOutOrderBean.getFansCurrency_deduction()));
        }

        if (takeOutOrderBean.getIntegral_deduction()!=0){
            appendLeftRightData("积分：","-"+String.valueOf(takeOutOrderBean.getIntegral_deduction()));
        }

        appendLeftBigNumberRightData("总计：",String.valueOf(takeOutOrderBean.getConsumption_money()));

        if (!TextUtils.isEmpty(takeOutOrderBean.getRemark())){
            appendLeftData("备注："+takeOutOrderBean.getRemark());
        }
        appendLeftData("支付方式："+takeOutOrderBean.getPayWay());
        appendLeftData("配送地址："+takeOutOrderBean.getMenAddress());
        appendLeftData("联系人："+takeOutOrderBean.getMenName());
        appendLeftData("联系方式："+takeOutOrderBean.getMenPhone());

        appendDividingLine();

        //二维码不能这么添加打  他们打印机BUG
        //appendQRCode();
        return getCustomByte(data.toString());
    }


    /*private byte[] getTakeOutOrderData2(TakeOutOrderBean takeOutOrderBean){
        data.setLength(0);
        appendCenterData("扫描上方二维码查看订单详情");
        appendRunLine(1);
        appendLeftData("感谢您使用"+order.getShop_name()+"，订餐热线："+order.getShop_phone());
        appendLeftData("联系地址："+order.getShop_adress());

        appendRunLine(1);
        appendCenterData("技术支持·多粉 400-899-4522");
        SetFeedCutClean(0);

        return getCustomByte(data.toString());
    }*/

    /**
     * 打印二维码下部分信息  到店跟外卖都的都一样 统一调用这个方法
     */
    private byte[] getPrintOtherMsg(String shopName,String shopPhone,String shopAddress){
        data.setLength(0);
        appendCenterData("扫描上方二维码查看订单详情");
        appendRunLine(1);
        appendLeftData("感谢您使用"+shopName+"，订餐热线："+shopPhone);
        appendLeftData("联系地址："+shopAddress);

        appendRunLine(1);
        appendCenterData("技术支持·多粉 400-899-4522");
        SetFeedCutClean(0);

        return getCustomByte(data.toString());
    }
    private void appendTakeOutOrderMenu(List<TakeOutOrderBean.MenusBean> menus){
        //共几份
        int allNum=0;
        appendData(PrintCmd.SetLinespace(5));
        appendGoods("名称","单价","数量","金额");
        appendDividingLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        for (int i=0;i<menus.size();i++){
            TakeOutOrderBean.MenusBean m=menus.get(i);
            if (i==menus.size()-1){
                appendData(PrintCmd.SetLinespace(5));
            }
            appendGoods(StringUtils.wipeOffSymbol(m.getDet_food_name()),String.valueOf(m.getDet_food_price()),String.valueOf(m.getDet_food_num()),String.valueOf(m.getPayPrice()));
            allNum+=m.getDet_food_num() ;
        }

        appendData(PrintCmd.SetLinespace(5));
        appendDividingLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        appendLeftRightData("","共"+allNum+"份");
    }

    private void appendStoreOrderMenu(List<StoreOrderBean.MenusBean> menus){
        //共几份
        int allNum=0;
        appendData(PrintCmd.SetLinespace(5));
        appendGoods("名称","单价","数量","金额");
        appendDividingLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        for (int i=0;i<menus.size();i++){
            StoreOrderBean.MenusBean m=menus.get(i);
            if (i==menus.size()-1){
                appendData(PrintCmd.SetLinespace(5));
            }
            appendGoods(m.getName(),String.valueOf(m.getOriginal_price()),String.valueOf(m.getNum()),String.valueOf(m.getMoney()));
            allNum+=m.getNum() ;
        }

        appendData(PrintCmd.SetLinespace(5));
        appendDividingLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        appendLeftRightData("","共"+allNum+"份");
    }

    protected void appendRunLine(int lines) {
        appendData(PrintCmd.PrintFeedline(lines));
    }


    public void printQRCode(UsbDriver msUsbDriver,String url,int leftMagin){
        msUsbDriver.write(PrintCmd.SetAlignment(1));
        msUsbDriver.write(PrintCmd.PrintQrcode(url, leftMagin, 8, 1));
        msUsbDriver.write(PrintCmd.PrintFeedline(1));
    }

    public byte[] getCustomByte(String src) {
        try {
            return src.getBytes(PRINTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void appendData(byte[] bytes) {
        try {
            data.append(new String(bytes, "GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    protected void appendQRCode() {
        appendData(PrintCmd.SetClean());
        appendData(PrintCmd.SetAlignment(1));
        appendData(PrintCmd.PrintQrcode("https://www.duofriend.com", 25, 8, 1));
    }

    protected void appendQRCode(String url) {
        appendData(PrintCmd.SetClean());
        appendData(PrintCmd.SetAlignment(1));
        appendData(PrintCmd.PrintQrcode(url, 25, 8, 1));
    }


    // 清理缓存，缺省模式
    protected void CleanPrinter() {
        //	mUsbDriver.write(PrintCmd.SetClean());    // 清理缓存
        appendData(PrintCmd.SetBold(0));             // 粗体设置
        printFromLeft();                            // 对齐方式
        appendData(PrintCmd.SetSizetext(0, 0));     // 字符大小
    }

    // 走纸换行、切纸、清理缓存   wzb 没切纸
    protected void SetFeedCutClean(int iMode) {
        appendRunLine(12);                            // 走纸换行
        appendData(PrintCmd.PrintCutpaper(iMode));  // 切纸类型
        appendData(PrintCmd.SetClean());            // 清理缓存
    }

    protected void appendDividingLine() {
        appendData(PrintCmd.PrintString(mTicketPrintHelper.DIVIDING_LINE, 1));
    }

    protected void appendLeftRightData(String left, String right) {
        left=SPCE+left;
        appendData(PrintCmd.PrintString(mTicketPrintHelper.getLineForPrint(left, right), 1));
    }

    /**
     * wzb 右边数字变大
     */
    protected void appendLeftBigNumberRightData(String left, String right){
        left=SPCE+left;
        appendData(PrintCmd.PrintString(left,1));
        appendData(PrintCmd.SetSizechar(1,1,0,9*17));
        appendData(PrintCmd.PrintString(mTicketPrintHelper.getBigRightLine(left,right), 0));
       // appendData(PrintCmd.PrintString(right, 0));
        appendData(PrintCmd.SetSizechar(0,0,0,0));
    }

    protected void appendGoods(String goodsName, String unitPrice, String num, String sum) {
        goodsName=SPCE+goodsName;
        appendData(PrintCmd.PrintString(mTicketPrintHelper.getGoodsLine(goodsName, unitPrice, num, sum), 1));
    }

    protected void appendCenterData(String center) {
        appendData(PrintCmd.SetAlignment(1));
        appendData(PrintCmd.PrintString(center, 0));
    }

    protected void appendLeftData(String left) {
        left=SPCE+left;
        appendData(PrintCmd.SetAlignment(0));
        appendData(PrintCmd.PrintString(left, 0));
    }

    protected void printFromLeft() {
        appendData(PrintCmd.SetAlignment(0));
    }

    protected void printFromCenter() {
        appendData(PrintCmd.SetAlignment(1));
    }

    protected void printFromRight() {
        appendData(PrintCmd.SetAlignment(2));
    }

    String getDoublePointAfter2(BigDecimal src) {
        String result = "" + src.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        int index = result.indexOf(".");
        if (index < 0) {
            return result + ".00";
        } else if (index == result.length() - 2) {
            return result + "0";
        } else {
            return result;
        }

    }

    public static String int2String(Integer i) {
        if (i == null)
            return "0";
        return i.toString();
    }

    public static String getMoneyString(BigDecimal money) {
        if (money == null || money.toString().equals(""))
            return "0";
        return money.toString();
    }

    public static double getMoneyDouble(BigDecimal money) {
        if (getMoneyString(money).equals("0")) {
            return 0.00;
        }
        return money.doubleValue();
    }
}

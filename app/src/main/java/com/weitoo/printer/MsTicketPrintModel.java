package com.weitoo.printer;

import com.gt.doubledisplay.bean.OrderPrintBean;
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
    private final String SPCE="   ";
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

    protected void appendRunLine(int lines) {
        appendData(PrintCmd.PrintFeedline(lines));
    }

    //public abstract void packPrintData(String orderNo, int payType, BigDecimal receiveCash, BigDecimal reducePrice,  byte[] extraData);

    public byte[] getPrintData() {
        if (data == null)
            return getCustomByte("无可打印数据");
        return getCustomByte(data.toString());
    }


    public byte[] getOrderPrintData1(OrderPrintBean order){
        data.setLength(0);
        //汉字模式
        appendData(PrintCmd.SetReadZKmode(0));

        appendData(PrintCmd.SetBold(1));
        appendData(PrintCmd.SetSizechinese(1,1,0,24*24));
        appendCenterData(order.getShop_name());
        appendData(PrintCmd.SetBold(0));
        appendData(PrintCmd.SetSizechinese(0,0,0,24*24));
        appendDividingLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        appendLeftData("流水号："+order.getOrder_code());

        appendLeftData("下单时间："+order.getOrder_time());
        appendData(PrintCmd.SetLinespace(LINE_SPCE*2));
        appendLeftData("打印时间："+ TimeUtils.getNowString());
        appendOrderMenu(order.getMenus());
        appendLeftRightData("会员折扣：",String.valueOf(order.getMember_deduction()));
        appendLeftRightData("积分：",String.valueOf(order.getIntegral_deduction()));
        appendLeftRightData("粉币：",String.valueOf(order.getFansCurrency_deduction()));

        appendLeftRightData("总计：",String.valueOf(order.getPay_money()));

        appendLeftData("备注：");
        appendLeftData("支付方式：");
        appendLeftData("配送地址：");
        appendLeftData("联系人：");
        appendLeftData("联系方式：");

        appendDividingLine();

        //二维码不能这么添加打  他们打印机BUG
        //appendQRCode();
        //

        return getCustomByte(data.toString());
    }

    public byte[] getOrderPrintData2(OrderPrintBean order){
        data.setLength(0);
        appendCenterData("扫描上方二维码查看订单详情");
        appendRunLine(1);
        appendLeftData("感谢您使用"+order.getShop_name()+"，订餐热线："+order.getShop_phone());
        appendLeftData("联系地址："+order.getShop_adress());

        appendRunLine(1);
        appendCenterData("技术支持·多粉 400-899-4522");
        SetFeedCutClean(0);

        return getCustomByte(data.toString());
    }



    public void printOrder(UsbDriver msUsbDriver,OrderPrintBean order){
        CleanPrinter();
        msUsbDriver.write(getOrderPrintData1(order));
        printQRCode(msUsbDriver);
        msUsbDriver.write(getOrderPrintData2(order));
    }

    public void printQRCode(UsbDriver msUsbDriver){
        msUsbDriver.write(PrintCmd.SetAlignment(1));
        msUsbDriver.write(PrintCmd.PrintQrcode("https://www.duofriend.com", 25, 8, 1));
        msUsbDriver.write(PrintCmd.PrintFeedline(1));
    }

    private void appendOrderMenu(List<OrderPrintBean.MenusBean> menus){
        //共几份
        int allNum=0;
        appendData(PrintCmd.SetLinespace(5));
        appendGoods("名称","单价","数量","金额");
        appendDividingLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        for (int i=0;i<menus.size();i++){
            OrderPrintBean.MenusBean m=menus.get(i);
            if (i==menus.size()-1){
                appendData(PrintCmd.SetLinespace(5));
            }
            appendGoods(m.getName(),String.valueOf(m.getMoney()/m.getNum()),String.valueOf(m.getNum()),String.valueOf(m.getMoney()));
            allNum+=m.getNum() ;
        }

        appendData(PrintCmd.SetLinespace(5));
        appendDividingLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        appendLeftRightData("","共"+allNum+"份");
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

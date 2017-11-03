package com.gt.doubledisplay.printer.policy;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;
import com.gt.doubledisplay.printer.extraposition.PrintESCOrTSCUtil;
import com.gt.doubledisplay.setting.SettingActivity;
import com.gt.doubledisplay.utils.DialogUtils;
import com.gt.doubledisplay.utils.commonutil.StringUtils;
import com.gt.doubledisplay.utils.commonutil.TimeUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.printsdk.cmd.PrintCmd;
import com.printsdk.usbsdk.UsbDriver;
import com.weitoo.printer.MsPrinter;
import com.weitoo.printer.MsTicketPrintHelper;
import com.weitoo.printer.MsTicketPrintModel;
import com.weitoo.printer.UsbPrinterUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by wzb on 2017/9/19 0019.
 * 微兔打印机具体实现
 */

public class WeituPrinter implements PrinterPolicy {
    private Gson gson;
    private static UsbDriver msUsbDriver ;

    final StringBuilder data = new StringBuilder();
    public final String PRINTER_ENCODING = "GBK";
    //调整位置用
    public static final String SPCE="";
    protected MsTicketPrintHelper mTicketPrintHelper;

    /**
     * 行间距
     */
    private final int LINE_SPCE=40;

    private static WeituPrinter instance;

    public static WeituPrinter getInstance(){
        if (instance==null){
            synchronized (WeituPrinter.class){
                if (instance==null){
                    instance=new WeituPrinter();

                }
            }
        }
        return instance;
    }

    public WeituPrinter() {
        msUsbDriver = MyApplication.getMsUsbDriver();
        mTicketPrintHelper = MsTicketPrintHelper.getInstance();
        printFromCenter();
    }

    @Override
    public void printStoreOrder(StoreOrderBean storeOrderBean) {
       // MsPrinter.printStoreOrder(storeOrderBean);
        //由于微兔二维码要分开打 所以调用这个方法就已经打印了   其他erp要注意这个问题
        storeCompose(storeOrderBean);
    }

    @Override
    public void startCompose() {
        data.setLength(0);
        //设置行间距
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
    }


    @Override
    public void printTakeOutOrder(TakeOutOrderBean takeOutOrderBean) {
       // MsPrinter.printTakeOutOrder(takeOutOrderBean);
        //由于微兔二维码要分开打 所以调用这个方法就已经打印了   其他erp要注意这个问题.
        takeOutCompose(takeOutOrderBean);
    }

    @Override
    public void openMoneyBox() {
        MsPrinter.openMoneyBox();
    }

    @Override
    public void printTest() {
        printTestStoreOrder();
    }

    @Override
    public void addTitle(String title) {
        appendData(PrintCmd.SetBold(1));
        appendData(PrintCmd.SetSizechinese(1,1,0,24*24));
        appendCenterData(title);
        appendData(PrintCmd.SetBold(0));
        appendData(PrintCmd.SetSizechinese(0,0,0,24*24));
    }

    @Override
    public void divisionLine() {
        appendData(PrintCmd.PrintString(mTicketPrintHelper.DIVIDING_LINE, 1));
    }

    @Override
    public void lineFeed(int feed) {
        appendData(PrintCmd.PrintFeedline(feed));
    }

    @Override
    public void addLeftText(String leftStr) {
        addLeftAndRight(leftStr,"");
    }

    @Override
    public void addRightText(String rightStr) {
        addLeftAndRight("",rightStr);
    }

    @Override
    public void addLeftAndRight(String leftStr, String rightStr) {
        leftStr=SPCE+leftStr;
        appendData(PrintCmd.PrintString(mTicketPrintHelper.getLineForPrint(leftStr, rightStr), 1));
    }

    @Override
    public void addLeftAndBigRight(String leftStr, String rightStr) {
        leftStr=SPCE+leftStr;
        appendData(PrintCmd.PrintString(leftStr,1));
        appendData(PrintCmd.SetSizechar(1,1,0,9*17));
        appendData(PrintCmd.PrintString(mTicketPrintHelper.getBigRightLine(leftStr,rightStr), 0));
        // appendData(PrintCmd.PrintString(right, 0));
        appendData(PrintCmd.SetSizechar(0,0,0,0));
    }

    @Override
    public void addCenterText(String rightStr) {
        appendData(PrintCmd.SetAlignment(1));
        appendData(PrintCmd.PrintString(rightStr, 0));
    }

    @Override
    public void addStoreOrder(StoreOrderBean storeOrderBean) {
        List<StoreOrderBean.MenusBean> menus=storeOrderBean.getMenus();
        int allNum=0;
        appendData(PrintCmd.SetLinespace(5));
        appendGoods("名称","单价","数量","金额");
        divisionLine();
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
        divisionLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        addLeftAndRight("","共"+allNum+"份");

        if (storeOrderBean.getMember_deduction()!=0){
            addLeftAndRight("会员折扣：","-"+String.valueOf(storeOrderBean.getMember_deduction()));
        }
        if (storeOrderBean.getYhq_deduction()!=0){
            addLeftAndRight("优惠券：","-"+String.valueOf(storeOrderBean.getYhq_deduction()));
        }

        if (storeOrderBean.getFansCurrency_deduction()!=0){
            addLeftAndRight("粉币：","-"+String.valueOf(storeOrderBean.getFansCurrency_deduction()));
        }

        if (storeOrderBean.getIntegral_deduction()!=0){
            addLeftAndRight("积分：","-"+String.valueOf(storeOrderBean.getIntegral_deduction()));
        }

        //appendLeftRightData("总计：",String.valueOf(storeOrderBean.getPay_money()));

        addLeftAndBigRight("总计：",String.valueOf(storeOrderBean.getPay_money()));


        if (!TextUtils.isEmpty(storeOrderBean.getRemark())){
            addLeftText("备注："+storeOrderBean.getRemark());
        }

        addLeftText("支付方式："+storeOrderBean.getPayWay());
        addLeftText(""); //换行

        divisionLine();
    }

    @Override
    public void addTakeOutOrder(TakeOutOrderBean takeOutOrderBean) {
        List<TakeOutOrderBean.MenusBean> menus=takeOutOrderBean.getMenus();
        int allNum=0;
        appendData(PrintCmd.SetLinespace(5));
        appendGoods("名称","单价","数量","金额");
        divisionLine();
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
        divisionLine();
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
        addLeftAndRight("","共"+allNum+"份");

        if (takeOutOrderBean.getMember_deduction()!=0){
            addLeftAndRight("会员折扣：","-"+String.valueOf(takeOutOrderBean.getMember_deduction()));
        }

        if (takeOutOrderBean.getYhq_deduction()!=0){
            addLeftAndRight("优惠券：","-"+String.valueOf(takeOutOrderBean.getYhq_deduction()));
        }

        if (takeOutOrderBean.getFansCurrency_deduction()!=0){
            addLeftAndRight("粉币：","-"+String.valueOf(takeOutOrderBean.getFansCurrency_deduction()));
        }

        if (takeOutOrderBean.getIntegral_deduction()!=0){
            addLeftAndRight("积分：","-"+String.valueOf(takeOutOrderBean.getIntegral_deduction()));
        }

        addLeftAndBigRight("总计：",String.valueOf(takeOutOrderBean.getConsumption_money()));

        if (!TextUtils.isEmpty(takeOutOrderBean.getRemark())){
            addLeftText("备注："+takeOutOrderBean.getRemark());
        }
        addLeftText("支付方式："+takeOutOrderBean.getPayWay());
        addLeftText("配送地址："+takeOutOrderBean.getMenAddress());
        addLeftText("联系人："+takeOutOrderBean.getMenName());
        addLeftText("联系方式："+takeOutOrderBean.getMenPhone());

        divisionLine();

    }


    @Override
    public void addOrderTitleText(String name,String price, String number, String money) {
        appendGoods("名称","单价","数量","金额");
        lineFeed(1);
        appendData(PrintCmd.SetLinespace(LINE_SPCE));
    }

    @Override
    public void addOrderText(String name,String price, int number, double money) {
        appendGoods(name,price,String.valueOf(number),String.valueOf(money));
    }

    @Override
    public void addOtherBottomMsg(String qrUrl, String shopName, String shopPhone, String shopAddress) {
        data.setLength(0);

        printQRCode(msUsbDriver,qrUrl,18);

        msUsbDriver.write(getPrintOtherMsg(shopName,shopPhone,shopAddress));

    }

    @Override
    public void storeCompose(StoreOrderBean storeOrderBean) {
        startCompose();
        //检查是否可用打印
        if (checkCanPrintOrder(storeOrderBean)){
            data.setLength(0);

            appendData(PrintCmd.SetSizechar(1,1,0,12*24));
            appendCenterData("编号："+storeOrderBean.getOrder_id());
            appendData(PrintCmd.SetSizechar(0,0,0,0));

            divisionLine();
            addLeftText(storeOrderBean.getShop_name());
            divisionLine();

            addLeftText("下单时间："+storeOrderBean.getOrder_time());
            appendData(PrintCmd.SetLinespace(LINE_SPCE*2));
            addLeftText("打印时间："+ TimeUtils.getNowString());
            addStoreOrder(storeOrderBean);
            msUsbDriver.write(getCustomByte(data.toString()));
            addOtherBottomMsg(storeOrderBean.getQrUrl(),storeOrderBean.getShop_name(),storeOrderBean.getShop_phone(),storeOrderBean.getShop_adress());

        }
    }

    @Override
    public void takeOutCompose(TakeOutOrderBean takeOutOrderBean) {
        if (checkCanPrintOrder(takeOutOrderBean)){
            data.setLength(0);

            addTitle(takeOutOrderBean.getResName());
            divisionLine();
            appendData(PrintCmd.SetLinespace(LINE_SPCE));
            addLeftText("流水号："+takeOutOrderBean.getOrder_no());

            addLeftText("下单时间："+takeOutOrderBean.getOrder_time());
            appendData(PrintCmd.SetLinespace(LINE_SPCE*2));
            addLeftText("打印时间："+ TimeUtils.getNowString());
            addTakeOutOrder(takeOutOrderBean);
            msUsbDriver.write(getCustomByte(data.toString()));
            addOtherBottomMsg(takeOutOrderBean.getQrUrl(),takeOutOrderBean.getResName(),takeOutOrderBean.getResPhone(),takeOutOrderBean.getResAddress());

        }
    }

    @Override
    public void print() {

    }


    public void printTestStoreOrder(){
        if(gson==null){
                gson=new Gson();
        }
        StoreOrderBean order=gson.fromJson(PRINT_TEST_STRING,StoreOrderBean.class);
        //打印不干胶
        PrintESCOrTSCUtil.printStoreXCM(order.getOrder_id(),order.getMenus());
        //微兔打印内置打印机
        if (MyApplication.getPrinter()!=null){
            MyApplication.getPrinter().printStoreOrder(order);
        }else{
            ToastUtil.getInstance().showToast("设备非打印机设备",5000);
        }
    }
    protected void appendData(byte[] bytes) {
        try {
            data.append(new String(bytes, PRINTER_ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    protected void printFromCenter() {
        appendData(PrintCmd.SetAlignment(1));
    }
    protected void appendCenterData(String center) {
        appendData(PrintCmd.SetAlignment(1));
        appendData(PrintCmd.PrintString(center, 0));
    }
    protected void appendGoods(String goodsName, String unitPrice, String num, String sum) {
        goodsName=SPCE+goodsName;
        appendData(PrintCmd.PrintString(mTicketPrintHelper.getGoodsLine(goodsName, unitPrice, num, sum), 1));
    }

    /**
     * 打印二维码
     * @param msUsbDriver
     * @param url
     * @param leftMagin
     */
    private void printQRCode(UsbDriver msUsbDriver, String url, int leftMagin){
        msUsbDriver.write(PrintCmd.SetAlignment(1));
        msUsbDriver.write(PrintCmd.PrintQrcode(url, leftMagin, 8, 1));
        msUsbDriver.write(PrintCmd.PrintFeedline(1));
    }

    private byte[] getCustomByte(String src) {
        try {
            return src.getBytes(PRINTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 打印二维码下部分信息  到店跟外卖都的都一样 统一调用这个方法
     */
    private byte[] getPrintOtherMsg(String shopName,String shopPhone,String shopAddress){
        data.setLength(0);
        appendCenterData("扫描上方二维码查看订单详情");
        lineFeed(1);
        addLeftText("感谢您使用"+shopName+"，订餐热线："+shopPhone);
        addLeftText("联系地址："+shopAddress);

        lineFeed(1);
        appendCenterData("技术支持·多粉 400-899-4522");
        setFeedCutClean(0);

        return getCustomByte(data.toString());
    }

    /**
     * 检查打印机是否能正常工作
     * @param orderObject
     * @return
     */
    private static boolean checkCanPrintOrder(Object orderObject){
        //用户设置不使用内置打印机
        if (MyApplication.getMsUsbDriver()==null
                ||(MyApplication.getSettingCode()& SettingActivity.DEVICE_SETTING_USE_PRINTER)==0){//不使用内置打印机
            return false;
        }

        if (orderObject==null){
            ToastUtil.getInstance().showToast("无订单数据");
            return false;
        }
        int status= UsbPrinterUtil.getPrinterStatus();
        //打印机不正常
        if (status!=0){
            //与佳博打印机一起使用会互相影响
            if (status==1){
                DialogUtils.showHint("打印机未上电或连接断开，请确认正确连接后重启应用/设备");
            }
            ToastUtil.getInstance().showToast(UsbPrinterUtil.getStatusMsg(status));
            return false;
        }
        return true;
    }

    // 走纸换行、切纸、清理缓存   wzb 没切纸
    protected void setFeedCutClean(int iMode) {
        lineFeed(12);                            // 走纸换行
        appendData(PrintCmd.PrintCutpaper(iMode));  // 切纸类型
        appendData(PrintCmd.SetClean());            // 清理缓存
    }
}

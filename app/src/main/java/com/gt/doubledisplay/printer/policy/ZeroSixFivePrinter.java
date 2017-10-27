package com.gt.doubledisplay.printer.policy;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.gprinter.command.EscCommand;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;
import com.gt.doubledisplay.printer.extraposition.PrintESCOrTSCUtil;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectService;
import com.gt.doubledisplay.utils.commonutil.StringUtils;
import com.gt.doubledisplay.utils.commonutil.TimeUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

import java.util.List;

/**
 * Created by wzb on 2017/9/25 0025.
 */

public class ZeroSixFivePrinter implements PrinterPolicy {

    /**
     * 打印机最大宽度字符
     */
    public static final int PRINTER_WIDTH=32;
    private Gson gson;
    private EscCommand esc;

    @Override
    public void startCompose() {
        esc=new EscCommand();
    }

    @Override
    public void printStoreOrder(StoreOrderBean storeOrderBean) {
        storeCompose(storeOrderBean);
        PrinterConnectService.printStoreOrTakeOutOrder();
    }

    @Override
    public void printTakeOutOrder(TakeOutOrderBean takeOutOrderBean) {
        takeOutCompose(takeOutOrderBean);
        PrinterConnectService.printStoreOrTakeOutOrder();
    }

    @Override
    public void openMoneyBox() {

    }

    @Override
    public void printTest() {
        printTestStoreOrder();
    }



    @Override
    public void addTitle(String title) {
        esc.addPrintAndFeedLines((byte) 1);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText(title);
        esc.addPrintAndLineFeed();
        // 打印文字 *//*
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    }

    @Override
    public void divisionLine() {
        esc.addText("…………………………………………\n");
    }

    @Override
    public void lineFeed(int feed) {
        esc.addPrintAndFeedLines((byte) feed);
    }

    @Override
    public void addLeftText(String leftStr) {
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText(leftStr+"\n");
    }

    @Override
    public void addRightText(String rightStr) {
        addLeftAndBigRight("",rightStr);
    }

    @Override
    public void addLeftAndRight(String leftStr, String rightStr) {
        addLeftAndRight(leftStr,rightStr,false);
    }

    @Override
    public void addLeftAndBigRight(String leftStr, String rightStr) {
        addLeftAndRight(leftStr,rightStr,true);
    }

    @Override
    public void addStoreOrder(StoreOrderBean storeOrderBean) {
        List<StoreOrderBean.MenusBean> menus=storeOrderBean.getMenus();
        int allNum=0;
        addOrderTitleText("名称","数量","金额");
        esc.addText("…………………………………………\n");

        if (menus==null||menus.size()==0){
            return;
        }

        for (int i=0;i<menus.size();i++){
            StoreOrderBean.MenusBean m=menus.get(i);
            addOrderText(m.getName(),m.getNum(),m.getMoney());
            allNum+=m.getNum() ;
            if (i==menus.size()-1){
                esc.addText("…………………………………………\n");
            }
        }
        esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
        esc.addText("共"+allNum+"份\n");


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
        addLeftAndBigRight("总计",storeOrderBean.getConsumption_money()+"\n");
    }

    @Override
    public void addTakeOutOrder(TakeOutOrderBean takeOutOrderBean) {
        List<TakeOutOrderBean.MenusBean> menus =takeOutOrderBean.getMenus();
        int allNum=0;
        addOrderTitleText("名称","数量","金额");
        esc.addText("…………………………………………\n");

        if (menus==null||menus.size()==0){
            return;
        }

        for (int i=0;i<menus.size();i++){
            TakeOutOrderBean.MenusBean m=menus.get(i);
            addOrderText(StringUtils.wipeOffSymbol(m.getDet_food_name()),m.getDet_food_num(),m.getPayPrice());
            allNum+=m.getDet_food_num() ;
            if (i==menus.size()-1){
                esc.addText("…………………………………………\n");
            }
        }
        esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
        esc.addText("共"+allNum+"份\n");


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

        addLeftAndBigRight("总计",takeOutOrderBean.getConsumption_money()+"\n");
    }

    @Override
    public void addOrderTitleText(String name, String number, String money) {
        int nameLength=PRINTER_WIDTH/2;
        StringBuilder sb=new StringBuilder(name);
        for (int i=0;i<nameLength-name.length()*2;i++){
            sb.append(" ");
        }
        sb.append(number);
        for (int i=0;i<PRINTER_WIDTH-nameLength-number.length()*2-money.length()*2;i++){
            sb.append(" ");
        }
        sb.append(money);

        esc.addText(sb.toString()+"\n");
    }

    @Override
    public void addOrderText(String name, int number, double money) {
        String numberStr=String.valueOf(number);
        String moneyStr=String.valueOf(money);
        int nameLength=PRINTER_WIDTH/2;
        //标记为了居中使用了多少个空格
        int centerSpaceNum=0;

        StringBuilder sb=new StringBuilder(name);
        for (int i=0;i<nameLength-name.length()*2;i++){
            sb.append(" ");
        }
        //数量居中
        if (number<10){
            centerSpaceNum=2;
            sb.append("  ");
        }else if (number<100){
            centerSpaceNum=1;
            sb.append(" ");
        }

        sb.append(number);
        for (int i=0;i<PRINTER_WIDTH-nameLength-centerSpaceNum-numberStr.length()-moneyStr.length();i++){
            sb.append(" ");
        }
        sb.append(money);

        esc.addText(sb.toString()+"\n");
    }

    @Override
    public void addOtherBottomMsg(String qrUrl, String shopName, String shopPhone, String shopAddress) {
        esc.addText("…………………………………………\n");
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        esc.addSelectSizeOfModuleForQRCode((byte) 8);
        esc.addStoreQRCodeData(qrUrl);
        esc.addPrintQRCode();
        esc.addPrintAndFeedLines((byte) 1);

        esc.addText("扫描上方二维码查看订单详情\n");
        esc.addPrintAndFeedLines((byte) 2);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);

        esc.addText("感谢您使用"+shopName+"，订餐热线："+shopPhone+"\n");
        esc.addText("联系地址："+shopAddress+"\n");
        esc.addPrintAndFeedLines((byte) 2);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        esc.addText("技术支持·多粉 400-899-4522");
    }

    @Override
    public void storeCompose(StoreOrderBean storeOrderBean) {
        startCompose();
        addTitle("编号："+storeOrderBean.getOrder_id());
        divisionLine();
        addLeftText(storeOrderBean.getShop_name());
        divisionLine();
        addLeftText("下单时间："+storeOrderBean.getOrder_time());
        addLeftText("打印时间："+ TimeUtils.getNowString());
        lineFeed(1);
        addStoreOrder(storeOrderBean);
        addLeftText("支付方式："+storeOrderBean.getPayWay());
        addOtherBottomMsg(storeOrderBean.getQrUrl(),storeOrderBean.getShop_name(),storeOrderBean.getShop_phone(),storeOrderBean.getShop_adress());
        lineFeed(5);
    }

    @Override
    public void takeOutCompose(TakeOutOrderBean takeOutOrderBean) {
        startCompose();
        addTitle(takeOutOrderBean.getResName());
        divisionLine();
        addLeftText("流水号："+takeOutOrderBean.getOrder_no());
        addLeftText("下单时间："+takeOutOrderBean.getOrder_time());
        addLeftText("打印时间："+ TimeUtils.getNowString());
        lineFeed(1);
        addTakeOutOrder(takeOutOrderBean);
        if (!TextUtils.isEmpty(takeOutOrderBean.getRemark())){
            addLeftText("备注："+takeOutOrderBean.getRemark());
        }
        addLeftText("支付方式："+takeOutOrderBean.getPayWay());
        addLeftText("配送地址："+takeOutOrderBean.getMenAddress());
        addLeftText("联系人："+takeOutOrderBean.getMenName());
        addLeftText("联系方式："+takeOutOrderBean.getMenPhone());
        addOtherBottomMsg(takeOutOrderBean.getQrUrl(),takeOutOrderBean.getResName(),takeOutOrderBean.getResPhone(),takeOutOrderBean.getResAddress());
        lineFeed(5);
    }

    @Override
    public void print() {

    }


    public void printTestStoreOrder(){
        if(gson==null){
            gson=new Gson();
        }
        StoreOrderBean order=gson.fromJson(printTestString,StoreOrderBean.class);
        //打印不干胶
        PrintESCOrTSCUtil.printStoreXCM(order.getOrder_id(),order.getMenus());
        //微兔打印内置打印机
        if (MyApplication.getPrinter()!=null){
            MyApplication.getPrinter().printStoreOrder(order);
        }else{
            ToastUtil.getInstance().showToast("设备非打印机设备",5000);
        }

    }

    private void addLeftAndRight(String leftStr, String rightStr,boolean isRightBig){
        int amplification=isRightBig?2:1;
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        int leftLength=leftStr.length();
        int rightLength=rightStr.length()*amplification;
        int spaceLength=PRINTER_WIDTH-leftLength-rightLength;
        StringBuilder leftStrAndSpace=new StringBuilder(leftStr);
        for (int i=0;i<spaceLength;i++){
            leftStrAndSpace.append(" ");
        }
        esc.addText(leftStrAndSpace.toString());
        if (isRightBig){
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
        }

        esc.addText(rightStr);
        if (isRightBig){
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
        }
    }

    public EscCommand getEsc() {
        return esc;
    }
}

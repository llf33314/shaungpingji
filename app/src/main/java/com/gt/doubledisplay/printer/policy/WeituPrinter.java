package com.gt.doubledisplay.printer.policy;

import com.google.gson.Gson;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;
import com.gt.doubledisplay.printer.extraposition.PrintESCOrTSCUtil;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.weitoo.printer.MsPrinter;

/**
 * Created by wzb on 2017/9/19 0019.
 */

public class WeituPrinter implements PrinterPolicy {
    private Gson gson;

    @Override
    public void printStoreOrder(StoreOrderBean storeOrderBean) {
        MsPrinter.printStoreOrder(storeOrderBean);
    }

    @Override
    public void startCompose() {

    }


    @Override
    public void printTakeOutOrder(TakeOutOrderBean takeOutOrderBean) {
        MsPrinter.printTakeOutOrder(takeOutOrderBean);
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

    }

    @Override
    public void divisionLine() {

    }

    @Override
    public void lineFeed(int feed) {

    }

    @Override
    public void addLeftText(String leftStr) {

    }

    @Override
    public void addRightText(String rightStr) {

    }

    @Override
    public void addLeftAndRight(String leftStr, String rightStr) {

    }

    @Override
    public void addLeftAndBigRight(String leftStr, String rightStr) {

    }

    @Override
    public void addStoreOrder(StoreOrderBean storeOrderBean) {

    }

    @Override
    public void addTakeOutOrder(TakeOutOrderBean takeOutOrderBean) {

    }


    @Override
    public void addOrderTitleText(String name, String number, String money) {

    }

    @Override
    public void addOrderText(String name, int number, double money) {

    }

    @Override
    public void addOtherBottomMsg(String qrUrl, String shopName, String shopPhone, String shopAddress) {

    }

    @Override
    public void storeCompose(StoreOrderBean storeOrderBean) {

    }

    @Override
    public void takeOutCompose(TakeOutOrderBean takeOutOrderBean) {

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
}

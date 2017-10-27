package com.gt.doubledisplay.printer.policy;

import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;

/**
 * Created by wzb on 2017/9/19 0019.
 */

public interface PrinterPolicy {

    String printTestString="{\"cashier\":\"\",\"consumption_money\":62.32,\"fansCurrency_deduction\":0,\"integral_deduction\":0,\"member_deduction\":0,\"menus\":[{\"commnt\":\"\",\"menu_no\":\"1002\",\"money\":10,\"name\":\"红烧鱼\",\"norms\":\"\",\"num\":1,\"original_price\":10},{\"commnt\":\"\",\"money\":11,\"name\":\"叉烧包\",\"norms\":\"\",\"num\":1,\"original_price\":11},{\"commnt\":\"\",\"menu_no\":\"1004\",\"money\":38,\"name\":\"咸菜炒猪大肠\",\"norms\":\"\",\"num\":1,\"original_price\":38},{\"commnt\":\"\",\"money\":3.32,\"name\":\"鱼粥\",\"norms\":\"加1  小份  黑色\",\"num\":1,\"original_price\":3.32}],\"order_code\":\"DD1508895434927\",\"order_id\":\"A00005\",\"order_time\":\"2017-10-25 09:37:14\",\"payWay\":\"现金支付\",\"pay_money\":62.32,\"pay_time\":\"2017-10-25 09:37:16\",\"pay_type\":1,\"print_type\":1,\"qrUrl\":\"http://canyin.duofriend.com//simple/79B4DE7C/orderDetailtoxp.do?orderId=3459\",\"remark\":\"\",\"result\":1,\"shop_adress\":\"广东省深圳市南山区兰光科技园C座513\",\"shop_name\":\"谷通科技\",\"shop_phone\":\"0755-26609632\",\"yhq_deduction\":0}";

    /**
     * 开始排版 主要做一些初始化动作 清除缓存等
     */
    void startCompose();

    /**
     * 打印到店订单
     */
    void printStoreOrder(StoreOrderBean storeOrderBean);

    /**
     * 打印外卖订单
     */
    void printTakeOutOrder(TakeOutOrderBean takeOutOrderBean);

    /**
     * 打开钱箱
     */
    void openMoneyBox();

    /**
     * 打印测试
     */
    void printTest();


    /**
     * 粗体标题
     */
    void addTitle(String title);

    /**
     *  ... 分割行
     */
    void divisionLine();

    /**
     * 空白换行
     * @param feed  换行数
     */
    void lineFeed(int feed);

    /**
     * 添加左边文字
     */
    void addLeftText(String leftStr);
    /**
     * 添加右边边文字
     */
    void addRightText(String rightStr);

    /**
     * 添加左右文字
     */
    void addLeftAndRight(String leftStr,String rightStr);
    /**
     * 添加左右文字   右边变粗
     */
    void addLeftAndBigRight(String leftStr,String rightStr);
    /**
     * 添加到店订单
     */
    void addStoreOrder(StoreOrderBean storeOrderBean);
    /**
     * 添加外卖订单
     */
    void addTakeOutOrder(TakeOutOrderBean takeOutOrderBean);

    /**
     * 商品栏标题
     */
    void addOrderTitleText(String name,String number,String money);

    /**
     * 添加商品
     */
    void addOrderText(String name,int number,double money);
    /**
     * 添加底部其他信息打印
     */
    void addOtherBottomMsg(String qrUrl,String shopName,String shopPhone,String shopAddress);

    /**
     * 到店小票排版
     */
    void storeCompose(StoreOrderBean storeOrderBean);
    /**
     * 外卖小票排版
     */
    void takeOutCompose(TakeOutOrderBean takeOutOrderBean);

    /**
     * 自定义排版后打印
     * 暂留接口后期 其他erp开发
     */
    void print();

}

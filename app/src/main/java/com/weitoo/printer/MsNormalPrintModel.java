/*
package com.weitoo.printer;


import com.printsdk.cmd.PrintCmd;

import java.math.BigDecimal;

*/
/**
 * Created by Lunger on 2017/02/10.
 *//*

@Deprecated
public class MsNormalPrintModel extends MsTicketPrintModel {

    public final static int PAY_TYPE_CASH = 0;
    public final static int PAY_TYPE_BANK = 1;
    public final static int PAY_TYPE_DOORTODOOR = 2;
    public final static int PAY_TYPE_MOBILE = 3;

    public void packPrintData1() {
        printFromLeft();
        appendData(PrintCmd.SetLinespace(40));
        appendLeftRightData("订单号：", "123456789");
        appendLeftRightData("结算时间:", "2017-03-17");
        String type;
        switch (1) {
            case PAY_TYPE_CASH:
                type = "现金支付";
                break;
            case PAY_TYPE_BANK:
                type = "银联刷卡";
                break;
            case PAY_TYPE_MOBILE:
                type = "手机支付";
                break;
            case PAY_TYPE_DOORTODOOR:
                type = "货到付款";
                break;
            default:
                type = "支付类型";
                break;
        }
        appendLeftRightData("结算方式:", type);

        appendDividingLine();
        appendGoods("商品名", "单价", "数量", "金额");
        String goodsName;
        String value;
//        CommodityModel commodity;
//        for (int i = 0; i < curOrder.getCommodities().size(); i++) {
//            commodity = curOrder.getCommodities().get(i);
//            goodsName = commodity.getName();
//            value = DecimalUtils.multiply(
//                    commodity.getPrice().toString(), Double.toString(commodity.getNum()));
//
//            appendGoods(goodsName, String.valueOf(commodity.getPrice()), String.valueOf(commodity.getNum()), value);
//        }
        appendDividingLine();


        BigDecimal orderPrice = new BigDecimal(30);
        BigDecimal receivable = orderPrice.subtract(new BigDecimal(100));
        appendLeftRightData("商品小计:", getDoublePointAfter2(orderPrice));
        appendLeftRightData("销售折扣:", getDoublePointAfter2(new BigDecimal(20)));
        appendLeftRightData("应收:", getDoublePointAfter2(orderPrice.subtract(new BigDecimal(20))));

        if (1 == PAY_TYPE_CASH) {
            appendLeftRightData("实收:", getDoublePointAfter2(new BigDecimal(20)));
            appendLeftRightData("找零:", getDoublePointAfter2(new BigDecimal(20).subtract(receivable)));
        }
        if (2 == PAY_TYPE_DOORTODOOR) {
            appendLeftRightData("实收:", getDoublePointAfter2(new BigDecimal(20)));
        }

        appendDividingLine();

//        VipModel vipModel = curOrder.getVipModel();
//        if (vipModel != null) {
//            String mobile = vipModel.getMobile();
//            if(mobile.length() == 11){
//                StringBuffer sb = new StringBuffer();
//                mobile = sb.append(mobile.substring(0, 3)).append("***").append(mobile.substring(7,11)).toString();
//            }
//            appendLeftData("手机号码：" + mobile);
//            appendLeftData("本次积分：" + vipModel.getEarnPoints());
//            appendLeftData("积分抵扣：" + vipModel.getCostPoints());
//            appendLeftData("积分余额：" + vipModel.getRemainingPoints());
//        }

        appendRunLine(1);
        appendCenterData("下载微兔手机客户端" + "\n" + "手机下单  送货上门");
        appendRunLine(1);

        //SetFeedCutClean(0);
    }

}
*/

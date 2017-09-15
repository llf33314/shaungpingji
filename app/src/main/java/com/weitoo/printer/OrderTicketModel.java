package com.weitoo.printer;

import com.weitoo.util.DateUtil;

import java.util.Date;

/**
 * Alter by sky on 2016/10/17
 * Created by Awen on 2016/9/7.
 */
@Deprecated
public class OrderTicketModel extends AbstractTicketModel {

    /**
     * 打印小票, 24文字
     */
    public void packPrintData(){
        //订单详情
        data.append(PRINTERCONFIG.LeftCMD);
        data.append(PRINTERCONFIG.DividingLine);
        data.append(PRINTERCONFIG.LeftCMD);
        data.append(PRINTERCONFIG.getLineForPrint("订单号:", "0000000000001"));
        data.append(PRINTERCONFIG.getLineForPrint("结算时间:", DateUtil.dateToLongString(new Date())));

        data.append(PRINTERCONFIG.getLineForPrint("结算方式:", "现金支付"));
        data.append(PRINTERCONFIG.DividingLine);

        //商品列表
        data.append(PRINTERCONFIG.LeftCMD);
        data.append(PRINTERCONFIG.goodsNameForPrint("商品名"));
        data.append(PRINTERCONFIG.getLineWidth25("商品名", "单价", "数量", "金额"));

        for (int i = 0; i < 1; i++) {
            String nameForPrint = PRINTERCONFIG.goodsNameForPrint("打印出来的肯定没问题");
            data.append(nameForPrint);
            data.append(PRINTERCONFIG.getLineWidth25(nameForPrint, "1.0", "1.0", "1.0"));
        }
        data.append(PRINTERCONFIG.DividingLine);

        //合计
        data.append(PRINTERCONFIG.LeftCMD);
        data.append(PRINTERCONFIG.getLineForPrint("应收:", "1.0"));
        data.append(PRINTERCONFIG.getLineForPrint("减免:", "1.0"));
        data.append(PRINTERCONFIG.getLineForPrint("实收:", "1.0"));


        data.append(PRINTERCONFIG.DividingLine);
        data.append(PRINTERCONFIG.NextLine);

        //二维码
        appendQRCode(data);
    }

}

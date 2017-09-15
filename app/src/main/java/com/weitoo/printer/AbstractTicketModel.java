package com.weitoo.printer;

import java.math.BigDecimal;

/**
 * Created by Awen on 2016/9/7.
 */
public abstract class AbstractTicketModel {

    final TicketPrinterConfig PRINTERCONFIG = new TicketPrinterConfig();
    final StringBuilder data = new StringBuilder();

    AbstractTicketModel() {
        this(true);
    }

    AbstractTicketModel(boolean needShopName) {
//        data.append(new String(new byte[]{0x1B, 0x47, 0x04}));
        data.append(new String(new byte[]{0x1C, 0x61, 0x01}));    //自动调速：0关闭，1打开
        data.append(new String(new byte[]{0x1B, 0x72, 0x01}));    //浓度
        data.append(new String(new byte[]{0x1C, 0x73, 0x55}));    // 80mm/s
        data.append(PRINTERCONFIG.CenterCMD);
        if (needShopName) {
            data.append("微兔硬件测试\n");
        }

    }

    void appendQRCode(StringBuilder data) {
        //二维码
        data.append(PRINTERCONFIG.CenterCMD);
        data.append("下载微兔手机客户端").append(PRINTERCONFIG.NextLine);
        data.append("手机下单  送货上门").append(PRINTERCONFIG.NextLine);
    }

    void appendEndSpace(StringBuilder data) {
        data.append(PRINTERCONFIG.SpaceLine)
                .append(PRINTERCONFIG.SpaceLine)
                .append(PRINTERCONFIG.SpaceLine)
                .append(PRINTERCONFIG.SpaceLine);
    }

    public byte[] getPrintData() {
        if (data == null)
            return PRINTERCONFIG.getCustomByte("无可打印数据");
        return PRINTERCONFIG.getCustomByte(data.toString());
    }

    String getDoublePointAfter2(BigDecimal src) {
        String result = ""+src.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        int index = result.indexOf(".");
        if (index < 0) {
            return result + ".00";
        } else if (index == result.length() - 2) {
            return result + "0";
        } else {
            return result;
        }

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

    public static String int2String(Integer i) {
        if (i == null)
            return "0";
        return i.toString();
    }
}

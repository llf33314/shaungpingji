package com.weitoo.printer;

import java.io.UnsupportedEncodingException;

import static com.weitoo.printer.MsTicketPrintModel.SPCE;

/**
 * 只是简单帮你组织byte[] 数组
 */
public final class MsTicketPrintHelper {

    //这个变量不同打印机的最大字符宽度
    private final int LINE_CHAR_COUNT = 48;
    public final String NextLine = "\n";
    public final String PRINTER_ENCODING = "GBK";
    public final String DIVIDING_LINE = SPCE+"……………………………………………………" +NextLine;
    private static MsTicketPrintHelper instance;
    private StringBuffer sb = new StringBuffer();

    public static MsTicketPrintHelper getInstance() {
        if (instance == null) {
            synchronized (MsTicketPrintHelper.class) {
                if (instance == null) {
                    instance = new MsTicketPrintHelper();
                }
            }
        }
        return instance;
    }

    private String getNString(int n, String c) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < n; i++) {
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    /**
     * 用来打印左右对齐的行
     *
     * @param left
     * @param right
     * @return
     */
    public String getLineForPrint(String left, String right) {
        /*int spaceNum = LINE_CHAR_COUNT - getCustomByteLen(left) - getCustomByteLen(right) - 4;

        return left + getNString(spaceNum, " ") + right + NextLine;*/

        return getLine(LINE_CHAR_COUNT, left, right);
    }

    private String getLine(int totalWidth, String left, String right) {

        return getLine(totalWidth, left, right, true);
    }

    private String getLine(int totalWidth, String left, String right, boolean needNextLine) {
        int spaceNum = totalWidth - getCustomByteLen(left) - getCustomByteLen(right) - 4;
        //wzb 缩进去一点点
        if (spaceNum < 0) {
            spaceNum = 0;
        }
        return left + getNString(spaceNum, " ") + right + (needNextLine ? NextLine : "");
    }

    /**
     * 右边粗体
     */
    public String getBigRightLine(String left,String right){
       // int spaceNum = LINE_CHAR_COUNT - getCustomByteLen(right)*2-4;  //这个4是右边距
        //int num=8-right.length(); //这个算法不靠谱 纯粹是试出来的
        int spaceNum = LINE_CHAR_COUNT - getCustomByteLen(left) - getCustomByteLen(right)*2;
        if (spaceNum < 0) {
            spaceNum = 0;
        }
        return  getNString(spaceNum, " ") + right ;

    }

    public String getGoodsLine(String name, String unitPrice, String num, String sum) {
        int len = getCustomByteLen(name);
        //Log.d("wbl", "length = " + len);
        if (len > 18) {
            char[] chars = new char[name.length()];
            name.getChars(0, name.length(), chars, 0);
            int tmpCharLen = 0;
            for (char c : chars){
                if(c > 0 && c < 126){
                    tmpCharLen += 1;
                }else{
                    tmpCharLen += 2;
                }
                sb.append(c);
                if(tmpCharLen % 18 == 0 && tmpCharLen != 0){
                    sb.append(NextLine);
                }
            }
        }else{
            sb.append(name);
        }
        int lastLineLen = len % (18);
        int spaceLeft = 22 - lastLineLen;
        sb.append(getNString(spaceLeft, " ")).append(getLine(25, getLine(17, unitPrice, num, false), sum));
        String goodsLine = sb.toString();
        sb.setLength(0);
        return goodsLine;
    }

    public int getCustomByteLen(String src) {
        try {
            return src.getBytes(PRINTER_ENCODING).length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

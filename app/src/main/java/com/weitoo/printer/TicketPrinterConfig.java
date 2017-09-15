package com.weitoo.printer;


import java.io.UnsupportedEncodingException;

/**
 * Created by Awen on 2016/9/7.
 */
public final class TicketPrinterConfig {

    public static final String APP_DOWNLOAD_URL = "http://www.weitoo.com/wap/";
    private final String PRINTER_ENCODING = "GBK";
    private final int LINE_CHAR_COUNT = 48;

    private final byte[] defaultHotCMD = new byte[]{0x1B, 0x47, 0x03};  //设置加热组
    private final byte[] customHotCMD = new byte[]{0x1B, 0x47, 0x06};
    private final byte[] leftCMD = new byte[]{0x1b,0x61,0x00};
    private final byte[] centerCMD = new byte[]{0x1b,0x61,0x01};
    private final byte[] rightCMD = new byte[]{0x1b,0x61,0x02};
    private final byte[] cleanBuf = new byte[]{0x1b,0x40}; //该命令后打印机恢复默认值,清空打印buf，但是接收buf中的数据不会被清空
    private final byte[] startTable = new byte[]{0x1b,0x44,0x02,0x1C,0x28,0x00};
    private final byte[] tableCol = new byte[]{0x09};
    private final byte[] tableColEnd = new byte[]{0x0D};
    private final byte[] endTable = new byte[]{0x1b,0x44,0x00};
    private final byte[] absolute = new byte[]{0x1B, 0x24, 0x1E, 0x00};
    private final byte[] qrCodeStart = new byte[]{0x1D,0x6C,0x01,0x1D,0x53,0x08,0x1D,0x44, (byte) getCustomByteLen(APP_DOWNLOAD_URL),0x00};
    private final byte[] qrCodeContent = getCustomByte(APP_DOWNLOAD_URL);
    private final byte[] qrCodeEnd = new byte[]{0x0A,0x0D,0x0A,0x0D};


    public final String NextLine = "\n";
    public final String LeftCMD = new String(leftCMD);
    public final String CenterCMD = new String(centerCMD);
    public final String RightCMD = new String(rightCMD);
    public final String CleanBuf = new String(cleanBuf);
    public final String StartTable = new String(startTable);
    public final String TableCol = new String(tableCol);
    public final String TableColEnd = new StringBuffer().append(new String(tableColEnd)).append(NextLine).toString();
    public final String EndTable = new String(endTable);
    public final String Absolute = new String(absolute);
    public final String QRCode = new StringBuffer().append(new String(qrCodeStart)).append(new String(qrCodeContent)).append(new String(qrCodeEnd)).append(NextLine).toString();
    public final String DividingLine = CenterCMD + "-----------------------------------------------" + NextLine;
    public final String SpaceLine    = CenterCMD + "                                               " + NextLine;

    private String getNString(int n, String c){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < n; i++) {
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    /**
     * 用来处理商品名称过长换行
     *    num = 23;商品名到紧挨数量中间的字符数   --  需要预留4个字符位置(22 - 5 = 18)
     *    num = 48;整行显示的字符数
     *
     *    根据字符串的字节数计算长度，判断是否需要换行
     *
     *   如果当前字符串buffer的长度 + 下一个字符串的长度（可能为一个字符或者汉字） 大于23 则 换行
     *
     *   注意：我调试的打印机的编码是GBK，不同编码，字节长度不一样
     *
     * @param goodsName
     * @return
     */
    public String goodsNameForPrint(String goodsName){
        final int nameLen = 18;
        int tmpLen = getCustomByteLen(goodsName);
        if (tmpLen > nameLen) {
            int charCount = 0, lineCount = 1, byteCount = 0;
            StringBuffer buffer = new StringBuffer();
            String tmpStr;
            while (charCount < tmpLen) {
                tmpStr = goodsName.substring(charCount, charCount+1);
                buffer.append(tmpStr);
                byteCount += getCustomByteLen(tmpStr);

                int nextCharCount = charCount + 1;
                //LogUtils.fff("goodsName Len: "+goodsName.length()+", byteCount: " + byteCount + ", charCount: " + charCount + ", nextCharCount: " + nextCharCount +", lineCount: " + lineCount);
                if (nextCharCount < goodsName.length()) {
                    String tmpNextStr = goodsName.substring(nextCharCount, nextCharCount +1);
                    int nextByteCount = byteCount + getCustomByteLen(tmpNextStr);
                    if (nextByteCount > nameLen*lineCount) {
//                        buffer.append(new String(tableColEnd)).append("\n").append(new String(tableCol));
                        buffer.append(NextLine);
                        lineCount++;
                    }
                } else {
                    break;
                }
                charCount++;
            }
            goodsName = buffer.toString();
        }
        return goodsName;
    }

    public String getOtherStringForPrint(String goodsName){
        if (goodsName == null || goodsName.equals(""))
            return "";
        goodsName = goodsName.replaceAll("\n","");
        final int nameLen = 39;
        int tmpLen = getCustomByteLen(goodsName);
        if (tmpLen > nameLen) {
            int charCount = 0, lineCount = 1, byteCount = 0;
            StringBuffer buffer = new StringBuffer();
            String tmpStr;
            while (charCount < tmpLen) {
                tmpStr = goodsName.substring(charCount, charCount+1);
                buffer.append(tmpStr);
                byteCount += getCustomByteLen(tmpStr);

                int nextCharCount = charCount + 1;
                //LogUtils.fff("goodsName Len: "+goodsName.length()+", byteCount: " + byteCount + ", charCount: " + charCount + ", nextCharCount: " + nextCharCount +", lineCount: " + lineCount);
                if (nextCharCount < goodsName.length()) {
                    String tmpNextStr = goodsName.substring(nextCharCount, nextCharCount +1);
                    int nextByteCount = byteCount + getCustomByteLen(tmpNextStr);
                    if (nextByteCount > nameLen*lineCount) {
                        buffer.append(new String(tableColEnd)).append("\n").append(getNString(9, " "));
                        lineCount++;
                    }
                } else {
                    break;
                }
                charCount++;
            }
            goodsName = buffer.toString();
        }
        return goodsName;
    }

    public int getCustomByteLen(String src){
        try {
            return src.getBytes(PRINTER_ENCODING).length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public byte[] getCustomByte(String src){
        try {
            return src.getBytes(PRINTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用来打印左右对齐的行
     * @param left
     * @param right
     * @return
     */
    public String getLineForPrint(String left, String right){
        /*int spaceNum = LINE_CHAR_COUNT - getCustomByteLen(left) - getCustomByteLen(right) - 4;

        return left + getNString(spaceNum, " ") + right + NextLine;*/

        return getLine(LINE_CHAR_COUNT, left, right);
    }

    public String getLineWidth25(String startString, String left, String center, String right) {
        int len = getCustomByteLen(startString);
        int spaceLeft = len%(18+1);  //+1表示换行
        int tmp = spaceLeft == 0 ? 4 : 22 - spaceLeft;
        return getNString(tmp, " ") + getLine(25, getLine(17, left, center, false), right);
    }

    public String getLine(int totalWidth, String left, String right) {

        return getLine(totalWidth, left, right, true);
    }

    public String getLine(int totalWidth, String left, String right, boolean needNextLine) {
        int spaceNum = totalWidth - getCustomByteLen(left) - getCustomByteLen(right) - 4;
        if (spaceNum < 0) {
            spaceNum = 0;
        }
        return left + getNString(spaceNum, " ") + right + (needNextLine?NextLine:"");
    }
}

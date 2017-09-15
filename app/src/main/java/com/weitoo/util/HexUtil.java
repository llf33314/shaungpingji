package com.weitoo.util;

/**
 * Created by Awen on 2016/7/6.
 */
public class HexUtil {


    public static String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String getIpShow(byte[] ipByte) {
        StringBuilder sb = new StringBuilder();
        for (byte b : ipByte) {
            sb.append(b&0xFF).append(".");
        }
        sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }

    public static String getMacShow(byte[] macByte) {
        StringBuilder sb = new StringBuilder();
        for (int offset = 0; offset < macByte.length; offset++) {
            int i = macByte[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(i)).append(":");
        }
        sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }

    public static String printHex(byte[] b) {
        StringBuffer buf = new StringBuffer("");
        int i;
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i)).append(" ");
        }
        System.out.println(buf.toString());
        return buf.toString();
    }

    public static byte[] int2byteLitEndia(int src, int len) {
        byte[] b = new byte[len];
        int moveBit;
        for (int i = 0 ; i < len; i++) {
            moveBit = i*8;
            b[i] = (byte) (src >> moveBit);
        }

        return b;
    }

}

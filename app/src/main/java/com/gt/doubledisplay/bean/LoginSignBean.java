package com.gt.doubledisplay.bean;

/**
 * Created by wzb on 2017/8/24 0024.
 */

public class LoginSignBean {
    private int code;

    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class  Data{
        private String sign;
        private String timeStamp;
        private String randNum;

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public String getRandNum() {
            return randNum;
        }

        public void setRandNum(String randNum) {
            this.randNum = randNum;
        }
    }

}

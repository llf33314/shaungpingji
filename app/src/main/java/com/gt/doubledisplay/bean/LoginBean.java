package com.gt.doubledisplay.bean;

/**
 * Created by wzb on 2017/8/17 0017.
 */

public class LoginBean {

    /**
     * sign : {"sign":"7e0da799f9d813de9028e6991459de3c","timeStamp":"1506390506077","randNum":"51774"}
     * message : 登录成功
     * error : 0
     * style : 1
     * UserId : 4856
     */

    private SignBean sign;
    private String message;
    private String error;
    private int style;
    private int UserId;

    public SignBean getSign() {
        return sign;
    }

    public void setSign(SignBean sign) {
        this.sign = sign;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int UserId) {
        this.UserId = UserId;
    }

    public static class SignBean {
        /**
         * sign : 7e0da799f9d813de9028e6991459de3c
         * timeStamp : 1506390506077
         * randNum : 51774
         */

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

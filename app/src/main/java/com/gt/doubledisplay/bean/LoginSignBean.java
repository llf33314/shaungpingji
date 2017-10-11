package com.gt.doubledisplay.bean;

/**
 *获取啊祥的接口 返回sign 等 再请求陈丹共享登录接口
 */

public class LoginSignBean {

    /**
     * sign : {"sign":"6c8f6e08fccd8c5c775dd83a032f456f","timeStamp":"1507625145441","randNum":"23793"}
     * message : 登录成功
     * eqCode : 97433959
     * style : 1
     * UserId : 4856
     */

    private SignBean sign;
    private String message;
    private String eqCode;
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

    public String getEqCode() {
        return eqCode;
    }

    public void setEqCode(String eqCode) {
        this.eqCode = eqCode;
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
         * sign : 6c8f6e08fccd8c5c775dd83a032f456f
         * timeStamp : 1507625145441
         * randNum : 23793
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

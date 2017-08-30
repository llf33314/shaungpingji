package com.gt.doubledisplay.bean;

/**
 * 发送给副屏的消息
 */

public class ScreenMsgBean {
    private String msg;

    public ScreenMsgBean(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

package com.gt.doubledisplay.bean;

/**
 * Created by wzb on 2017/10/30 0030.
 */

public class MainScreenMsgBean {
    private String methonName;
    private String msg;

    public MainScreenMsgBean(String msg) {
        this.msg = msg;
    }

    public MainScreenMsgBean(String methonName, String msg) {
        this.methonName = methonName;
        this.msg = msg;
    }

    public String getMethonName() {
        return methonName;
    }

    public void setMethonName(String methonName) {
        this.methonName = methonName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

package com.gt.doubledisplay.bean;

/**
 * 发送给副屏的消息
 */

public class AssistantScreenMsgBean {
    private String methonName;
    private String msg;

    public AssistantScreenMsgBean(String msg) {
        this.msg = msg;
    }

    public AssistantScreenMsgBean(String methonName, String msg) {
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

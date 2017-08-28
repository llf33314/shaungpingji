package com.gt.doubledisplay.http;

/**
 * 接口数据类型
 */
public class BaseResponse<T> {

    public static final int SUCCESS_STATUS = 0;

    private int code;
    private T data;

    public boolean isSuccess() {
        return  code == SUCCESS_STATUS;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

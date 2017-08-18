package com.gt.doubledisplay.http;

/**
 * 接口数据类型
 */
public class BaseResponse<T> {

    public static final int SUCCESS_STATUS = 0;

    private int error;
    private String message;
    private T data;


    public boolean isSuccess() {
        return  error == SUCCESS_STATUS;
    }
    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

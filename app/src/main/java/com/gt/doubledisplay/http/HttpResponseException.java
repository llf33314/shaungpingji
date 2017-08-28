package com.gt.doubledisplay.http;



public class HttpResponseException extends RuntimeException {

    private  int code;

    public HttpResponseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

package com.gt.doubledisplay.http;

/**
 * <p>Description:
 *
 * <p>Created by Devin Sun on 2017/3/25.
 */

public class HttpResponseException extends RuntimeException {

    private  int error;

    public HttpResponseException(String message, int error) {
        super(message);
        this.error = error;
    }

    public int getError() {
        return error;
    }
}

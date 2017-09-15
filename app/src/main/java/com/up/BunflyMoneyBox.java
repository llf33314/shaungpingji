package com.up;

/**
 * Created by Awen on 2016/9/6.
 */
public class BunflyMoneyBox {

    static {
        System.loadLibrary("weitoobox");
    }

    public native int boxopen();

    public native int boxclose();
}

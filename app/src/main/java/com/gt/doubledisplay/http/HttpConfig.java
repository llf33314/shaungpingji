package com.gt.doubledisplay.http;

/**
 * Created by wzb on 2017/7/11 0011.
 */

public class HttpConfig {
    public static final int SUCCESS_CODE=200;

    public static final String BASE_URL = "http://deeptel.com.cn";
    public static final String LOGIN_URL=BASE_URL+"/ErpMenus/79B4DE7C/Erplogin.do";
    public static final String WEB_LOGIN=BASE_URL+"/user/tologin.do";

    public static final String SOCKET_SERVER_URL = "http://192.168.3.10:9092";
   //public static final String SOCKET_SERVER_URL = "https://socketio-chat.now.sh/";

    // socket auth 链接socket时的key值
    public static final String SOCKET_ANDROID_AUTH = "androidAuth";
    // socket auth key 发送socket请求或接收socket请求的key值
    public static final String SOCKET_ANDROID_AUTH_KEY = "doubleScreen_socket";


}

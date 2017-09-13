package com.gt.doubledisplay.http;

/**
 * Created by wzb on 2017/7/11 0011.
 */

public class HttpConfig {

    //测试
    //public static final String BASE_URL = "http://yj.deeptel.com.cn/";
    //堡垒
   public static final String BASE_URL = "http://nb.yj.deeptel.com.cn/";
    //正式
   // public static final String BASE_URL = " http://yj.duofriend.com/";

    public static final String LOGIN_URL=BASE_URL+"/ErpMenus/79B4DE7C/Erplogin.do";

    //正式
   // public static final String DUOFRIEND_XCM="http://canyin.duofriend.com/";
    //堡垒
    public static final String DUOFRIEND_XCM="http://nb.canyin.deeptel.com.cn/";
    //测试
    //public static final String DUOFRIEND_XCM="http://canyin.deeptel.com.cn/";

    public static final String ADVERTISING_RUL=BASE_URL+"doubleScreenIndex/mobileIndex";
    //public static final String ADVERTISING_RUL="http://192.168.3.39:8091/views/Slide/list.html";

     //socket auth 链接socket时的key值  测试
   // public static final String SOCKET_SERVER_URL = "http://113.106.202.51:8881";
    public static final String SOCKET_SERVER_URL = "http://183.47.242.2:8881";
    public static final String SOCKET_ANDROID_AUTH = "androidAuth";
    // socket auth key 发送socket请求或接收socket请求的key值
   // public static final String SOCKET_ANDROID_AUTH_KEY = "doubleScreen_socket";
    public static final String SOCKET_ANDROID_AUTH_KEY = "doubleScreen_eat_";
}

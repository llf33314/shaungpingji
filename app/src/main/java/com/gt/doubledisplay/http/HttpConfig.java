package com.gt.doubledisplay.http;

/**
 * Created by wzb on 2017/7/11 0011.
 */

public class HttpConfig {

    //测试
    //public static final String BASE_URL = "https://yj.deeptel.com.cn/";
    //堡垒
    //public static final String BASE_URL = "https://nb.yj.deeptel.com.cn/";
    //正式
     public static final String BASE_URL = "https://yj.duofriend.com/";

    //微站测试
   // public static final String BASE_URL = "http://wz.yj.deeptel.com.cn/";
    //微站正式
   // public static final String BASE_URL = "http://yj.gutong.114zan.com/";


    //public static final String LOGIN_URL=BASE_URL+"/ErpMenus/79B4DE7C/Erplogin.do";

    //测试
   // public static final String DUOFRIEND_XCM="https://canyin.deeptel.com.cn/";
    //堡垒
     //public static final String DUOFRIEND_XCM="https://nb.canyin.deeptel.com.cn";
    //正式
      public static final String DUOFRIEND_XCM="https://canyin.duofriend.com/";

    //微站测试
   // public static final String DUOFRIEND_XCM="http://wz.canyin.deeptel.com.cn/login/index.do";
    //微站正式
   // public static final String DUOFRIEND_XCM="http://cy.gutong.114zan.com/login/index.do";

    public static final String DUOFRIEND_XCM_LOGIN_OUT_URL=DUOFRIEND_XCM+"login/out.do";
    //session过期
    public static final String DUOFRIEND_XCM_LOGIN_OUT_URL_2=DUOFRIEND_XCM+"login/index.do";


    //副屏广告
    public static final String ADVERTISING_RUL=BASE_URL+"doubleScreenIndex/mobileIndex";

     //socket auth 链接socket时的key值  测试
   // public static final String SOCKET_SERVER_URL = "http://113.106.202.51:8881";
    //public static final String SOCKET_SERVER_URL = "http://183.47.242.2:8881";
    public static final String SOCKET_ANDROID_AUTH = "androidAuth";
    // socket auth key 发送socket请求或接收socket请求的key值
   // public static final String SOCKET_ANDROID_AUTH_KEY = "doubleScreen_socket";
    public static final String SOCKET_ANDROID_AUTH_KEY = "doubleScreen_eat_";


    //陈丹erp登录接口
    //堡垒
   // public static final String LOGIN_URL="http://nb.deeptel.com.cn/ErpMenus/79B4DE7C/DoubleErplogin.do";
    //正式
    public static final String LOGIN_URL="https://duofriend.com/ErpMenus/79B4DE7C/DoubleErplogin.do";
}

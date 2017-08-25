package com.gt.doubledisplay.http;




import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

 /*   @FormUrlEncoded
   // @POST("ErpMenus/79B4DE7C/Erplogin.do")
    @POST("https://deeptel.com.cn/ErpMenus/79B4DE7C/Erplogin.do")
    Observable<String> login(@Field("login_name")String account, @Field("password")String psd,@Field("sign")String sign);*/

    @FormUrlEncoded
    @POST("doubleScreenMobile/getBusId")
    Observable<String> login(@Field("login_name")String account, @Field("password")String psd);

/*    @FormUrlEncoded
    @POST("http://192.168.3.44:8081/doubleScreenMobile/getSign")
    Observable<String> getSign(@Field("login_name")String account, @Field("password")String psd,@Field("signCode") String signCode);*/

    @FormUrlEncoded
   // @POST("http://192.168.3.44:8081/doubleScreenMobile/redisKey")
    @POST("doubleScreenMobile/getPrintData")
    Observable<String> getPrintTscOrder(@Field("orderId")String orderId);
}

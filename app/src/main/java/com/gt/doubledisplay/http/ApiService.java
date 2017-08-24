package com.gt.doubledisplay.http;




import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
   // @POST("ErpMenus/79B4DE7C/Erplogin.do")
    @POST("https://sz.yifriend.net/ErpMenus/79B4DE7C/Erplogin.do")
    Observable<BaseResponse> login(@Field("login_name")String account, @Field("password")String psd,@Field("sign")String sign);

    @FormUrlEncoded
    @POST("http://192.168.3.44:8081/doubleScreenMobile/getSign")
    Observable<String> getSign(@Field("login_name")String account, @Field("password")String psd,@Field("signCode") String signCode);
}

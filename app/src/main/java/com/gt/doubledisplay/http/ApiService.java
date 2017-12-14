package com.gt.doubledisplay.http;




import com.gt.doubledisplay.bean.LoginSignBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST(HttpConfig.LOGIN_URL)
    Observable<String> login(@Field("login_name")String account, @Field("password")String psd, @Field("sign")String sign);

    @FormUrlEncoded
    @POST("doubleScreenMobile/getBusId")
    Observable<BaseResponse<LoginSignBean>> getBusId(@Field("login_name")String account, @Field("password")String psd);

    @FormUrlEncoded
    @POST("doubleScreenMobile/getPrintData")
    Observable<String> getPrintTscOrder(@Field("orderId")String orderId);

    @POST("http://hz1.yifriend.net/ErpMenus/79B4DE7C/Erplogin.do")
    Observable<String>getErpLogin(@Query("login_name")String login_name,@Query("password")String password,@Query("sign")String sign);


}

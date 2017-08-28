package com.gt.doubledisplay.http;




import com.gt.doubledisplay.bean.DeviceBean;
import com.gt.doubledisplay.bean.LoginBean;
import com.gt.doubledisplay.bean.LoginSignBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("doubleScreenMobile/getBusId")
    Observable<BaseResponse<LoginBean>> login(@Field("login_name")String account, @Field("password")String psd,@Field("sign")String sign);

    /*@FormUrlEncoded
    @POST("doubleScreenMobile/getBusId")
    Observable<BaseResponse<LoginBean>> login(@Field("login_name")String account, @Field("password")String psd);*/

    @FormUrlEncoded
    @POST("doubleScreenMobile/getSign")
    Observable<BaseResponse<LoginSignBean>> getSign(@Field("login_name")String account, @Field("password")String psd, @Field("signCode") String signCode);

    @FormUrlEncoded
   // @POST("http://192.168.3.44:8081/doubleScreenMobile/redisKey")
    @POST("doubleScreenMobile/getPrintData")
    Observable<String> getPrintTscOrder(@Field("orderId")String orderId);

    @POST("doubleScreenMobile/getEqCode")
    Observable<BaseResponse<DeviceBean>>getDeviceId(@Query("busId") String busId);
}

package com.gt.doubledisplay.http;




import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    String BASE_URL = "http://deeptel.com.cn";
    String LOGIN_URL=BASE_URL+"/ErpMenus/79B4DE7C/Erplogin.do";
    String WEB_LOGIN=BASE_URL+"/user/tologin.do";

    @FormUrlEncoded
    @POST("ErpMenus/79B4DE7C/Erplogin.do")
    Observable<BaseResponse> login(@Field("login_name")String account, @Field("password")String psd);
}

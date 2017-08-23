package com.gt.doubledisplay.http;




import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("ErpMenus/79B4DE7C/Erplogin.do")
    Observable<BaseResponse> login(@Field("login_name")String account, @Field("password")String psd);
}

package com.gt.doubledisplay.http;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * Created by wzb on 2017/7/11 0011.
 */

public interface ApiService {
    @FormUrlEncoded
    @POST("service_name")
    Observable<BaseResponse> test( @Field("lost_project_reasons[]") ArrayList<String> arr,
                                    @PartMap() Map<String, RequestBody> imgs
    );
}

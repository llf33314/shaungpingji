package com.gt.doubledisplay.http.retrofit;

import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.http.retrofit.converter.string.StringConverterFactory;
import com.gt.doubledisplay.http.ApiService;
import com.gt.doubledisplay.web.store.CookieJarImpl;
import com.gt.doubledisplay.web.store.PersistentCookieStore;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <p>Description:
 * <p>
 * <p>Created by Devin Sun on 2017/3/24.
 */

public class HttpCall {

    private static String token;

    private static ApiService mApiService;

    public static ApiService getApiService(){
        if (mApiService==null){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            PersistentCookieStore persistentCookieStore = new PersistentCookieStore(MyApplication.getAppContext());
            CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore,MyApplication.getAppContext());

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    // .addNetworkInterceptor(mRequestInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .cookieJar(cookieJarImpl)
                    // .authenticator(mAuthenticator2)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiService.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(StringConverterFactory.create()) //String 转换
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .validateEagerly(true)
                    .build();

            mApiService = retrofit.create(ApiService.class);
        }
        return mApiService;
    }


}

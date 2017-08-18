package com.gt.doubledisplay.web.store;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.gt.doubledisplay.http.ApiService;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {
    private CookieStore cookieStore;
    private Context context;
    CookieManager cookieManager ;

    public CookieJarImpl(CookieStore cookieStore,Context context) {
        if (cookieStore == null) new IllegalArgumentException("cookieStore can not be null.");
        this.cookieStore = cookieStore;
        CookieSyncManager.createInstance(context);
        cookieManager = CookieManager.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        Log.i("test","saveFromResponse url="+url.toString()+" url.host="+url.host()+"  cookies.size="+cookies.size()+" cookies="+cookies.get(0).toString());
        //清空缓存 否则再登录登录不上 应该与web那边有关
        if (ApiService.LOGIN_URL.equals(url.toString())){
            cookieManager.removeAllCookie();
            cookieStore.removeAll();
        }

        if (cookies!=null&&cookies.size()>0){
        saveCookies(url.host(),cookies.get(0).toString());
        cookieStore.add(url, cookies);
        }
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        Log.i("test","loadForRequest url="+url.toString());
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            CookieSyncManager.getInstance().sync();
        }

        return cookieStore.get(url);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * 将浏览器cookie 同步到RAM内存中
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void saveCookies(String url, String cookies) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
            cookieManager.flush();
        }else{
            //api 21后被废弃
            cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
            CookieSyncManager.getInstance().sync();
        }

    }
}

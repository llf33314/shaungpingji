package com.gt.doubledisplay.web.store;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.gt.doubledisplay.http.HttpConfig;

import org.xwalk.core.XWalkCookieManager;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {
    private CookieStore cookieStore;
    private Context context;

    XWalkCookieManager mCookieManager = new XWalkCookieManager();

    public CookieJarImpl(CookieStore cookieStore,Context context) {
        if (cookieStore == null) new IllegalArgumentException("cookieStore can not be null.");
        this.cookieStore = cookieStore;
        this.context=context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        Log.i("test","saveFromResponse url="+url.toString()+" url.host="+url.host()+"  cookies.size="+cookies.size()+" cookies="+cookies.get(0).toString());

        if (cookies!=null&&cookies.size()>0) {
            saveCookies(url.toString(),cookies.get(0).toString());
        }
        cookieStore.add(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        Log.i("test","loadForRequest url="+url.toString());
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

        mCookieManager.setAcceptCookie(true);
        mCookieManager.setAcceptFileSchemeCookies(true);
        mCookieManager.setCookie(url,cookies);
        mCookieManager.flushCookieStore();

    }
}

package com.zcool.inkstone.lang;

import com.zcool.inkstone.manager.CookieStoreManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class OkHttp3CookieJar implements CookieJar {

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (url == null) {
            return;
        }

        if (cookies != null && !cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                if (cookie != null) {
                    CookieStoreManager.getInstance().save(url.toString(), Arrays.asList(cookie.toString()));
                }
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookieList = new ArrayList<>();

        if (url == null) {
            return cookieList;
        }

        List<String> cookies = CookieStoreManager.getInstance().matches(url.toString());
        if (cookies != null && !cookies.isEmpty()) {
            for (String cookieString : cookies) {
                Cookie cookie = Cookie.parse(url, cookieString);
                if (cookie != null) {
                    cookieList.add(cookie);
                }
            }
        }

        return cookieList;
    }
}

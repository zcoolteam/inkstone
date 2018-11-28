package com.zcool.inkstone.service;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zcool.inkstone.db.SimpleDB;
import com.zcool.inkstone.lang.Singleton;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.Pair;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import timber.log.Timber;

class CookieStoreServiceProvider {

    private final SimpleDB mStore;

    private static final Singleton<CookieStoreServiceProvider> sInstance =
            new Singleton<CookieStoreServiceProvider>() {
                @Override
                protected CookieStoreServiceProvider create() {
                    return new CookieStoreServiceProvider();
                }
            };

    public static CookieStoreServiceProvider getInstance() {
        return sInstance.get();
    }

    private final Gson mGson = new Gson();
    private final Type mCookieStoreEntityType = new TypeToken<CookieStoreEntity>() {
    }.getType();

    private final Map<String, Pair<CookieStoreEntity, Cookie>> mData = new HashMap<>();

    private CookieStoreServiceProvider() {
        Timber.v("init");
        mStore = new SimpleDB("inkstone_cookie_store");
        mStore.trim(MAX_SIZE);

        Map<String, String> rows = mStore.getAll();
        if (rows != null) {
            for (Map.Entry<String, String> entry : rows.entrySet()) {
                try {
                    CookieStoreEntity entity = mGson.fromJson(entry.getValue(), mCookieStoreEntityType);
                    if (entity == null) {
                        Timber.w("skip null entity %s -> %s", entry.getKey(), entry.getValue());
                        continue;
                    }

                    HttpUrl httpUrl = HttpUrl.parse(entity.url);
                    if (httpUrl == null) {
                        Timber.w("skip null httpUrl %s %s -> %s", entity.url, entry.getKey(), entry.getValue());
                        continue;
                    }

                    Cookie cookie = Cookie.parse(httpUrl, entity.setCookie);
                    if (cookie == null) {
                        Timber.w("skip null cookie %s %s -> %s", entity.setCookie, entry.getKey(), entry.getValue());
                        continue;
                    }
                    if (!ObjectsCompat.equals(entry.getKey(), entity.savedKey)) {
                        Timber.w("skip key not equals %s : %s", entry.getKey(), entity.savedKey);
                        continue;
                    }
                    if (deleteFromDBIfExpires(entity, cookie)) {
                        Timber.v("skip expires cookie %s -> %s", entry.getKey(), entry.getValue());
                        continue;
                    }

                    synchronized (mData) {
                        mData.put(entity.savedKey, new Pair<>(entity, cookie));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(String url, List<String> setCookies) {
        if (TextUtils.isEmpty(url)) {
            Timber.w("ignore save, url is empty");
            return;
        }
        if (setCookies == null || setCookies.isEmpty()) {
            Timber.w("ignore save, setCookies is empty");
            return;
        }

        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            Timber.w("ignore save, fail to parse url %s", url);
            return;
        }

        for (String setCookie : setCookies) {
            try {
                if (TextUtils.isEmpty(setCookie)) {
                    Timber.w("ignore save, setCookie is empty");
                    continue;
                }

                Cookie cookie = Cookie.parse(httpUrl, setCookie);
                if (cookie == null) {
                    Timber.w("ignore save, fail to parse cookie %s -> %s", httpUrl, setCookie);
                    continue;
                }

                CookieStoreEntity entity = CookieStoreEntity.valueOf(url, setCookie, cookie);
                if (deleteFromDBIfExpires(entity, cookie)) {
                    Timber.w("ignore save, cookie expires %s -> %s", httpUrl, setCookie);
                    continue;
                }

                synchronized (mData) {
                    mData.put(entity.savedKey, new Pair<>(entity, cookie));
                    mStore.set(entity.savedKey, mGson.toJson(entity, mCookieStoreEntityType));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> matches(String url) {
        List<String> setCookies = new ArrayList<>();
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            return setCookies;
        }

        synchronized (mData) {
            List<String> removedKeys = new ArrayList<>();
            for (Pair<CookieStoreEntity, Cookie> pair : mData.values()) {
                if (pair == null || pair.first == null || pair.second == null) {
                    continue;
                }
                if (deleteFromDBIfExpires(pair.first, pair.second)) {
                    removedKeys.add(pair.first.savedKey);
                    continue;
                }
                if (pair.second.matches(httpUrl)) {
                    setCookies.add(pair.first.setCookie);
                }
            }
            for (String removedKey : removedKeys) {
                mData.remove(removedKey);
            }
        }
        return setCookies;
    }

    public List<String> get(String url) {
        List<String> setCookies = new ArrayList<>();
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            return setCookies;
        }

        synchronized (mData) {
            List<String> removedKeys = new ArrayList<>();
            for (Pair<CookieStoreEntity, Cookie> pair : mData.values()) {
                if (pair == null || pair.first == null || pair.second == null) {
                    continue;
                }
                if (deleteFromDBIfExpires(pair.first, pair.second)) {
                    removedKeys.add(pair.first.savedKey);
                    continue;
                }
                if (url.equals(pair.first.url)) {
                    setCookies.add(pair.first.setCookie);
                }
            }
            for (String removedKey : removedKeys) {
                mData.remove(removedKey);
            }
        }
        return setCookies;
    }

    public List<String> getUrls() {
        List<String> urls = new ArrayList<>();
        synchronized (mData) {
            List<String> removedKeys = new ArrayList<>();
            for (Pair<CookieStoreEntity, Cookie> pair : mData.values()) {
                if (pair == null || pair.first == null || pair.second == null) {
                    continue;
                }
                if (deleteFromDBIfExpires(pair.first, pair.second)) {
                    removedKeys.add(pair.first.savedKey);
                    continue;
                }
                urls.add(pair.first.url);
            }
            for (String removedKey : removedKeys) {
                mData.remove(removedKey);
            }
        }
        return urls;
    }

    public void clear() {
        synchronized (mData) {
            mData.clear();
            mStore.clear();
        }
    }

    public void clearSession() {
        synchronized (mData) {
            List<String> removedKeys = new ArrayList<>();
            for (Pair<CookieStoreEntity, Cookie> pair : mData.values()) {
                if (pair == null || pair.first == null || pair.second == null) {
                    continue;
                }
                if (deleteFromDBIfExpires(pair.first, pair.second)
                        || deleteFromDBIfSession(pair.first, pair.second)) {
                    removedKeys.add(pair.first.savedKey);
                }
            }
            for (String removedKey : removedKeys) {
                mData.remove(removedKey);
            }
        }
    }

    public void printAll() {
        mStore.printAllRows();
    }

    private boolean deleteFromDBIfExpires(CookieStoreEntity entity, Cookie cookie) {
        if (cookie.expiresAt() < System.currentTimeMillis()) {
            Timber.v("delete expires cookie %s -> %s", entity.savedKey, entity.setCookie);
            mStore.remove(entity.savedKey);
            return true;
        }
        return false;
    }

    private boolean deleteFromDBIfSession(CookieStoreEntity entity, Cookie cookie) {
        if (!cookie.persistent()) {
            Timber.v("delete session cookie %s -> %s", entity.savedKey, entity.setCookie);
            mStore.remove(entity.savedKey);
            return true;
        }
        return false;
    }

    /**
     * 存储的 cookie 条数的上限
     */
    private static final int MAX_SIZE = 5000;

    @Keep
    public static class CookieStoreEntity {

        public String url;
        public String setCookie;
        public String savedKey;

        @NonNull
        public static CookieStoreEntity valueOf(String url, String setCookie, Cookie cookie) {
            CookieStoreEntity entity = new CookieStoreEntity();
            entity.url = url;
            entity.setCookie = setCookie;
            entity.savedKey = cookie.domain() + "|" + cookie.path() + "|" + cookie.name();
            return entity;
        }

    }

}

package com.zcool.inkstone.manager;

import android.os.RemoteException;

import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.service.BaseServicesProvider;
import com.zcool.inkstone.service.ICookieStoreService;

import java.util.List;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * 支持跨进程
 */
public class CookieStoreManager {

    private static final Singleton<CookieStoreManager> sInstance =
            new Singleton<CookieStoreManager>() {
                @Override
                protected CookieStoreManager create() {
                    return new CookieStoreManager();
                }
            };

    public static CookieStoreManager getInstance() {
        return sInstance.get();
    }

    private CookieStoreManager() {
        Timber.v("init");
    }

    public void save(String url, List<String> setCookies) {
        try {
            getService().save(url, setCookies);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public List<String> matches(String url) {
        try {
            return getService().matches(url);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public List<String> get(String url) {
        try {
            return getService().get(url);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public List<String> getUrls() {
        try {
            return getService().getUrls();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear() {
        try {
            getService().clear();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void clearSession() {
        try {
            getService().clearSession();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void printAll() {
        try {
            getService().printAll();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private ICookieStoreService getService() throws RemoteException {
        return ICookieStoreService.Stub.asInterface(
                ServiceManager.getInstance().fetchService(BaseServicesProvider.SERVICE_COOKIE_STORE));
    }

}

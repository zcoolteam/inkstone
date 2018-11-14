package com.zcool.inkstone.manager;

import android.os.RemoteException;

import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.service.BaseServicesProvider;
import com.zcool.inkstone.service.IStorageService;

import timber.log.Timber;

/**
 * 支持跨进程
 */
public class StorageManager {

    public static final String NAMESPACE_SETTING = "inkstone_setting";
    public static final String NAMESPACE_CACHE = "inkstone_cache";

    private static final Singleton<StorageManager> sInstance =
            new Singleton<StorageManager>() {
                @Override
                protected StorageManager create() {
                    return new StorageManager();
                }
            };

    public static StorageManager getInstance() {
        return sInstance.get();
    }

    private StorageManager() {
        Timber.v("init");
    }

    public void set(String namespace, String key, String value) {
        try {
            getService().set(namespace, key, value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String get(String namespace, String key) {
        try {
            return getService().get(namespace, key);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOrSetLock(String namespace, String key, String setValue) {
        try {
            return getService().getOrSetLock(namespace, key, setValue);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void printAllRows(String namespace) {
        try {
            getService().printAllRows(namespace);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private IStorageService getService() throws RemoteException {
        return IStorageService.Stub.asInterface(
                ServiceManager.getInstance().fetchService(BaseServicesProvider.SERVICE_STORAGE));
    }

}

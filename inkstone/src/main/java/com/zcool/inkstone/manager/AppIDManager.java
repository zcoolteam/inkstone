package com.zcool.inkstone.manager;

import com.zcool.inkstone.lang.Singleton;

import java.util.UUID;

import timber.log.Timber;

/**
 * 支持跨进程
 */
public class AppIDManager {

    private static final Singleton<AppIDManager> sInstance =
            new Singleton<AppIDManager>() {
                @Override
                protected AppIDManager create() {
                    return new AppIDManager();
                }
            };

    public static AppIDManager getInstance() {
        return sInstance.get();
    }

    private static final String KEY_APP_ID = "inkstone_app_id";
    private String mAppID;

    private AppIDManager() {
        Timber.v("init");
        mAppID = StorageManager.getInstance().getOrSetLock(
                StorageManager.NAMESPACE_SETTING,
                KEY_APP_ID,
                UUID.randomUUID().toString());

        Timber.d("AppID=%s", mAppID);
    }

    public String getAppID() {
        return mAppID;
    }

}

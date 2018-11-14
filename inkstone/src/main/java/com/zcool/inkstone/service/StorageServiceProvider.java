package com.zcool.inkstone.service;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zcool.inkstone.db.SimpleDB;
import com.zcool.inkstone.lang.Singleton;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

class StorageServiceProvider {

    private final Map<String, SimpleDB> mProviders = new HashMap<>();

    private static final Singleton<StorageServiceProvider> sInstance =
            new Singleton<StorageServiceProvider>() {
                @Override
                protected StorageServiceProvider create() {
                    return new StorageServiceProvider();
                }
            };

    public static StorageServiceProvider getInstance() {
        return sInstance.get();
    }

    private final Object mGetOrSetLock = new Object();

    private StorageServiceProvider() {
        Timber.v("init");
    }

    public void set(String namespace, String key, String value) {
        getTarget(namespace).set(key, value);
    }

    public String get(String namespace, String key) {
        return getTarget(namespace).get(key);
    }

    public String getOrSetLock(String namespace, String key, String setValue) {
        String value;
        SimpleDB target = getTarget(namespace);
        synchronized (mGetOrSetLock) {
            value = target.get(key);
            if (TextUtils.isEmpty(value)) {
                value = setValue;
                target.set(key, value);
            }
        }
        return value;
    }

    public void printAllRows(String namespace) {
        getTarget(namespace).printAllRows();
    }

    @NonNull
    private SimpleDB getTarget(String namespace) {
        namespace = checkNamespace(namespace);

        SimpleDB db;
        boolean trim = false;
        synchronized (mProviders) {
            db = mProviders.get(namespace);
            if (db == null) {
                db = new SimpleDB(namespace);
                mProviders.put(namespace, db);
                trim = true;
            }
        }
        if (trim) {
            db.trim(MAX_ROWS);
        }
        return db;
    }

    private static String checkNamespace(String namespace) {
        if (TextUtils.isEmpty(namespace)) {
            throw new IllegalArgumentException("need namespace, like StorageManager#NAMESPACE_*");
        }
        return namespace;
    }

    private static final int MAX_ROWS = 5000;
}

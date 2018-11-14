package com.zcool.inkstone.lang;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zcool.inkstone.manager.StorageManager;
import com.zcool.inkstone.thread.TaskQueue;

import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CacheDataHelper<T> {

    private final String mCacheKey;

    @Nullable
    private T mData;

    public CacheDataHelper(String cacheKey, Type type) {
        mCacheKey = cacheKey;

        mData = readCacheData(type);
    }

    @Nullable
    public T getData() {
        return mData;
    }

    @Nullable
    private T readCacheData(Type type) {
        try {
            String json = StorageManager.getInstance()
                    .get(StorageManager.NAMESPACE_SETTING, mCacheKey);
            if (TextUtils.isEmpty(json)) {
                return null;
            }
            return new Gson().fromJson(json, type);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setData(@Nullable final T data) {
        mData = data;
        mSaveQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                try {
                    String json;
                    if (data == null) {
                        json = null;
                    } else {
                        json = new Gson().toJson(data);
                    }
                    StorageManager.getInstance()
                            .set(StorageManager.NAMESPACE_SETTING, mCacheKey, json);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveOnly(@Nullable final T cloneData) {
        mSaveQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                try {
                    T data = cloneData;
                    String json;
                    if (data == null) {
                        json = null;
                    } else {
                        json = new Gson().toJson(data);
                    }
                    StorageManager.getInstance()
                            .set(StorageManager.NAMESPACE_SETTING, mCacheKey, json);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void runSyncTask(@NonNull final Callable<T> callable) {
        mSyncHolder.set(Single.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        new Consumer<T>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull T data)
                                    throws Exception {
                                if (data != null) {
                                    setData(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Throwable e) throws Exception {
                                // ignore
                            }
                        }));
    }

    protected final TaskQueue mSaveQueue = new TaskQueue(1);
    protected final DisposableHolder mSyncHolder = new DisposableHolder();

}

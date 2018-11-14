package com.zcool.inkstone.lang;

/**
 * copy from android.util.Singleton 支持延迟初始化
 */
public abstract class Singleton<T> {
    private T mInstance;

    protected abstract T create();

    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }
}

package com.zcool.inkstone.lang;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * @return true, if the reference object is null.
 * or check the object#isAbort if it's instance of AbortSignal
 */
public class WeakAbortSignal extends SimpleAbortSignal {

    private final WeakReference<Object> mWeakReference;

    public WeakAbortSignal(@Nullable Object object) {
        mWeakReference = new WeakReference<>(object);
    }

    public Object getObject() {
        return mWeakReference.get();
    }

    @Override
    public boolean isAbort() {
        if (super.isAbort()) {
            return true;
        }

        Object object = mWeakReference.get();
        if (object == null) {
            setAbort();
            return true;
        }

        if (object instanceof AbortSignal) {
            AbortSignal abortSignal = (AbortSignal) object;
            if (abortSignal.isAbort()) {
                setAbort();
                return true;
            }
        }

        return false;
    }

}

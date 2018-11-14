package com.zcool.inkstone.util;

import android.support.annotation.Nullable;

import com.zcool.inkstone.lang.AbortException;
import com.zcool.inkstone.lang.AbortSignal;

public class AbortUtil {

    /**
     * @param abortSignal
     * @return true if abortSignal is not null and isAbort return true, otherwise false.
     */
    public static boolean isAbort(@Nullable AbortSignal abortSignal) {
        if (abortSignal == null) {
            return false;
        }
        return abortSignal.isAbort();
    }

    /**
     * @see #isAbort(AbortSignal)
     */
    public static void throwIfAbort(@Nullable AbortSignal abortSignal) {
        if (isAbort(abortSignal)) {
            throw new AbortException();
        }
    }

}

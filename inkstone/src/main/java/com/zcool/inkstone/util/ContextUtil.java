package com.zcool.inkstone.util;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

public class ContextUtil {

    private static Context sContext;

    private ContextUtil() {
    }

    @NonNull
    public static Context getContext() {
        if (sContext == null) {
            throw new IllegalAccessError(
                    "context not found, @see InkstoneDelegate#init(Context)");
        }
        return sContext;
    }

    /**
     * do not call this method direct, and use InkstoneDelegate.init(Context) instead.
     */
    public static synchronized void setContext(@NonNull Context context) {
        if (sContext != null) {
            throw new IllegalAccessError("context already set");
        }

        if (context instanceof Application) {
            sContext = context;
            return;
        }

        Context originalContext = context;

        context = context.getApplicationContext();

        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("application not found " + originalContext);
        }
        sContext = context;
    }
}

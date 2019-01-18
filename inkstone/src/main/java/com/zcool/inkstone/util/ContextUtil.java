package com.zcool.inkstone.util;

import android.content.Context;

import com.zcool.inkstone.Inkstone;

import androidx.annotation.NonNull;

public class ContextUtil {

    private ContextUtil() {
    }

    @NonNull
    public static Context getContext() {
        return Inkstone.getApplication();
    }

}

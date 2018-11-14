package com.zcool.inkstone.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.Nullable;

import com.zcool.inkstone.lang.AbortSignal;
import com.zcool.inkstone.lang.Progress;

import java.io.InputStream;
import java.util.List;

/**
 * helper for read asset content
 */
public class AssetUtil {

    public static String readAllAsString(
            String path,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        Context context = ContextUtil.getContext();
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(path);
            return IOUtil.readAllAsString(is, abortSignal, progress);
        } finally {
            IOUtil.closeQuietly(is);
        }
    }

    public static byte[] readAll(
            String path,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        Context context = ContextUtil.getContext();
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(path);
            return IOUtil.readAll(is, abortSignal, progress);
        } finally {
            IOUtil.closeQuietly(is);
        }
    }

    public static List<String> readAllLines(
            String path,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        Context context = ContextUtil.getContext();
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(path);
            return IOUtil.readAllLines(is, abortSignal, progress);
        } finally {
            IOUtil.closeQuietly(is);
        }
    }
}

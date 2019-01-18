package com.zcool.inkstone;

import android.app.Application;
import android.content.Context;
import android.os.IBinder;

import com.zcool.inkstone.annotation.Config;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Inkstone {

    private Inkstone() {
    }

    public static void init(Context context) {
        InkstoneDelegate.getInstance().init(context);
    }

    public static void checkInit() {
        InkstoneDelegate.getInstance().checkInit();
    }

    public static Application getApplication() {
        return InkstoneDelegate.getInstance().getApplication();
    }

    @NonNull
    public static List<Config.ServicesProvider> getSortServicesProviderConfig() {
        return InkstoneDelegate.getInstance().getSortServicesProviderConfig();
    }

    public static boolean isDebug() {
        return InkstoneDelegate.getInstance().getAttr().isDebug();
    }

    public static boolean isDebugHttpBody() {
        return InkstoneDelegate.getInstance().getAttr().isDebugHttpBody();
    }

    public static boolean isDebugWidget() {
        return InkstoneDelegate.getInstance().getAttr().isDebugWidget();
    }

    @NonNull
    public static AppCallbacks getAppCallbacks() {
        return InkstoneDelegate.getInstance().getAppCallbacks();
    }

    public static void setDefaultUserAgent(@Nullable String userAgent) {
        InkstoneDelegate.getInstance().getAttr().setDefaultUserAgent(userAgent);
    }

    @Nullable
    public static String getDefaultUserAgent() {
        return InkstoneDelegate.getInstance().getAttr().getDefaultUserAgent();
    }

    public static String getMediaDirName() {
        return InkstoneDelegate.getInstance().getAttr().getMediaDirName();
    }

    public interface ServiceFetcher<T> {
        T getService();
    }

    public static abstract class StaticServiceFetcher<T> implements ServiceFetcher<T> {
        private T mCachedInstance;

        @Override
        public final T getService() {
            synchronized (StaticServiceFetcher.this) {
                if (mCachedInstance == null) {
                    mCachedInstance = createService();
                }
                return mCachedInstance;
            }
        }

        public abstract T createService();
    }

    public interface ServicesProviderHost {
        void addService(String name, ServiceFetcher<IBinder> serviceFetcher);
    }

}

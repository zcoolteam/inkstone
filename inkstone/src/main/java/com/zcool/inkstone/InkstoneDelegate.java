package com.zcool.inkstone;

import android.app.Activity;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.zcool.inkstone.annotation.Config;
import com.zcool.inkstone.annotation.ModuleConfig;
import com.zcool.inkstone.lang.NotInitException;
import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.manager.FrescoManager;
import com.zcool.inkstone.util.ContextUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

@Keep
public final class InkstoneDelegate {

    private static final Singleton<InkstoneDelegate> INSTANCE = new Singleton<InkstoneDelegate>() {
        @Override
        protected InkstoneDelegate create() {
            return new InkstoneDelegate();
        }
    };

    private static boolean sInit;

    public synchronized static void init(Context context) {
        if (sInit) {
            return;
        }
        sInit = true;

        // set global context first
        ContextUtil.setContext(context);
        context = ContextUtil.getContext();

        // config RxJava2
        RxJavaPlugins.setErrorHandler(e -> {
            // ignore
        });

        getInstance().onCreate(context);
    }

    static InkstoneDelegate getInstance() {
        throwIfNotInit();
        return INSTANCE.get();
    }

    @NonNull
    private final ModuleConfig mAppConfig;
    @NonNull
    private final List<Config.ApplicationDelegate> mSortApplicationDelegateConfig;
    @NonNull
    private final List<Config.ServicesProvider> mSortServicesProviderConfig;
    @NonNull
    private final List<ModuleApplicationDelegate> mModuleApplicationDelegates;

    private InkstoneDelegate() {
        final String className = "com.zcool.inkstone.InkstoneAppConfigImpl";
        try {
            mAppConfig = (ModuleConfig) Class.forName(className).newInstance();
            Preconditions.checkNotNull(mAppConfig, "mAppConfig can not be null");

            List<Config.ApplicationDelegate> sortApplicationDelegateConfig = new ArrayList<>(
                    mAppConfig.getConfig().getApplicationDelegates());
            Collections.sort(sortApplicationDelegateConfig, (left, right) -> left.priority - right.priority);
            mSortApplicationDelegateConfig = sortApplicationDelegateConfig;

            List<Config.ServicesProvider> sortServicesProviderConfig = new ArrayList<>(
                    mAppConfig.getConfig().getServicesProviders());
            Collections.sort(sortServicesProviderConfig, (left, right) -> left.priority - right.priority);
            mSortServicesProviderConfig = sortServicesProviderConfig;
        } catch (Throwable e) {
            throw new RuntimeException("fail to create InkstoneDelegate", e);
        }

        mModuleApplicationDelegates = new ArrayList<>();
        try {
            for (Config.ApplicationDelegate item : mSortApplicationDelegateConfig) {
                mModuleApplicationDelegates.add((ModuleApplicationDelegate) Class.forName(item.clazz).newInstance());
            }
        } catch (Throwable e) {
            throw new RuntimeException("fail to instance module application delegates", e);
        }
    }

    @NonNull
    public List<Config.ApplicationDelegate> getSortApplicationDelegateConfig() {
        return mSortApplicationDelegateConfig;
    }

    @NonNull
    public List<Config.ServicesProvider> getSortServicesProviderConfig() {
        return mSortServicesProviderConfig;
    }

    /**
     * Android 8.0+ 系统权限控制，不允许在 APP 后台运行的情况下启动 service
     */
    private void onStartBackgroundService() {
        throwIfNotInit();

        for (ModuleApplicationDelegate item : mModuleApplicationDelegates) {
            item.onStartBackgroundService();
        }
    }

    private synchronized static void throwIfNotInit() {
        if (!sInit) {
            throw new NotInitException();
        }
    }

    private AppCallbacks mAppCallbacks;
    private String mDefaultUserAgent;

    @CallSuper
    public void onCreate(Context context) {
        throwIfNotInit();

        if (Debug.isDebug()) {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.v(new Throwable("[only print InkstoneDelegate#onCreate call stack]"));

        mAppCallbacks = new AppCallbacks();
        mAppCallbacks.addApplicationCallbacks(mServiceStarterCallback);

        // init builtin
        FrescoManager.getInstance();

        for (ModuleApplicationDelegate item : mModuleApplicationDelegates) {
            item.onCreate(context);
        }
    }

    private final AppCallbacks.SimpleApplicationCallbacks mServiceStarterCallback = new AppCallbacks.SimpleApplicationCallbacks() {

        private boolean mServiceStarted;

        @Override
        public void onActivityResumed(Activity activity) {
            super.onActivityResumed(activity);

            if (!mServiceStarted) {
                mServiceStarted = true;
                InkstoneDelegate.getInstance().onStartBackgroundService();
            }
        }
    };

    public boolean isDebug() {
        return ContextUtil.getContext().getResources().getBoolean(R.bool.inkstone_debug);
    }

    public boolean isDebugHttpBody() {
        return ContextUtil.getContext().getResources().getBoolean(R.bool.inkstone_debug_http_body);
    }

    public boolean isDebugWidget() {
        return ContextUtil.getContext().getResources().getBoolean(R.bool.inkstone_debug_widget);
    }

    public AppCallbacks getAppCallbacks() {
        return mAppCallbacks;
    }

    public void setDefaultUserAgent(@Nullable String userAgent) {
        mDefaultUserAgent = userAgent;
    }

    @Nullable
    public String getDefaultUserAgent() {
        return mDefaultUserAgent;
    }

    public String getMediaDirName() {
        return ContextUtil.getContext().getResources().getString(ContextUtil.getContext().getApplicationInfo().labelRes);
    }

}

package com.zcool.inkstone;

import android.app.Activity;
import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.zcool.inkstone.annotation.Config;
import com.zcool.inkstone.annotation.ModuleConfig;
import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.util.ContextUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

final class InkstoneDelegate {

    private static final Singleton<InkstoneDelegate> INSTANCE = new Singleton<InkstoneDelegate>() {
        @Override
        protected InkstoneDelegate create() {
            return new InkstoneDelegate();
        }
    };

    public static InkstoneDelegate getInstance() {
        return INSTANCE.get();
    }

    private boolean mInit;
    private Application mApplication;
    private Attr mAttr;

    private AppCallbacks mAppCallbacks;

    private ModuleConfig mAppConfig;
    private List<Config.ApplicationDelegate> mSortApplicationDelegateConfig;
    private List<Config.ServicesProvider> mSortServicesProviderConfig;
    private List<ModuleApplicationDelegate> mModuleApplicationDelegates;

    private InkstoneDelegate() {
    }

    synchronized void init(@NonNull Context context) {
        if (mInit) {
            return;
        }

        Application application;
        if (context instanceof Application) {
            application = (Application) context;
        } else {
            application = (Application) context.getApplicationContext();
        }

        if (application == null) {
            Timber.e("application not found from Context %s", context);
            new RuntimeException("application not found, invalid context " + context).printStackTrace();
            return;
        }

        mInit = true;

        mApplication = application;
        mAttr = new Attr();

        // config RxJava2
        RxJavaPlugins.setErrorHandler(e -> {
            Timber.w(e, "RxJavaPlugins#errorHandler");
            e.printStackTrace();
        });

        if (Debug.isDebug()) {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.v(new Throwable("[only print call stack]"));

        mAppCallbacks = new AppCallbacks();
        mAppCallbacks.addApplicationCallbacks(mServiceStarterCallback);

        loadConfig();
        onCreate(context);
    }

    synchronized void checkInit() {
        if (!mInit) {
            Context context = ActivityThread.currentApplication();
            if (context != null) {
                init(context);
                return;
            }
            Timber.e("auto init fail, ActivityThread.currentApplication() return null");
        }

        if (!mInit) {
            throw new RuntimeException("inkstone not init, try manual init with Inkstone.init(Context)");
        }
    }

    private void loadConfig() {
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

            // all process need module application delegates class instance
            mModuleApplicationDelegates = new ArrayList<>();
            for (Config.ApplicationDelegate item : mSortApplicationDelegateConfig) {
                mModuleApplicationDelegates.add((ModuleApplicationDelegate) item.clazz.newInstance());
            }
        } catch (Throwable e) {
            throw new RuntimeException("loadConfig fail", e);
        }
    }

    private void onCreate(Context context) {
        checkInit();

        for (ModuleApplicationDelegate item : mModuleApplicationDelegates) {
            try {
                item.onCreate(context);
            } catch (Throwable e) {
                e.printStackTrace();
                Timber.e(e);
            }
        }
    }

    /**
     * Android 8.0+ 系统权限控制，不允许在 APP 后台运行的情况下启动 service
     */
    private void onStartBackgroundService() {
        checkInit();

        for (ModuleApplicationDelegate item : mModuleApplicationDelegates) {
            try {
                item.onStartBackgroundService();
            } catch (Throwable e) {
                e.printStackTrace();
                Timber.e(e);
            }
        }
    }

    private final AppCallbacks.SimpleApplicationCallbacks mServiceStarterCallback = new AppCallbacks.SimpleApplicationCallbacks() {

        private boolean mServiceStarted;

        @Override
        public void onActivityResumed(Activity activity) {
            super.onActivityResumed(activity);

            synchronized (this) {
                if (!mServiceStarted) {
                    mServiceStarted = true;

                    InkstoneDelegate.this.onStartBackgroundService();
                }
            }
        }
    };

    @NonNull
    public Application getApplication() {
        checkInit();

        Preconditions.checkNotNull(mApplication);
        return mApplication;
    }

    @NonNull
    public List<Config.ApplicationDelegate> getSortApplicationDelegateConfig() {
        checkInit();

        Preconditions.checkNotNull(mSortApplicationDelegateConfig);
        return mSortApplicationDelegateConfig;
    }

    @NonNull
    public List<Config.ServicesProvider> getSortServicesProviderConfig() {
        checkInit();

        Preconditions.checkNotNull(mSortServicesProviderConfig);
        return mSortServicesProviderConfig;
    }

    @NonNull
    public AppCallbacks getAppCallbacks() {
        checkInit();

        Preconditions.checkNotNull(mAppCallbacks);
        return mAppCallbacks;
    }

    @NonNull
    public Attr getAttr() {
        checkInit();

        Preconditions.checkNotNull(mAttr);
        return mAttr;
    }

    public static class Attr {

        private String mDefaultUserAgent;

        public String getMediaDirName() {
            return ContextUtil.getContext().getResources().getString(ContextUtil.getContext().getApplicationInfo().labelRes);
        }

        public void setDefaultUserAgent(@Nullable String userAgent) {
            mDefaultUserAgent = userAgent;
        }

        @Nullable
        public String getDefaultUserAgent() {
            return mDefaultUserAgent;
        }

        public boolean isDebug() {
            return ContextUtil.getContext().getResources().getBoolean(R.bool.inkstone_debug);
        }

        public boolean isDebugHttpBody() {
            return ContextUtil.getContext().getResources().getBoolean(R.bool.inkstone_debug_http_body);
        }

        public boolean isDebugWidget() {
            return ContextUtil.getContext().getResources().getBoolean(R.bool.inkstone_debug_widget);
        }

    }

}

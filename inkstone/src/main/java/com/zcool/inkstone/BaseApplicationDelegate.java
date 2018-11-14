package com.zcool.inkstone;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zcool.inkstone.lang.NotInitException;
import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.manager.FrescoManager;
import com.zcool.inkstone.service.InkstoneService;
import com.zcool.inkstone.util.ContextUtil;

import timber.log.Timber;

@Keep
public class BaseApplicationDelegate {

    private static final String LOG_TAG = "BaseApplicationDelegate";

    private static final Singleton<BaseApplicationDelegate> INSTANCE = new Singleton<BaseApplicationDelegate>() {
        @Override
        protected BaseApplicationDelegate create() {
            final String className = "com.zcool.inkstone.ApplicationDelegateInstance";

            BaseApplicationDelegate target;
            try {
                target = (BaseApplicationDelegate) Class.forName(className).getDeclaredMethod("get").invoke(null);
                if (target == null) {
                    throw new NullPointerException(className + " static method get return null");
                }
                Log.v(LOG_TAG, "init BaseApplicationDelegate with " + target.getClass().getName());
            } catch (Throwable e) {
                throw new RuntimeException("init BaseApplicationDelegate fail", e);
            }
            return target;
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

        getInstance().onCreate(context);
    }

    public static BaseApplicationDelegate getInstance() {
        throwIfNotInit();
        return INSTANCE.get();
    }

    private static boolean sCallConstructor;

    protected BaseApplicationDelegate() {
        if (sCallConstructor) {
            throw new IllegalStateException("can not create more than one BaseApplicationDelegate instance");
        }
        sCallConstructor = true;
    }

    /**
     * Android 8.0+ 系统权限控制，不允许在 APP 后台运行的情况下启动 service
     */
    @CallSuper
    public void onStartBackgroundService() {
        throwIfNotInit();

        InkstoneService.start(ContextUtil.getContext());
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

        if (isDebug()) {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.v(new Throwable("[only print BaseApplicationDelegate#onCreate call stack]"));

        mAppCallbacks = new AppCallbacks();
        mAppCallbacks.addApplicationCallbacks(mServiceStarterCallback);

        // init builtin
        FrescoManager.getInstance();
    }

    private final AppCallbacks.SimpleApplicationCallbacks mServiceStarterCallback = new AppCallbacks.SimpleApplicationCallbacks() {

        private boolean mServiceStarted;

        @Override
        public void onActivityResumed(Activity activity) {
            super.onActivityResumed(activity);

            if (!mServiceStarted) {
                mServiceStarted = true;
                BaseApplicationDelegate.getInstance().onStartBackgroundService();
            }
        }
    };

    public boolean isDebug() {
        return ContextUtil.getContext().getResources().getBoolean(R.bool.inkstone_debug);
    }

    public boolean isDebugHttpBody() {
        return ContextUtil.getContext().getResources().getBoolean(R.bool.inkstone_debug_http_body);
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

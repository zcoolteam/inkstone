package com.zcool.inkstone;

import android.app.Activity;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.zcool.inkstone.lang.NotInitException;
import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.manager.FrescoManager;
import com.zcool.inkstone.service.InkstoneService;
import com.zcool.inkstone.util.ContextUtil;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

@Keep
public class ApplicationDelegateRoot {

    private static final Singleton<ApplicationDelegateRoot> INSTANCE = new Singleton<ApplicationDelegateRoot>() {
        @Override
        protected ApplicationDelegateRoot create() {
            return new ApplicationDelegateRoot();
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

    public static ApplicationDelegateRoot getInstance() {
        throwIfNotInit();
        return INSTANCE.get();
    }

    private static boolean sCallConstructor;

    @NonNull
    private final List<SubApplicationDelegate> mSubApplicationDelegates;

    private ApplicationDelegateRoot() {
        if (sCallConstructor) {
            throw new IllegalStateException("can not create more than one ApplicationDelegateRoot instance");
        }
        sCallConstructor = true;

        final String className = "com.zcool.inkstone.SubApplicationDelegateGroup";
        try {
            mSubApplicationDelegates = (List<SubApplicationDelegate>) Class.forName(className).getDeclaredMethod("get").invoke(null);
            Preconditions.checkNotNull(mSubApplicationDelegates, "mSubApplicationDelegates can not be null");
        } catch (Throwable e) {
            throw new RuntimeException("fail to create ApplicationDelegateRoot", e);
        }

    }

    /**
     * Android 8.0+ 系统权限控制，不允许在 APP 后台运行的情况下启动 service
     */
    @CallSuper
    public void onStartBackgroundService() {
        throwIfNotInit();

        InkstoneService.start(ContextUtil.getContext());

        for (SubApplicationDelegate item : mSubApplicationDelegates) {
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

        Timber.v(new Throwable("[only print ApplicationDelegateRoot#onCreate call stack]"));

        mAppCallbacks = new AppCallbacks();
        mAppCallbacks.addApplicationCallbacks(mServiceStarterCallback);

        // init builtin
        FrescoManager.getInstance();

        for (SubApplicationDelegate item : mSubApplicationDelegates) {
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
                ApplicationDelegateRoot.getInstance().onStartBackgroundService();
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

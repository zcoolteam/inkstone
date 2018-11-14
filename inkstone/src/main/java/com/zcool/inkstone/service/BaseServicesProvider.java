package com.zcool.inkstone.service;

import android.os.IBinder;
import android.support.annotation.CallSuper;

import com.zcool.inkstone.lang.Singleton;

import java.util.HashMap;

import timber.log.Timber;

public class BaseServicesProvider {

    private final HashMap<String, ServiceFetcher<IBinder>> CORE_SERVICE_FETCHERS = new HashMap<>();

    public static final String SERVICE_STORAGE = "storage";
    public static final String SERVICE_COOKIE_STORE = "cookie_store";

    private static boolean sCallConstructor;

    protected BaseServicesProvider() {
        if (sCallConstructor) {
            throw new IllegalStateException("can not create more than one BaseServicesProvider instance");
        }
        sCallConstructor = true;
    }

    @CallSuper
    protected void onCreate() {
        addService(SERVICE_STORAGE, new StaticServiceFetcher<IBinder>() {
            @Override
            public IBinder createService() {
                return new StorageService();
            }
        });
        addService(SERVICE_COOKIE_STORE, new StaticServiceFetcher<IBinder>() {
            @Override
            public IBinder createService() {
                return new CookieStoreService();
            }
        });
    }

    protected final void addService(String name, ServiceFetcher<IBinder> serviceFetcher) {
        synchronized (CORE_SERVICE_FETCHERS) {
            CORE_SERVICE_FETCHERS.put(name, serviceFetcher);
        }
    }

    protected final IBinder getService(String name) {
        ServiceFetcher<IBinder> fetcher;
        synchronized (CORE_SERVICE_FETCHERS) {
            fetcher = CORE_SERVICE_FETCHERS.get(name);
        }
        if (fetcher == null) {
            throw new IllegalArgumentException("service not found " + name);
        }
        IBinder service = fetcher.getService();
        if (service == null) {
            throw new IllegalStateException("fetch service fail " + name + " " + fetcher);
        }
        return service;
    }

    protected interface ServiceFetcher<T> {
        T getService();
    }

    protected static abstract class StaticServiceFetcher<T> implements ServiceFetcher<T> {
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

    private static final Singleton<BaseServicesProvider> INSTANCE = new Singleton<BaseServicesProvider>() {
        @Override
        protected BaseServicesProvider create() {
            final String className = "com.zcool.inkstone.service.ServicesProviderInstance";

            BaseServicesProvider target;
            try {
                target = (BaseServicesProvider) Class.forName(className).getDeclaredMethod("get").invoke(null);
                if (target == null) {
                    throw new NullPointerException(className + " static method get return null");
                }
                Timber.v("init BaseServicesProvider with " + target.getClass().getName());
                target.onCreate();
            } catch (Throwable e) {
                throw new RuntimeException("init BaseServicesProvider fail", e);
            }
            return target;
        }
    };

    static BaseServicesProvider getInstance() {
        return INSTANCE.get();
    }

}

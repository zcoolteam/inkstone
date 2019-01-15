package com.zcool.inkstone.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.zcool.inkstone.Inkstone;
import com.zcool.inkstone.ModuleServicesProvider;
import com.zcool.inkstone.annotation.Config;
import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.util.ContextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.BundleCompat;

public class DefaultServiceContentProvider extends ContentProvider {

    public static final String FETCH_SERVICE = "fetch_service";

    @Override
    public boolean onCreate() {
        Inkstone.init(getContext());
        return true;
    }

    public static Uri getContentUri() {
        return Uri.parse("content://" + "inkstone." + ContextUtil.getContext().getPackageName() + ".DefaultServiceContentProvider");
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        if (FETCH_SERVICE.equals(method)) {
            Bundle ret = new Bundle();
            BundleCompat.putBinder(ret, FETCH_SERVICE, new ICoreService.Stub() {
                @Override
                public IBinder getService(String serviceName) throws RemoteException {
                    return ServicesProviderHostImpl.getInstance().getService(serviceName);
                }
            });
            return ret;
        }
        return super.call(method, arg, extras);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private static class ServicesProviderHostImpl implements Inkstone.ServicesProviderHost {

        private static final Singleton<ServicesProviderHostImpl> INSTANCE = new Singleton<ServicesProviderHostImpl>() {
            @Override
            protected ServicesProviderHostImpl create() {
                return new ServicesProviderHostImpl();
            }
        };

        private static ServicesProviderHostImpl getInstance() {
            return INSTANCE.get();
        }

        @NonNull
        private final HashMap<String, Inkstone.ServiceFetcher<IBinder>> mServiceFetchers = new HashMap<>();
        @NonNull
        private final List<ModuleServicesProvider> mModuleServicesProviders;

        private ServicesProviderHostImpl() {
            List<Config.ServicesProvider> sortServicesProviderConfig = Inkstone.getSortServicesProviderConfig();

            mModuleServicesProviders = new ArrayList<>();
            try {
                for (Config.ServicesProvider item : sortServicesProviderConfig) {
                    mModuleServicesProviders.add((ModuleServicesProvider) Class.forName(item.clazz).newInstance());
                }
            } catch (Throwable e) {
                throw new RuntimeException("fail to instance module application delegates", e);
            }
            for (ModuleServicesProvider item : mModuleServicesProviders) {
                item.onCreate(this);
            }
        }

        private IBinder getService(String name) {
            Inkstone.ServiceFetcher<IBinder> fetcher;
            synchronized (mServiceFetchers) {
                fetcher = mServiceFetchers.get(name);
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

        @Override
        public void addService(String name, Inkstone.ServiceFetcher<IBinder> serviceFetcher) {
            synchronized (mServiceFetchers) {
                mServiceFetchers.put(name, serviceFetcher);
            }
        }
    }

}

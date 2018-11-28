package com.zcool.inkstone.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.service.DefaultServiceContentProvider;
import com.zcool.inkstone.service.ICoreService;
import com.zcool.inkstone.util.ContextUtil;

import androidx.annotation.NonNull;
import androidx.core.app.BundleCompat;
import timber.log.Timber;

/**
 * 支持跨进程
 */
public class ServiceManager {

    private static final Singleton<ServiceManager> sInstance =
            new Singleton<ServiceManager>() {
                @Override
                protected ServiceManager create() {
                    return new ServiceManager();
                }
            };

    public static ServiceManager getInstance() {
        return sInstance.get();
    }

    private ServiceManager() {
        Timber.v("init");
    }

    @NonNull
    public ICoreService fetchService() throws RemoteException {
        try {
            Context context = ContextUtil.getContext();
            Bundle ret = context.getContentResolver().call(
                    DefaultServiceContentProvider.getContentUri(),
                    DefaultServiceContentProvider.FETCH_SERVICE,
                    null,
                    null);
            IBinder binder = BundleCompat.getBinder(ret, DefaultServiceContentProvider.FETCH_SERVICE);
            ICoreService coreService = ICoreService.Stub.asInterface(binder);
            if (coreService == null) {
                throw new NullPointerException();
            }
            return coreService;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RemoteException("remote not found");
        }
    }

    public IBinder fetchService(String serviceName) throws RemoteException {
        return fetchService().getService(serviceName);
    }

}

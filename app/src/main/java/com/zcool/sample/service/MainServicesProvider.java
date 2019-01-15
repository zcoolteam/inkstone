package com.zcool.sample.service;

import android.os.IBinder;

import com.zcool.inkstone.Inkstone;
import com.zcool.inkstone.ModuleServicesProvider;
import com.zcool.inkstone.annotation.ServicesProvider;

import androidx.annotation.Keep;
import timber.log.Timber;

@Keep
@ServicesProvider
public class MainServicesProvider implements ModuleServicesProvider {
    // 注解 @ServicesProvider 必不可少

    public static final String SERVICE_SESSION = "session";

    @Override
    public void onCreate(Inkstone.ServicesProviderHost host) {
        Timber.v("[priority default] onCreate");

        // 添加自定义服务 SessionManager
        host.addService(SERVICE_SESSION, new Inkstone.StaticServiceFetcher<IBinder>() {
            @Override
            public IBinder createService() {
                return new SessionService();
            }
        });
    }
}

package com.zcool.sample.service;

import android.os.IBinder;
import android.support.annotation.Keep;

import com.zcool.inkstone.annotation.ServicesProvider;
import com.zcool.inkstone.service.BaseServicesProvider;

@Keep
@ServicesProvider
public class MainServicesProvider extends BaseServicesProvider {
    // 注解 @ServicesProvider 必不可少，全局有且只有一个类标记此注解

    public static final String SERVICE_SESSION = "session";

    @Override
    protected void onCreate() {
        super.onCreate();

        // 添加自定义服务 SessionManager
        addService(SERVICE_SESSION, new StaticServiceFetcher<IBinder>() {
            @Override
            public IBinder createService() {
                return new SessionService();
            }
        });
    }

}

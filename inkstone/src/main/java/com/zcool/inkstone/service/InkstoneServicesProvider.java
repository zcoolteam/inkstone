package com.zcool.inkstone.service;

import android.os.IBinder;

import com.zcool.inkstone.Inkstone;
import com.zcool.inkstone.ModuleServicesProvider;
import com.zcool.inkstone.annotation.ServicesProvider;

import androidx.annotation.Keep;

@Keep
@ServicesProvider
public final class InkstoneServicesProvider implements ModuleServicesProvider {

    public static final String SERVICE_STORAGE = "storage";
    public static final String SERVICE_COOKIE_STORE = "cookie_store";

    @Override
    public void onCreate(Inkstone.ServicesProviderHost host) {
        host.addService(SERVICE_STORAGE, new Inkstone.StaticServiceFetcher<IBinder>() {
            @Override
            public IBinder createService() {
                return new StorageService();
            }
        });
        host.addService(SERVICE_COOKIE_STORE, new Inkstone.StaticServiceFetcher<IBinder>() {
            @Override
            public IBinder createService() {
                return new CookieStoreService();
            }
        });
    }
}

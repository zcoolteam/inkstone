package com.example.appmodule;

import com.zcool.inkstone.Inkstone;
import com.zcool.inkstone.ModuleServicesProvider;
import com.zcool.inkstone.annotation.ServicesProvider;

import timber.log.Timber;

@ServicesProvider
public class AppModuleServicesProvider implements ModuleServicesProvider {

    @Override
    public void onCreate(Inkstone.ServicesProviderHost host) {
        Timber.v("[priority default] onCreate");
    }

}

package com.example.appmodule;

import com.zcool.inkstone.Inkstone;
import com.zcool.inkstone.ModuleServicesProvider;
import com.zcool.inkstone.annotation.ServicesProvider;

import androidx.annotation.Keep;
import timber.log.Timber;

@Keep
@ServicesProvider
public class AppModuleServicesProvider implements ModuleServicesProvider {

    @Override
    public void onCreate(Inkstone.ServicesProviderHost host) {
        Timber.v("[priority default] onCreate");
    }

}

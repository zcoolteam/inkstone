package com.zcool.inkstone;

import androidx.annotation.Keep;

@Keep
public interface ModuleServicesProvider {

    void onCreate(Inkstone.ServicesProviderHost host);

}

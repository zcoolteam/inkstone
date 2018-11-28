package com.zcool.inkstone.app;

import com.zcool.inkstone.Inkstone;

import androidx.multidex.MultiDexApplication;

public class InkstoneApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Inkstone.init(this);
    }

}

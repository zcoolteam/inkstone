package com.zcool.inkstone.app;

import android.support.multidex.MultiDexApplication;

import com.zcool.inkstone.Inkstone;

public class InkstoneApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Inkstone.init(this);
    }

}

package com.zcool.sample;

import android.content.Context;

import com.zcool.inkstone.ModuleApplicationDelegate;
import com.zcool.inkstone.annotation.ApplicationDelegate;

import timber.log.Timber;

@ApplicationDelegate(priority = -2)
public class MainApplicationDelegate2 implements ModuleApplicationDelegate {

    @Override
    public void onCreate(Context context) {
        Timber.v("[priority -2] onCreate");
        // App 启动入口, 在此处配置自定义初始化内容
    }

    @Override
    public void onStartBackgroundService() {
        Timber.v("[priority -2] onStartBackgroundService");
    }

}

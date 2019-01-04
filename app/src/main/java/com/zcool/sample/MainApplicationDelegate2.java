package com.zcool.sample;

import android.content.Context;

import com.zcool.inkstone.SubApplicationDelegate;
import com.zcool.inkstone.annotation.ApplicationDelegate;

import androidx.annotation.Keep;

@Keep
@ApplicationDelegate(priority = -2)
public class MainApplicationDelegate2 implements SubApplicationDelegate {

    @Override
    public void onCreate(Context context) {
        // App 启动入口, 在此处配置自定义初始化内容
    }

    @Override
    public void onStartBackgroundService() {
    }

}

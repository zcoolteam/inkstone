package com.example.appmodule2;

import android.content.Context;

import com.zcool.inkstone.ModuleApplicationDelegate;
import com.zcool.inkstone.annotation.ApplicationDelegate;

import androidx.annotation.Keep;
import timber.log.Timber;

@Keep
@ApplicationDelegate
public class AppModule2ApplicationDelegate implements ModuleApplicationDelegate {

    @Override
    public void onCreate(Context context) {
        Timber.v("[priority 1] onCreate");
    }

    @Override
    public void onStartBackgroundService() {
        Timber.v("[priority 1] onStartBackgroundService");
    }

}

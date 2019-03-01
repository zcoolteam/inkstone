package com.zcool.inkstone;

import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

import com.zcool.inkstone.annotation.ApplicationDelegate;
import com.zcool.inkstone.manager.FrescoManager;
import com.zcool.inkstone.manager.ProcessManager;
import com.zcool.inkstone.service.InkstoneService;
import com.zcool.inkstone.util.ContextUtil;

import timber.log.Timber;

@ApplicationDelegate(priority = -10000)
public class InkstoneApplicationDelegate implements ModuleApplicationDelegate {

    @Override
    public void onCreate(Context context) {
        Timber.v("[priority default] onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix(String.valueOf(ProcessManager.getInstance().getProcessTag()));
        }

        FrescoManager.getInstance();
    }

    @Override
    public void onStartBackgroundService() {
        Timber.v("[priority default] onStartBackgroundService");

        InkstoneService.start(ContextUtil.getContext());
    }

}

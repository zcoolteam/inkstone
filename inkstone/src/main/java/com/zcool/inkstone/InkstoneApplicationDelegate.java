package com.zcool.inkstone;

import android.content.Context;

import com.zcool.inkstone.annotation.ApplicationDelegate;
import com.zcool.inkstone.manager.FrescoManager;
import com.zcool.inkstone.service.InkstoneService;
import com.zcool.inkstone.util.ContextUtil;

import androidx.annotation.Keep;
import timber.log.Timber;

@Keep
@ApplicationDelegate
public class InkstoneApplicationDelegate implements ModuleApplicationDelegate {

    @Override
    public void onCreate(Context context) {
        Timber.v("[priority default] onCreate");

        FrescoManager.getInstance();
    }

    @Override
    public void onStartBackgroundService() {
        Timber.v("[priority default] onStartBackgroundService");

        InkstoneService.start(ContextUtil.getContext());
    }

}

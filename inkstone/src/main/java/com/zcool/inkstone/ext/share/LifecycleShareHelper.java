package com.zcool.inkstone.ext.share;

import com.zcool.inkstone.util.IOUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class LifecycleShareHelper implements LifecycleObserver {

    private Lifecycle mLifecycle;
    private ShareHelper mShareHelper;

    private LifecycleShareHelper(Lifecycle lifecycle, ShareHelper shareHelper) {
        mLifecycle = lifecycle;
        mShareHelper = shareHelper;

        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        mShareHelper.resume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        IOUtil.closeQuietly(mShareHelper);

        mLifecycle.removeObserver(this);
    }

    public static ShareHelper create(@NonNull FragmentActivity activity, @Nullable ShareHelper.AuthListener authListener, @Nullable ShareHelper.ShareListener shareListener) {
        ShareHelper shareHelper = new ShareHelper(activity, authListener, shareListener);
        LifecycleShareHelper lifecycleShareHelper = new LifecycleShareHelper(activity.getLifecycle(), shareHelper);
        return lifecycleShareHelper.mShareHelper;
    }

}

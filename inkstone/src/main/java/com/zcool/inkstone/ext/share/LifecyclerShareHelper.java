package com.zcool.inkstone.ext.share;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.zcool.inkstone.util.IOUtil;

public class LifecyclerShareHelper implements LifecycleObserver {

    private Lifecycle mLifecycle;
    private ShareHelper mShareHelper;

    private LifecyclerShareHelper(Lifecycle lifecycle, ShareHelper shareHelper) {
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
        LifecyclerShareHelper lifecyclerShareHelper = new LifecyclerShareHelper(activity.getLifecycle(), shareHelper);
        return lifecyclerShareHelper.mShareHelper;
    }

}

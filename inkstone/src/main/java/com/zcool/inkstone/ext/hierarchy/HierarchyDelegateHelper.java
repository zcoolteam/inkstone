package com.zcool.inkstone.ext.hierarchy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Window;

import timber.log.Timber;

public class HierarchyDelegateHelper {

    @Nullable
    public static Fragment showContentFragment(FragmentActivity activity, String fragmentClassName, Bundle args) {
        final int containerId = Window.ID_ANDROID_CONTENT;
        final String tag = "inkstone:content_fragment";
        return showFragment(activity, containerId, tag, fragmentClassName, args);
    }

    @Nullable
    public static Fragment showFragment(FragmentActivity activity, int containerId, String tag, String fragmentClassName, Bundle args) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.isStateSaved()) {
            Timber.e("fragment manager is state saved. you may need to call #setFragment on Activity#onCreate");
            return null;
        }

        if (TextUtils.isEmpty(tag)) {
            Timber.e("tag is empty");
            return null;
        }

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            return fragment;
        }

        fragment = Fragment.instantiate(activity, fragmentClassName, args);
        fragmentManager.beginTransaction()
                .add(containerId, fragment, tag)
                .commit();
        return fragment;
    }


}

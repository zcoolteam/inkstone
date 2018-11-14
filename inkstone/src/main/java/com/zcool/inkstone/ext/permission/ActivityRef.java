package com.zcool.inkstone.ext.permission;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.lang.ref.WeakReference;

public class ActivityRef extends WeakReference<Object> {

    private final RxPermissionResult mResult;

    private ActivityRef(Object target, RxPermissionResult result) {
        super(target);
        mResult = result;
    }

    @Override
    public FragmentActivity get() {
        Object target = super.get();
        if (target == null) {
            RxPermissionException exception = new RxPermissionException("target Activity is null or already destroyed");
            exception.setRxPermissionResult(mResult);
            throw exception;
        }

        if (target instanceof Fragment) {
            FragmentActivity activity = ((Fragment) target).getActivity();
            if (activity == null) {
                RxPermissionException exception = new RxPermissionException("target Activity is null or already destroyed. [fragment is not attached to Activity]");
                exception.setRxPermissionResult(mResult);
                throw exception;
            }
            return activity;
        }

        if (target instanceof FragmentActivity) {
            return (FragmentActivity) target;
        }

        RxPermissionException exception = new RxPermissionException("target Activity is null or already destroyed. [target type must be support Fragment or FragmentActivity, found " + target.getClass().getName() + "]");
        exception.setRxPermissionResult(mResult);
        throw exception;
    }

    public static ActivityRef create(Fragment fragment, String[] permissions) {
        return new ActivityRef(fragment, null);
    }

    public static ActivityRef create(FragmentActivity fragmentActivity, String[] permissions) {
        return new ActivityRef(fragmentActivity, null);
    }

}

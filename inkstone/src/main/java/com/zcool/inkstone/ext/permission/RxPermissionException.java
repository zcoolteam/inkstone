package com.zcool.inkstone.ext.permission;

import android.support.annotation.Nullable;

public class RxPermissionException extends RuntimeException {

    private RxPermissionResult mRxPermissionResult;
    private boolean mAbortWithOtherPermissionRequest;

    public RxPermissionException(String message) {
        super(message);
    }

    public RxPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public void setRxPermissionResult(RxPermissionResult rxPermissionResult) {
        mRxPermissionResult = rxPermissionResult;
    }

    public void setAbortWithOtherPermissionRequest(boolean abortWithOtherPermissionRequest) {
        mAbortWithOtherPermissionRequest = abortWithOtherPermissionRequest;
    }

    public boolean isAbortWithOtherPermissionRequest() {
        return mAbortWithOtherPermissionRequest;
    }

    @Nullable
    public RxPermissionResult getRxPermissionResult() {
        return mRxPermissionResult;
    }

}

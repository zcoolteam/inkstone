package com.zcool.inkstone.ext.permission;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.PermissionChecker;

import com.zcool.inkstone.thread.Threads;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class RxPermissionFragment extends Fragment {

    private static final String TAG = "inkstone_RxPermissionFragment";

    @NonNull
    private static RxPermissionFragment getOrCreate(@NonNull final ActivityRef activityRef) {
        FragmentManager fragmentManager = activityRef.get().getSupportFragmentManager();
        RxPermissionFragment fragment = (RxPermissionFragment) fragmentManager.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new RxPermissionFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, TAG)
                    .commitNowAllowingStateLoss();
        }
        return fragment;
    }

    static SingleSource<RxPermissionResult> requestPermissions(@NonNull final ActivityRef activityRef, @NonNull final String[] permissions, final boolean preFilterRationale) {
        return Single.just(activityRef)
                .flatMap(new Function<ActivityRef, SingleSource<? extends RxPermissionResult>>() {
                    @Override
                    public SingleSource<? extends RxPermissionResult> apply(ActivityRef activityRef) throws Exception {
                        return getOrCreate(activityRef).requestPermissions(permissions, preFilterRationale);
                    }
                });
    }

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String HAS_CURRENT_PERMISSIONS_REQUEST_KEY =
            "inkstone_hasCurrentPermissionsRequest";

    private boolean mHasCurrentPermissionsRequest;

    @Nullable
    private PendingResult mPendingResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreHasCurrentPermissionRequest(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        storeHasCurrentPermissionRequest(outState);
    }

    private void storeHasCurrentPermissionRequest(Bundle bundle) {
        if (bundle != null && mHasCurrentPermissionsRequest) {
            bundle.putBoolean(HAS_CURRENT_PERMISSIONS_REQUEST_KEY, true);
        }
    }

    private void restoreHasCurrentPermissionRequest(Bundle bundle) {
        if (bundle != null) {
            mHasCurrentPermissionsRequest = bundle.getBoolean(
                    HAS_CURRENT_PERMISSIONS_REQUEST_KEY, false);
        }
    }

    @UiThread
    private SingleSource<RxPermissionResult> requestPermissions(@NonNull final String[] permissions, final boolean preFilterRationale) {
        return new SingleSource<RxPermissionResult>() {
            @Override
            public void subscribe(SingleObserver<? super RxPermissionResult> observer) {
                Threads.mustUi();

                PendingResult pendingResult = create(permissions, observer);

                if (mHasCurrentPermissionsRequest) {
                    RxPermissionException exception = new RxPermissionException("mHasCurrentPermissionsRequest is true");
                    exception.setRxPermissionResult(pendingResult.mResult);
                    exception.setAbortWithOtherPermissionRequest(true);
                    throw exception;
                }

                if (pendingResult.requestPermissions(preFilterRationale)) {
                    mPendingResult = pendingResult;
                    mHasCurrentPermissionsRequest = true;
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Threads.mustUi();

        if (requestCode != PERMISSION_REQUEST_CODE) {
            Timber.w("request code invalid, ignore. require %s, but %s.", PERMISSION_REQUEST_CODE, requestCode);
            return;
        }

        if (!mHasCurrentPermissionsRequest) {
            Timber.w("mHasCurrentPermissionsRequest is false, ignore.");
            return;
        }
        mHasCurrentPermissionsRequest = false;
        PendingResult pendingResult = mPendingResult;
        mPendingResult = null;

        if (pendingResult == null) {
            Timber.w("pending result is null, ignore.");
            return;
        }

        pendingResult.finishWithRequestPermissionsResult();
    }

    public PendingResult create(@NonNull String[] permissions, @NonNull SingleObserver<? super RxPermissionResult> observer) {
        return new PendingResult(new RxPermissionResult(permissions), observer);
    }

    private class PendingResult {

        @NonNull
        private RxPermissionResult mResult;

        @NonNull
        private final SingleObserver<? super RxPermissionResult> mObserver;

        private PendingResult(@NonNull RxPermissionResult result, @NonNull SingleObserver<? super RxPermissionResult> observer) {
            mResult = result;
            mObserver = observer;
        }

        public void finishWithRequestPermissionsResult() {
            refresh();
            mObserver.onSuccess(mResult);
        }

        private boolean requestPermissions(boolean preFilterRationale) {
            refresh();

            if (preFilterRationale && mResult.hasAnyRationalePermission()) {
                mObserver.onSuccess(mResult);
                return false;
            }

            if (mResult.isAllGranted()) {
                mObserver.onSuccess(mResult);
                return false;
            }

            RxPermissionFragment.this.requestPermissions(mResult.getOriginalPermissions(), PERMISSION_REQUEST_CODE);
            return true;
        }

        private void refresh() {
            mResult = mResult.create();
            for (String permission : mResult.permissions) {
                int status = PermissionChecker.checkSelfPermission(createActivityRef(mResult.getOriginalPermissions()).get(), permission);
                if (status == PermissionChecker.PERMISSION_GRANTED) {
                    mResult.addGrantedPermission(permission);
                } else if (status == PermissionChecker.PERMISSION_DENIED_APP_OP) {
                    mResult.addOpsDeniedPermission(permission);
                } else if (status == PermissionChecker.PERMISSION_DENIED) {
                    mResult.addDefaultDeniedPermission(permission);
                } else {
                    Timber.e("unknown permission status %s for %s", status, permission);
                }

                if (ActivityCompat.shouldShowRequestPermissionRationale(createActivityRef(mResult.getOriginalPermissions()).get(), permission)) {
                    mResult.addNeedShowRationalePermission(permission);
                }
            }
        }

    }

    private ActivityRef createActivityRef(String[] permissions) {
        return ActivityRef.create(this, permissions);
    }

}

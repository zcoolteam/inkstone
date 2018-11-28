package com.zcool.inkstone.ext.permission;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class RxPermission {

    private RxPermission() {
    }

    public static SingleSource<RxPermissionResult> from(@NonNull FragmentActivity activity, @NonNull final String[] permissions, @Nullable Function<RxPermissionResult, SingleSource<RxPermissionResult>> rationaleFunction) {
        return from(activity, permissions, rationaleFunction, rationaleFunction != null);
    }

    public static SingleSource<RxPermissionResult> from(@NonNull FragmentActivity activity, @NonNull final String[] permissions, @Nullable final Function<RxPermissionResult, SingleSource<RxPermissionResult>> rationaleFunction, final boolean preFilterRationale) {
        final ActivityRef activityRef = ActivityRef.create(activity, permissions);
        return Single.just(new Object())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Object, SingleSource<RxPermissionResult>>() {
                    @Override
                    public SingleSource<RxPermissionResult> apply(Object o) throws Exception {
                        return Single.just(new Object())
                                .flatMap(new Function<Object, SingleSource<RxPermissionResult>>() {
                                    @Override
                                    public SingleSource<RxPermissionResult> apply(Object o) throws Exception {
                                        return from(activityRef.get(), permissions, preFilterRationale && rationaleFunction != null);
                                    }
                                })
                                .flatMap(new Function<RxPermissionResult, SingleSource<RxPermissionResult>>() {
                                    @Override
                                    public SingleSource<RxPermissionResult> apply(RxPermissionResult result) throws Exception {
                                        if (!result.hasAnyRationalePermission()) {
                                            return Single.just(result);
                                        }

                                        if (rationaleFunction == null) {
                                            return Single.just(result);
                                        }

                                        return Single.just(result)
                                                .flatMap(rationaleFunction)
                                                .flatMap(new Function<RxPermissionResult, SingleSource<RxPermissionResult>>() {
                                                    @Override
                                                    public SingleSource<RxPermissionResult> apply(RxPermissionResult result) throws Exception {
                                                        return from(activityRef.get(), result.getOriginalPermissions(), rationaleFunction, false);
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    public static SingleSource<RxPermissionResult> from(@NonNull FragmentActivity activity, @NonNull String[] permissions, boolean preFilterRationale) {
        return RxPermissionFragment.requestPermissions(ActivityRef.create(activity, permissions), permissions, preFilterRationale);
    }

}

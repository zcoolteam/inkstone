package com.zcool.inkstone.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;

import com.zcool.inkstone.BaseApplicationDelegate;
import com.zcool.inkstone.util.ContextUtil;

public class DefaultServiceContentProvider extends ContentProvider {

    public static final String FETCH_SERVICE = "fetch_service";

    @Override
    public boolean onCreate() {
        BaseApplicationDelegate.init(getContext());
        return true;
    }

    public static Uri getContentUri() {
        return Uri.parse("content://" + "inkstone." + ContextUtil.getContext().getPackageName() + ".DefaultServiceContentProvider");
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        if (FETCH_SERVICE.equals(method)) {
            Bundle ret = new Bundle();
            BundleCompat.putBinder(ret, FETCH_SERVICE, new ICoreService.Stub() {
                @Override
                public IBinder getService(String serviceName) throws RemoteException {
                    return BaseServicesProvider.getInstance().getService(serviceName);
                }
            });
            return ret;
        }
        return super.call(method, arg, extras);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

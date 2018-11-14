package com.zcool.inkstone.util;

import android.Manifest;
import android.support.v4.content.PermissionChecker;

public class PermissionUtil {

    private PermissionUtil() {
    }

    /**
     * 如果授权通过返回 true, 否则返回 false.
     */
    public static boolean isGranted(String permission) {
        return PermissionChecker.checkSelfPermission(ContextUtil.getContext(), permission) == PermissionChecker.PERMISSION_GRANTED;
    }

    /**
     * 如果授权都通过返回 true, 否则返回 false.
     */
    public static boolean isAllGranted(String... permissions) {
        if (permissions == null) {
            return true;
        }

        for (String permission : permissions) {
            if (!isGranted(permission)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断当前是否有读写 sdcard 权限
     */
    public static boolean hasExternalStoragePermission() {
        return isAllGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

}

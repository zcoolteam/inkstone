package com.zcool.inkstone.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.List;

public class CameraUtil {

    /**
     * 尝试创建一个用来拍照的临时文件, 位于扩展卡的 app 缓存路径下.
     * 仅用来计算这样一个可用的路径, 文件本身并没有创建. 如果没有可用的文件, 返回 null.
     */
    public static File createCameraTmpFile() {
        File cacheDir = FileUtil.getAppCacheDir();
        if (cacheDir == null) {
            return null;
        }

        File cameraTmpDir = new File(cacheDir, "camera");
        File cameraFile = FileUtil.createNewTmpFileQuietly("camera", ".jpg", cameraTmpDir);
        if (cameraFile == null) {
            return null;
        }

        FileUtil.deleteFileQuietly(cameraFile);

        return cameraFile;
    }

    public static class OutParams {
        public boolean error;
        public String errorMsg;
        public File cameraTmpFile;
    }

    /**
     * 如果调用成功, 在 Fragment 的 onActivityResult 中获取最终拍照结果
     *
     * @param outParams 用来获取拍照的调用信息
     */
    public static void takePhoto(Fragment fragment, int requestCode, @NonNull OutParams outParams) {
        outParams.error = false;
        outParams.errorMsg = null;
        outParams.cameraTmpFile = createCameraTmpFile();

        if (outParams.cameraTmpFile == null) {
            outParams.error = true;
            outParams.errorMsg = "SD卡不可用或权限不足";
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getFileUri(outParams.cameraTmpFile));
        FileUtil.addGrantUriPermission(intent);

        List<ResolveInfo> infos =
                ContextUtil.getContext().getPackageManager().queryIntentActivities(intent, 0);
        if (infos != null && infos.size() > 0) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            outParams.error = true;
            outParams.errorMsg = "没有找到可用的相机或权限不足";
        }
    }

    /**
     * 如果调用成功, 在 Activity 的 onActivityResult 中获取最终拍照结果
     *
     * @param outParams 用来获取拍照的调用信息
     */
    public static void takePhoto(Activity activity, int requestCode, @NonNull OutParams outParams) {
        outParams.error = false;
        outParams.errorMsg = null;
        outParams.cameraTmpFile = createCameraTmpFile();

        if (outParams.cameraTmpFile == null) {
            outParams.error = true;
            outParams.errorMsg = "SD卡不可用或权限不足";
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getFileUri(outParams.cameraTmpFile));
        FileUtil.addGrantUriPermission(intent);

        List<ResolveInfo> infos =
                ContextUtil.getContext().getPackageManager().queryIntentActivities(intent, 0);
        if (infos != null && infos.size() > 0) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            outParams.error = true;
            outParams.errorMsg = "没有找到可用的相机或权限不足";
        }
    }

    private CameraUtil() {
    }
}

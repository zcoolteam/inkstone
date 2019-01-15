package com.zcool.inkstone.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.zcool.inkstone.ApplicationDelegateRoot;
import com.zcool.inkstone.lang.CommonFileProvider;
import com.zcool.inkstone.manager.ProcessManager;
import com.zcool.inkstone.manager.TmpFileManager;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * 文件操作相关辅助类
 */
public class FileUtil {

    private FileUtil() {
    }

    /**
     * 获取用于存储到媒体库的目录.
     * 如果外部存储区域不可用，返回 null.
     */
    @Nullable
    public static File getAppMediaDir() {
        if (!PermissionUtil.hasExternalStoragePermission()) {
            Timber.e("permission required");
            return null;
        }

        File rootDir = Environment.getExternalStorageDirectory();
        if (rootDir == null) {
            Timber.e("Environment.getExternalStorageDirectory() return null");
            return null;
        }
        String mediaDirName = ApplicationDelegateRoot.getInstance().getMediaDirName();
        if (TextUtils.isEmpty(mediaDirName)) {
            Timber.e("mediaDirName is empty");
            return null;
        }

        File targetDir = new File(rootDir, mediaDirName);
        if (createDir(targetDir)) {
            return targetDir;
        }

        final boolean exists = targetDir.exists();
        final boolean notDirectory = !targetDir.isDirectory();
        Timber.e("fail to init dir(getAppMediaDir) exists:%s, notDirectory:%s, path:%s", exists, notDirectory, targetDir.getPath());
        return null;
    }

    /**
     * 此目录不需要读写外部存储区域权限.
     * 获得一个进程安全的缓存目录，如果不能获得这样一个目录，返回 null.
     */
    @Nullable
    public static File getAppCacheDir() {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dir = ContextUtil.getContext().getExternalCacheDir();
            if (dir != null) {
                if (Environment.isExternalStorageRemovable(dir)) {
                    dir = null;
                }
            }
        }

        if (dir == null) {
            dir = ContextUtil.getContext().getCacheDir();
        }

        if (dir == null) {
            return null;
        }
        File processDir = new File(dir, ProcessManager.getInstance().getProcessTag());
        if (createDir(processDir)) {
            return processDir;
        }
        return null;
    }

    /**
     * 此目录不需要读写外部存储区域权限.
     * 获得一个进程安全的数据目录，如果不能获得这样一个目录，返回 null.
     */
    @Nullable
    public static File getAppFilesDir() {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dir = ContextUtil.getContext().getExternalFilesDir(null);
            if (dir != null) {
                if (Environment.isExternalStorageRemovable(dir)) {
                    dir = null;
                }
            }
        }

        if (dir == null) {
            dir = ContextUtil.getContext().getFilesDir();
        }

        if (dir == null) {
            return null;
        }
        File processDir = new File(dir, ProcessManager.getInstance().getProcessTag());
        if (createDir(processDir)) {
            return processDir;
        }
        return null;
    }

    /**
     * 判断指定的文件是否存在并且是一个文件(不是文件夹)
     */
    public static boolean isFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 判断指定的文件是否存在并且是一个文件夹
     */
    public static boolean isDir(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    public static boolean hasMoreSpace(File file) {
        return getAvailableSpace(file) > HumanUtil.MB * 10;
    }

    public static long getAvailableSpace(File file) {
        StatFs statFS = new StatFs(file.getPath());
        long blockSize, availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFS.getBlockSizeLong();
            availableBlocks = statFS.getAvailableBlocksLong();
        } else {
            blockSize = statFS.getBlockSize();
            availableBlocks = statFS.getAvailableBlocks();
        }
        return blockSize * availableBlocks;
    }

    /**
     * 创建目录，如果创建成功，或者目录已经存在，返回true. 否则返回false.
     */
    public static boolean createDir(File file) {
        if (file == null) {
            return false;
        }

        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file.isDirectory() || !file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 文件(文件夹)已删除或者删除成功返回true, 否则返回false
     */
    public static boolean deleteFileQuietly(File file) {
        if (file == null || !file.exists()) {
            return true;
        }
        if (file.isFile()) {
            file.delete();
            return !file.exists();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!deleteFileQuietly(f)) {
                        return false;
                    }
                }
            }
            file.delete();
            return !file.exists();
        }
        return false;
    }

    /**
     * 路径无效或者文件(文件夹)已删除或者删除成功返回true, 否则返回false
     */
    public static boolean deleteFileQuietly(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        return deleteFileQuietly(new File(path));
    }

    /**
     * 如果文件不存在并且此次创建成功，返回true. 否则返回false.
     */
    public static boolean createNewFileQuietly(File file) {
        if (file == null) {
            return false;
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!createDir(parent)) {
                return false;
            }
        }

        if (file.exists()) {
            return false;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.exists() && file.isFile();
    }

    /**
     * 从指定 url 获取扩展名, 不包含扩展名分隔符<code>.</code>，如果获取失败，返回 null.
     */
    @Nullable
    public static String getFileExtensionFromUrl(String url) {
        String filename = getFilenameFromUrl(url);
        filename = StringUtil.trim(filename, "#?/\\.");
        if (!TextUtils.isEmpty(filename)) {
            int dotPos = filename.lastIndexOf('.');
            if (dotPos > 0) {
                return filename.substring(dotPos + 1);
            }
        }

        return null;
    }

    /**
     * 从指定 url 获取文件名，包含扩展名, 如果获取失败，返回 null.
     */
    @Nullable
    public static String getFilenameFromUrl(String url) {
        url = StringUtil.trim(url, "#?/\\.");
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        int filenamePos0 = url.lastIndexOf('/');
        int filenamePos1 = url.lastIndexOf('\\');

        int filenamePos = Math.max(filenamePos0, filenamePos1);
        String filename = filenamePos > 0 ? url.substring(filenamePos + 1) : url;

        if (filename.length() == 0) {
            return null;
        }
        return filename;
    }

    /**
     * 以指定的文件名前缀和后缀在指定文件夹创建一个临时文件，如果创建失败，返回 null. 推荐使用 TmpFileManager 生成临时文件.
     *
     * @see TmpFileManager
     */
    @Nullable
    public static File createNewTmpFileQuietly(String prefix, String suffix, File dir) {
        try {
            if (createDir(dir)) {
                if (prefix == null) {
                    prefix = "";
                }
                if (suffix == null) {
                    suffix = ".tmp";
                }

                if (!suffix.isEmpty() && !suffix.startsWith(".")) {
                    suffix = "." + suffix;
                }

                String filename = String.valueOf(System.currentTimeMillis());
                File tmpFile = new File(dir, prefix + filename + suffix);

                if (tmpFile.createNewFile()) {
                    return tmpFile;
                }

                for (int i = 1; i < 20; i++) {
                    tmpFile = new File(dir, prefix + filename + "(" + i + ")" + suffix);
                    if (tmpFile.createNewFile()) {
                        return tmpFile;
                    }
                }

                throw new RuntimeException("相似文件太多 " + tmpFile.getAbsolutePath());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建一个新文件，如果指定的文件已经存在，则尝试创建一个相似的文件，返回创建成功的文件路径，
     * 如果创建失败，返回 null.
     */
    @Nullable
    public static String createSimilarFileQuietly(String path) {
        try {
            File f = new File(path);
            File parent = f.getParentFile();
            String filename = f.getName();

            String extension = FileUtil.getFileExtensionFromUrl(filename);
            if (!TextUtils.isEmpty(extension)) {
                filename = filename.substring(0, filename.length() - extension.length() - 1);
                extension = "." + extension;
            } else {
                extension = "";
            }

            if (FileUtil.createDir(parent)) {
                File tmpFile = new File(parent, filename + extension);

                if (tmpFile.createNewFile()) {
                    return tmpFile.getAbsolutePath();
                }

                for (int i = 1; i < 20; i++) {
                    tmpFile = new File(parent, filename + "(" + i + ")" + extension);
                    if (tmpFile.createNewFile()) {
                        return tmpFile.getAbsolutePath();
                    }
                }

                throw new RuntimeException("相似文件太多 " + tmpFile.getAbsolutePath());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri getFileUri(File file) {
        Uri targetUri;
        if (Build.VERSION.SDK_INT >= 24) {
            targetUri = CommonFileProvider.getUriForFile(file);
        } else {
            targetUri = Uri.fromFile(file);
        }
        return targetUri;
    }

    /**
     * use file content uri for Intent data, need set grant uri permission
     */
    public static void addGrantUriPermission(Intent intent) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }
}

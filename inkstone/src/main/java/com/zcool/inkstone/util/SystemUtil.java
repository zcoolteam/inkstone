package com.zcool.inkstone.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import java.io.File;

import timber.log.Timber;

/**
 * 一些系统相关辅助类
 */
public class SystemUtil {

    /**
     * 成功打开软件商店(会尝试定位到指定软件)返回true, 如果没有安装任何软件商店, 返回false.
     */
    public static boolean openMarket(String packageName) {
        String url = "market://details?id=" + packageName;
        return openView(url);
    }

    public static boolean openView(String url) {
        return openView(url, null);
    }

    /**
     * 使用 chooser 方式打开指定 url, 处理成功返回true, 否则返回false.
     */
    public static boolean openView(String url, CharSequence chooserTitle) {
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.resolveActivity(ContextUtil.getContext().getPackageManager()) != null) {
                if (TextUtils.isEmpty(chooserTitle)) {
                    chooserTitle = " ";
                }
                Intent chooser = Intent.createChooser(intent, chooserTitle);
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ContextUtil.getContext().startActivity(chooser);
                return true;
            } else {
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前进程允许的最大 heap size(字节数). 仅 Java 部分的内容受此系统设置限制， native 层的内容消耗受手机内存容量限制. 容量超过此值会出现 OOM 错误. 如
     * 手机 CHM-UL00 的 heap size 是 268435456 byte (256M), 该手机的配置是 2G 内存，16G 存储空间, 1280x720 分辨率,
     * Android 4.4.2 系统
     */
    public static long getMaxHeapSize() {
        ActivityManager am =
                (ActivityManager)
                        ContextUtil.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        return am.getMemoryClass() * 1024L * 1024L;
    }

    public static boolean showSoftKeyboard(EditText editText) {
        InputMethodManager inputMethodManager =
                (InputMethodManager)
                        ContextUtil.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.showSoftInput(editText, 0);
    }

    public static boolean hideSoftKeyboard(EditText editText) {
        InputMethodManager inputMethodManager =
                (InputMethodManager)
                        ContextUtil.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 将指定文件添加到系统媒体库, 如将一张图片添加到系统媒体库, 使得在 Gallery 中能够显示.
     * 文件内容和文件名必须符合规范, 如：文件后缀需要时媒体类型.
     * 初始校验通过返回 true, 否则返回 false.
     */
    public static boolean addToMediaStore(File file) {
        try {
            if (!PermissionUtil.hasExternalStoragePermission()) {
                throw new IllegalStateException("external storage permission required");
            }

            final String filePath = file.getPath();
            File baseDir = Environment.getExternalStorageDirectory();
            String filePathPrefix = baseDir.getPath();
            if (!filePath.startsWith(filePathPrefix)) {
                throw new IllegalArgumentException("file path error, must starts with " + filePathPrefix);
            }

            File exceptDir = new File(baseDir, "Android");
            String exceptPathPrefix = exceptDir.getPath();
            if (filePath.startsWith(exceptPathPrefix)) {
                throw new IllegalArgumentException("file path error, must not system(Android) dir " + exceptPathPrefix);
            }

            if (!file.exists()) {
                throw new IllegalStateException("file not exists " + file.getCanonicalPath());
            }
            if (!file.canRead()) {
                throw new IllegalStateException("file can not read " + file.getCanonicalPath());
            }
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            ContextUtil.getContext().sendBroadcast(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            Timber.e(new Throwable("fail add to media store " + file, e));
        }
        return false;
    }

    /**
     * 调用系统安装程序安装指定 apk, 调用成功返回 true, 否则返回 false.
     */
    public static boolean installApk(File apkFile) {
        try {
            Uri uri = FileUtil.getFileUri(apkFile);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            FileUtil.addGrantUriPermission(intent);

            if (intent.resolveActivity(ContextUtil.getContext().getPackageManager()) != null) {
                Intent chooser = Intent.createChooser(intent, null);
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ContextUtil.getContext().startActivity(chooser);
                return true;
            } else {
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取系统默认 user-agent
     */
    public static String getSystemUserAgent() {
        return System.getProperty("http.agent");
    }

    private static String sSystemWebViewUserAgent;

    /**
     * 获取系统 webview 默认 user-agent
     */
    public static String getSystemWebViewUserAgent() {
        if (sSystemWebViewUserAgent != null) {
            return sSystemWebViewUserAgent;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            sSystemWebViewUserAgent = WebSettings.getDefaultUserAgent(ContextUtil.getContext());
        } else {
            sSystemWebViewUserAgent =
                    new WebView(ContextUtil.getContext()).getSettings().getUserAgentString();
        }
        return sSystemWebViewUserAgent;
    }

}

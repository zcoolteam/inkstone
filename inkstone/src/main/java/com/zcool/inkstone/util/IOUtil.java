package com.zcool.inkstone.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteClosable;
import android.graphics.BitmapRegionDecoder;
import android.webkit.WebView;

import com.zcool.inkstone.lang.AbortSignal;
import com.zcool.inkstone.lang.Charsets;
import com.zcool.inkstone.lang.Progress;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * IO 辅助类
 */
public final class IOUtil {

    private IOUtil() {
    }

    public static void closeQuietly(WebView webView) {
        if (webView != null) {
            try {
                webView.stopLoading();
                webView.clearHistory();
                webView.loadUrl("about:blank");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * SQLiteDatabase not need close
     */
    @Deprecated
    public static void closeQuietly(SQLiteClosable closeable) {
        // ignore
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }

    public static void closeQuietly(BitmapRegionDecoder decoder) {
        if (decoder != null) {
            decoder.recycle();
        }
    }

    public static long copy(
            InputStream from,
            OutputStream to,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        long copy = 0;
        byte[] step = new byte[8 * 1024];
        int read;
        while ((read = from.read(step)) != -1) {
            AbortUtil.throwIfAbort(abortSignal);
            to.write(step, 0, read);
            copy += read;
            Progress.append(progress, read);
        }
        return copy;
    }

    public static long copy(
            byte[] from,
            OutputStream to,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(from);
            return copy(bais, to, abortSignal, progress);
        } finally {
            closeQuietly(bais);
        }
    }

    public static long copy(
            InputStream from,
            OutputStream to,
            long count,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        int stepSize = 8 * 1024;
        if (stepSize > count) {
            stepSize = (int) count;
        }
        long copy = 0;
        byte[] step = new byte[stepSize];
        int read;
        while ((read = from.read(step, 0, stepSize)) != -1) {
            AbortUtil.throwIfAbort(abortSignal);
            to.write(step, 0, read);
            copy += read;
            Progress.append(progress, read);
            if (count < copy) {
                throw new IndexOutOfBoundsException("count:" + count + ", copy:" + copy);
            }
            if (count == copy) {
                break;
            }
            long remainCount = count - copy;
            if (stepSize > remainCount) {
                stepSize = (int) remainCount;
            }
        }
        return copy;
    }

    public static long copy(
            File from,
            OutputStream to,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(from);
            return copy(fis, to, abortSignal, progress);
        } finally {
            closeQuietly(fis);
        }
    }

    public static long copy(
            InputStream from,
            File to,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        return copy(from, to, false, abortSignal, progress);
    }

    public static long copy(
            InputStream from,
            File to,
            boolean append,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(to, append);
            return copy(from, fos, abortSignal, progress);
        } finally {
            closeQuietly(fos);
        }
    }

    public static long copy(
            File from,
            File to,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(from);
            fos = new FileOutputStream(to);
            return copy(fis, fos, abortSignal, progress);
        } finally {
            closeQuietly(fis);
            closeQuietly(fos);
        }
    }

    public static byte[] read(
            InputStream is,
            long count,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            copy(is, baos, count, abortSignal, progress);
            return baos.toByteArray();
        } finally {
            closeQuietly(baos);
        }
    }

    public static String readAsString(
            InputStream is,
            long count,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        byte[] all = read(is, count, abortSignal, progress);
        return new String(all, Charsets.UTF8);
    }

    public static byte[] readAll(
            File file,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return readAll(fis, abortSignal, progress);
        } finally {
            IOUtil.closeQuietly(fis);
        }
    }

    public static byte[] readAll(
            InputStream is,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            copy(is, baos, abortSignal, progress);
            return baos.toByteArray();
        } finally {
            closeQuietly(baos);
        }
    }

    public static String readAllAsString(
            InputStream is,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        byte[] all = readAll(is, abortSignal, progress);
        return new String(all, Charsets.UTF8);
    }

    /**
     * 读取所有的行并返回，注意返回的行内容不包括换行符号 '\n', '\r', "\r\n" <br>
     * 每读取一行，进度 append 1.
     */
    public static List<String> readAllLines(
            InputStream is,
            @Nullable AbortSignal abortSignal,
            @Nullable Progress progress)
            throws Throwable {
        List<String> allLines = new ArrayList<>();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is, Charsets.UTF8);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                AbortUtil.throwIfAbort(abortSignal);
                allLines.add(line);
                Progress.append(progress, 1);
            }
        } finally {
            closeQuietly(br);
            closeQuietly(isr);
        }
        return allLines;
    }
}

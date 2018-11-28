package com.zcool.inkstone.util;

import android.text.TextUtils;

import com.zcool.inkstone.lang.Charsets;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * MD5 辅助类(小写32位)
 */
public class MD5Util {

    private static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private static final String DEFAULT_MD5 = "";

    @NonNull
    public static String md5FilePath(@Nullable String filePath) {
        try {
            if (TextUtils.isEmpty(filePath)) {
                return DEFAULT_MD5;
            }

            return md5(new File(filePath));
        } catch (Throwable e) {
            e.printStackTrace();
            return DEFAULT_MD5;
        }
    }

    @NonNull
    public static String md5(@Nullable File file) {
        FileInputStream fis = null;
        DigestInputStream dis = null;
        try {
            if (file == null || !file.exists()) {
                return DEFAULT_MD5;
            }

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, messageDigest);

            byte[] step = new byte[8 * 1024];
            int read;
            do {
                read = dis.read(step);
            } while (read != -1);

            byte[] data = messageDigest.digest();
            return toHexString(data);
        } catch (Throwable e) {
            e.printStackTrace();
            return DEFAULT_MD5;
        } finally {
            IOUtil.closeQuietly(dis);
            IOUtil.closeQuietly(fis);
        }
    }

    @NonNull
    public static String md5(@Nullable String str) {
        try {
            if (str == null) {
                return DEFAULT_MD5;
            }

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] data = messageDigest.digest(str.getBytes(Charsets.UTF8));
            return toHexString(data);
        } catch (Throwable e) {
            e.printStackTrace();
            return DEFAULT_MD5;
        }
    }

    private static String toHexString(byte[] data) {
        char[] result = new char[data.length * 2];
        int i = 0;
        for (byte b : data) {
            result[i++] = HEX_DIGITS[(b >> 4) & 0xf];
            result[i++] = HEX_DIGITS[b & 0xf];
        }
        return String.valueOf(result);
    }
}

package com.zcool.inkstone.util;

import java.util.Locale;

/**
 * 处理适合阅读的数据转换
 */
public class HumanUtil {

    public static final long B = 1L;
    public static final long KB = 1024 * B;
    public static final long MB = 1024 * KB;
    public static final long GB = 1024 * MB;

    private static final long _80P_KB = _80p(KB);
    private static final long _80P_MB = _80p(MB);
    private static final long _80P_GB = _80p(GB);

    /**
     * 将以 byte 为单位的一定长度值转换为 GB, MB, KB, B为单位的字符串,结果中携带有 GB, MB, KB, B, 并保留一位小数.
     */
    public static String getHumanSizeFromByte(long bytes) {
        long bytesAbs = Math.abs(bytes);
        if (bytesAbs > _80P_GB) {
            return String.format(Locale.getDefault(), "%.1fGB", 1.0f * bytes / GB);
        }
        if (bytesAbs > _80P_MB) {
            return String.format(Locale.getDefault(), "%.1fMB", 1.0f * bytes / MB);
        }
        if (bytesAbs > _80P_KB) {
            return String.format(Locale.getDefault(), "%.1fKB", 1.0f * bytes / KB);
        }
        return String.format(Locale.getDefault(), "%.1fB", 1.0f * bytes);
    }

    /**
     * 返回这个数值的80%，结果为long类型。
     */
    private static long _80p(long input) {
        return input * 80 / 100;
    }
}

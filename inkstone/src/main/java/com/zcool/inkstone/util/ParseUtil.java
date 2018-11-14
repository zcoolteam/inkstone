package com.zcool.inkstone.util;

public class ParseUtil {

    public static int getInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Throwable e) {
            try {
                double d = Double.parseDouble(value);
                int ret = (int) d;
                if (ret == d) {
                    return ret;
                }
            } catch (Throwable ex) {
                // ignore
            }
            return defaultValue;
        }
    }

    public static long getLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (Throwable e) {
            try {
                double d = Double.parseDouble(value);
                long ret = (long) d;
                if (ret == d) {
                    return ret;
                }
            } catch (Throwable ex) {
                // ignore
            }
            return defaultValue;
        }
    }

    public static float getFloat(String value, float defaultValue) {
        try {
            return Float.parseFloat(value);
        } catch (Throwable e) {
            try {
                double d = Double.parseDouble(value);
                float ret = (float) d;
                if (ret == d) {
                    return ret;
                }
            } catch (Throwable ex) {
                // ignore
            }
            return defaultValue;
        }
    }

    public static double getDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Throwable e) {
            return defaultValue;
        }
    }

}

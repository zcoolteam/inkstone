package com.zcool.inkstone.util;

import android.text.TextUtils;

import com.zcool.inkstone.lang.Charsets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextUtil {

    private TextUtil() {
    }

    public static int getGBKLength(String input) {
        if (TextUtils.isEmpty(input)) {
            return 0;
        }

        int length = input.length();
        try {
            length = input.getBytes(Charsets.GBK).length;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return length;
    }

    @Nullable
    public static String trim(String string) {
        if (string != null) {
            string = string.trim();
        }
        return string;
    }

    @NonNull
    public static <T extends CharSequence> T checkStringNotEmpty(final T string) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    @NonNull
    public static <T extends CharSequence> T checkStringNotEmpty(final T string, final Object errorMessage) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return string;
    }

}

package com.zcool.inkstone.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class StringUtil {

    private StringUtil() {
    }

    /**
     * 去除开始和结尾的空白字符和指定需要删除的字符
     */
    @NonNull
    public static String trim(@Nullable String text, String remove) {
        if (text == null) {
            return "";
        }

        int len = text.length();
        int start = 0;

        char tmpChar;

        while (start < len) {
            tmpChar = text.charAt(start);
            if (tmpChar <= ' ') {
                start++;
                continue;
            }

            if (remove != null && remove.indexOf(tmpChar) >= 0) {
                start++;
                continue;
            }
            break;
        }

        while (start < len) {
            tmpChar = text.charAt(len - 1);
            if (tmpChar <= ' ') {
                len--;
                continue;
            }

            if (remove != null && remove.indexOf(tmpChar) >= 0) {
                len--;
                continue;
            }
            break;
        }

        return ((start > 0) || (len < text.length())) ? text.substring(start, len) : text;
    }

}

package com.zcool.inkstone.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常见正则表达式相关辅助类
 */
public class RegexUtil {

    private static final Pattern PATTERN_PHONE =
            Pattern.compile("^(1)\\d{10}$");
    private static final Pattern PATTERN_EMAIL =
            Pattern.compile(
                    "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
    private static final Pattern PATTERN_JSONP =
            Pattern.compile("^[a-zA-Z_$][0-9a-zA-Z_$]*[\\s]*\\(([^\\)]*)\\)[\\s]*[;]*$");

    /**
     * 国内手机号
     */
    public static boolean isPhoneNumber(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        Pattern p = PATTERN_PHONE;
        Matcher m = p.matcher(phoneNum);
        return m.matches();
    }

    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }

        Pattern p = PATTERN_EMAIL;
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isJsonp(String jsonp) {
        if (TextUtils.isEmpty(jsonp)) {
            return false;
        }

        Pattern p = PATTERN_JSONP;
        Matcher m = p.matcher(jsonp);
        return m.matches();
    }

    /**
     * 如果是合法的 jsonp 格式, 转换为 json, 否则原样返回.
     */
    public static String jsonp2json(String jsonp) {
        if (TextUtils.isEmpty(jsonp)) {
            return jsonp;
        }

        Pattern p = PATTERN_JSONP;
        Matcher m = p.matcher(jsonp);
        if (m.matches()) {
            return m.group(1);
        } else {
            return jsonp;
        }
    }

}

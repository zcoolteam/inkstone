package com.zcool.inkstone.lang;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;

public class GBKLengthInputFilter implements InputFilter {

    private final int mMax;
    private final boolean mSingleLine;

    public GBKLengthInputFilter(int max, boolean singleLine) {
        mMax = max;
        mSingleLine = singleLine;
    }

    public CharSequence filter(
            CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int len;
            boolean more;
            do {
                source = source.subSequence(start, end);
                SpannableStringBuilder builder =
                        new SpannableStringBuilder(dest).replace(dstart, dend, source);
                len = builder.toString().getBytes(Charsets.GBK).length;
                more = len > mMax;
                if (more) {
                    end--;
                    if (end == start) {
                        return "";
                    }

                    if (Character.isHighSurrogate(source.charAt(end - 1))) {
                        end--;
                        if (end == start) {
                            return "";
                        }
                    }
                }
            } while (more);

            if (mSingleLine) {
                if (source instanceof Spanned) {
                    String result = source.toString().replaceAll("[\\r\\n]", " ");
                    SpannableString sp = new SpannableString(result);
                    TextUtils.copySpansFrom((Spanned) source, 0, source.length(), null, sp, 0);
                    return sp;
                } else {
                    return source.toString().replaceAll("[\\r\\n]", " ");
                }
            }

            return source;
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getMax() {
        return mMax;
    }
}

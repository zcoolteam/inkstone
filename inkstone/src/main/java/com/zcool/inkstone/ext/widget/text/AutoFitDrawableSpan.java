package com.zcool.inkstone.ext.widget.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ReplacementSpan;

public class AutoFitDrawableSpan extends ReplacementSpan {

    private final Drawable mDrawable;
    private final int mOriginalWidth;
    private final int mOriginalHeight;
    private final Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();

    public AutoFitDrawableSpan(@NonNull Drawable drawable) {
        mDrawable = drawable;
        mOriginalWidth = mDrawable.getIntrinsicWidth();
        mOriginalHeight = mDrawable.getIntrinsicHeight();
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        adjustDrawableBounds(paint);

        canvas.save();
        int offsetY = y + mFontMetricsInt.ascent;
        canvas.translate(x, offsetY);
        mDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        adjustDrawableBounds(paint);

        if (fm != null) {
            fm.ascent = mFontMetricsInt.ascent;
            fm.descent = mFontMetricsInt.descent;
            fm.top = mFontMetricsInt.top;
            fm.bottom = mFontMetricsInt.bottom;
            fm.leading = mFontMetricsInt.leading;
        }

        return mDrawable.getBounds().right;
    }


    private void adjustDrawableBounds(Paint paint) {
        paint.getFontMetricsInt(mFontMetricsInt);
        int size = mFontMetricsInt.descent - mFontMetricsInt.ascent;

        if (mOriginalWidth <= 0 || mOriginalHeight <= 0) {
            mDrawable.setBounds(0, 0, size, size);
        } else {
            mDrawable.setBounds(0, 0, (int) (1f * mOriginalWidth * size / mOriginalHeight), size);
        }
    }

}

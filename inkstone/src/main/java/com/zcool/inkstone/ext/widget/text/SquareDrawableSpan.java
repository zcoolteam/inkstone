package com.zcool.inkstone.ext.widget.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 将 drawable 渲染为与字体同等大小的正方形
 */
public class SquareDrawableSpan extends ReplacementSpan {

    @NonNull
    private final Drawable mDrawable;
    private final Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();
    private int mLineHeight;

    public SquareDrawableSpan(@NonNull Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        adjustDrawableBounds(paint);

        canvas.save();
        int offsetY = y + mFontMetricsInt.ascent + (mLineHeight - mDrawable.getBounds().bottom) / 2;
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
        mLineHeight = mFontMetricsInt.descent - mFontMetricsInt.ascent;
        int size = mLineHeight;
        mDrawable.setBounds(0, 0, size, size);
    }

}
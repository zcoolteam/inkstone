package com.zcool.sample.widget.refreshlayout;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.PathShape;

import com.zcool.inkstone.util.DimenUtil;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ArrowDrawable extends Drawable {

    private int mColor = 0xffb5b6b7;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final PathShape mPathShape;

    private int mPadding;

    public ArrowDrawable() {
        final float size = DimenUtil.dp2px(60);
        final float cellSize = size / 6f;

        Path path = new Path();
        path.moveTo(3 * cellSize, 0);
        path.lineTo(2 * cellSize, 4 * cellSize);
        path.lineTo(cellSize, 4 * cellSize);
        path.lineTo(3 * cellSize, size);
        path.lineTo(5 * cellSize, 4 * cellSize);
        path.lineTo(4 * cellSize, 4 * cellSize);
        path.lineTo(3 * cellSize, 0);

        mPathShape = new PathShape(path, size, size);
    }

    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            invalidateSelf();
        }
    }

    public void setPadding(int padding) {
        mPadding = padding;
    }

    public int getColor() {
        return mColor;
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mPathShape.resize(bounds.width(), bounds.height());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int level = getLevel(); // [0, 10000]

        Rect bounds = getBounds();
        float degrees;
        if (level < 5000) {
            degrees = 0;
        } else if (level < 10000) {
            degrees = (level - 5000) / 5000f * 180;
        } else {
            degrees = 180;
        }

        int saveCount = canvas.save();

        float centerX = bounds.exactCenterX();
        float centerY = bounds.exactCenterY();
        canvas.rotate(degrees, centerX, centerY);

        if (mPadding > 0 && mPadding * 2 < bounds.width()) {
            float scale = 1f - mPadding * 2f / bounds.width();
            canvas.scale(scale, scale, centerX, centerY);
        }

        mPaint.setColor(mColor);
        mPathShape.draw(canvas, mPaint);

        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return getOpacityFromColor(mColor);
    }

    private static int getOpacityFromColor(int color) {
        int colorAlpha = color >>> 24;
        if (colorAlpha == 255) {
            return PixelFormat.OPAQUE;
        } else if (colorAlpha == 0) {
            return PixelFormat.TRANSPARENT;
        } else {
            return PixelFormat.TRANSLUCENT;
        }
    }

}

package com.zcool.inkstone.ext.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.zcool.inkstone.R;

import timber.log.Timber;

/**
 * 辅助处理自定义 window insets, 屏蔽版本差异 {@link #onFitInsets(int, int, int, int)}
 */
public class FitInsetsLayout extends FrameLayout {

    private static final boolean DEBUG = true;

    public FitInsetsLayout(Context context) {
        this(context, null);
    }

    public FitInsetsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitInsetsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FitInsetsLayout(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public static final int NONE = -1;
    public static final int ALL = -2;

    private int mFitInsetPaddingLeft = NONE;
    private int mFitInsetPaddingTop = NONE;
    private int mFitInsetPaddingRight = NONE;
    private int mFitInsetPaddingBottom = NONE;

    public int mPrivateFlags;

    /**
     * Flag indicating that we're in the process of dispatchApplyWindowInsets.
     */
    private static final int FLAG_DISPATCH_APPLY_WINDOW_INSETS = 0x001;
    /**
     * Flag indicating that we're in the process of fitSystemWindows.
     */
    private static final int FLAG_FIT_SYSTEM_WINDOWS = 0x002;

    protected void init(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a =
                context.obtainStyledAttributes(
                        attrs, R.styleable.FitInsetsLayout, defStyleAttr, defStyleRes);

        mFitInsetPaddingLeft =
                a.getLayoutDimension(
                        R.styleable.FitInsetsLayout_systemInsetPaddingLeft,
                        mFitInsetPaddingLeft);
        mFitInsetPaddingTop =
                a.getLayoutDimension(
                        R.styleable.FitInsetsLayout_systemInsetPaddingTop,
                        mFitInsetPaddingTop);
        mFitInsetPaddingRight =
                a.getLayoutDimension(
                        R.styleable.FitInsetsLayout_systemInsetPaddingRight,
                        mFitInsetPaddingRight);
        mFitInsetPaddingBottom =
                a.getLayoutDimension(
                        R.styleable.FitInsetsLayout_systemInsetPaddingBottom,
                        mFitInsetPaddingBottom);

        a.recycle();

        if (DEBUG) {
            Timber.d("fit inset padding %s", getFitInsetPadding());
        }
    }

    @NonNull
    public Rect getFitInsetPadding() {
        return new Rect(
                mFitInsetPaddingLeft,
                mFitInsetPaddingTop,
                mFitInsetPaddingRight,
                mFitInsetPaddingBottom);
    }

    public void setFitInsetPadding(int left, int top, int right, int bottom) {
        if (mFitInsetPaddingLeft != left
                || mFitInsetPaddingTop != top
                || mFitInsetPaddingRight != right
                || mFitInsetPaddingBottom != bottom) {
            mFitInsetPaddingLeft = left;
            mFitInsetPaddingTop = top;
            mFitInsetPaddingRight = right;
            mFitInsetPaddingBottom = bottom;
            ViewCompat.requestApplyInsets(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        if ((mPrivateFlags & FLAG_FIT_SYSTEM_WINDOWS) != 0) {
            // start call from #fitSystemWindows
            return super.dispatchApplyWindowInsets(insets);
        }

        if ((mPrivateFlags & FLAG_DISPATCH_APPLY_WINDOW_INSETS) != 0) {
            // looper call from this method
            return super.dispatchApplyWindowInsets(insets);
        }

        try {
            mPrivateFlags |= FLAG_DISPATCH_APPLY_WINDOW_INSETS;

            int insetLeft = insets.getSystemWindowInsetLeft();
            int insetTop = insets.getSystemWindowInsetTop();
            int insetRight = insets.getSystemWindowInsetRight();
            int insetBottom = insets.getSystemWindowInsetBottom();

            Rect remain = onFitInsets(insetLeft, insetTop, insetRight, insetBottom);

            insets = insets.replaceSystemWindowInsets(remain.left, remain.top, remain.right, remain.bottom);

            return super.dispatchApplyWindowInsets(insets);
        } finally {
            mPrivateFlags &= ~FLAG_DISPATCH_APPLY_WINDOW_INSETS;
        }
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if ((mPrivateFlags & FLAG_DISPATCH_APPLY_WINDOW_INSETS) != 0) {
            // start call from #dispatchApplyWindowInsets
            return super.fitSystemWindows(insets);
        }

        if ((mPrivateFlags & FLAG_FIT_SYSTEM_WINDOWS) != 0) {
            // looper call from this method
            return super.fitSystemWindows(insets);
        }

        try {
            mPrivateFlags |= FLAG_FIT_SYSTEM_WINDOWS;

            int insetLeft = insets.left;
            int insetTop = insets.top;
            int insetRight = insets.right;
            int insetBottom = insets.bottom;

            Rect remain = onFitInsets(insetLeft, insetTop, insetRight, insetBottom);

            insets.set(remain);

            return super.fitSystemWindows(insets);
        } finally {
            mPrivateFlags &= ~FLAG_FIT_SYSTEM_WINDOWS;
        }
    }

    /**
     * @return 返回剩余的 insets 值
     */
    protected Rect onFitInsets(int left, int top, int right, int bottom) {
        Rect insetsPadding = new Rect();

        insetsPadding.left = calculateInsetPaddingValueConsumed(left, mFitInsetPaddingLeft);
        insetsPadding.top = calculateInsetPaddingValueConsumed(top, mFitInsetPaddingTop);
        insetsPadding.right = calculateInsetPaddingValueConsumed(right, mFitInsetPaddingRight);
        insetsPadding.bottom = calculateInsetPaddingValueConsumed(bottom, mFitInsetPaddingBottom);

        setPadding(
                insetsPadding.left, insetsPadding.top, insetsPadding.right, insetsPadding.bottom);

        Rect remain =
                new Rect(
                        left - insetsPadding.left,
                        top - insetsPadding.top,
                        right - insetsPadding.right,
                        bottom - insetsPadding.bottom);

        if (DEBUG) {
            Timber.d(
                    "onFitInsets %s -> consumed:%s remain:%s target:%s",
                    new Rect(left, top, right, bottom),
                    insetsPadding,
                    remain,
                    getFitInsetPadding());
        }

        return remain;
    }

    private int calculateInsetPaddingValueConsumed(int value, int target) {
        int consumed = 0;
        int consumedTarget = NONE;
        if (value > 0) {
            if (target != NONE) {
                if (target == ALL) {
                    consumedTarget = value;
                } else if (target >= 0) {
                    consumedTarget = target;
                } else {
                    throw new IllegalArgumentException("invalid target value " + target);
                }
            }

            if (consumedTarget >= 0) {
                consumed = Math.min(consumedTarget, value);
            }
        }

        return consumed;
    }

}

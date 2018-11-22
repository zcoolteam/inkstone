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

import java.util.Arrays;

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

    private interface Index {
        int LEFT = 0;
        int TOP = 1;
        int RIGHT = 2;
        int BOTTOM = 3;
    }

    public static final int NONE = -1;
    public static final int ALL = -2;

    private int mFitInsetPaddingLeft = NONE;
    private int mFitInsetPaddingTop = NONE;
    private int mFitInsetPaddingRight = NONE;
    private int mFitInsetPaddingBottom = NONE;

    private boolean mFitInsetPaddingLeftNotSet = false; // 是否将 left 值设置到 padding
    private boolean mFitInsetPaddingLeftNotConsume = false; // 是否将 left 值从输入值中减少

    private boolean mFitInsetPaddingTopNotSet = false; // 是否将 top 值设置到 padding
    private boolean mFitInsetPaddingTopNotConsume = false;// 是否将 top 值从输入值中减少

    private boolean mFitInsetPaddingRightNotSet = false; // 是否将 right 值设置到 padding
    private boolean mFitInsetPaddingRightNotConsume = false;// 是否将 right 值从输入值中减少

    private boolean mFitInsetPaddingBottomNotSet = false; // 是否将 bottom 值设置到 padding
    private boolean mFitInsetPaddingBottomNotConsume = false;// 是否将 bottom 值从输入值中减少

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

        mFitInsetPaddingLeftNotSet = a.getBoolean(R.styleable.FitInsetsLayout_systemInsetPaddingLeftNotSet, mFitInsetPaddingLeftNotSet);
        mFitInsetPaddingTopNotSet = a.getBoolean(R.styleable.FitInsetsLayout_systemInsetPaddingTopNotSet, mFitInsetPaddingTopNotSet);
        mFitInsetPaddingRightNotSet = a.getBoolean(R.styleable.FitInsetsLayout_systemInsetPaddingRightNotSet, mFitInsetPaddingRightNotSet);
        mFitInsetPaddingBottomNotSet = a.getBoolean(R.styleable.FitInsetsLayout_systemInsetPaddingBottomNotSet, mFitInsetPaddingBottomNotSet);

        mFitInsetPaddingLeftNotConsume = a.getBoolean(R.styleable.FitInsetsLayout_systemInsetPaddingLeftNotConsume, mFitInsetPaddingLeftNotConsume);
        mFitInsetPaddingTopNotConsume = a.getBoolean(R.styleable.FitInsetsLayout_systemInsetPaddingTopNotConsume, mFitInsetPaddingTopNotConsume);
        mFitInsetPaddingRightNotConsume = a.getBoolean(R.styleable.FitInsetsLayout_systemInsetPaddingRightNotConsume, mFitInsetPaddingRightNotConsume);
        mFitInsetPaddingBottomNotConsume = a.getBoolean(R.styleable.FitInsetsLayout_systemInsetPaddingBottomNotConsume, mFitInsetPaddingBottomNotConsume);

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

    public boolean[] getFitInsetPaddingNotSet() {
        return new boolean[]{
                mFitInsetPaddingLeftNotSet,
                mFitInsetPaddingTopNotSet,
                mFitInsetPaddingRightNotSet,
                mFitInsetPaddingBottomNotSet};
    }

    public void setFitInsetPaddingNotSet(boolean left, boolean top, boolean right, boolean bottom) {
        if (mFitInsetPaddingLeftNotSet != left
                || mFitInsetPaddingTopNotSet != top
                || mFitInsetPaddingRightNotSet != right
                || mFitInsetPaddingBottomNotSet != bottom) {
            mFitInsetPaddingLeftNotSet = left;
            mFitInsetPaddingTopNotSet = top;
            mFitInsetPaddingRightNotSet = right;
            mFitInsetPaddingBottomNotSet = bottom;
            ViewCompat.requestApplyInsets(this);
        }
    }

    public boolean[] getFitInsetPaddingNotConsume() {
        return new boolean[]{
                mFitInsetPaddingLeftNotConsume,
                mFitInsetPaddingTopNotConsume,
                mFitInsetPaddingRightNotConsume,
                mFitInsetPaddingBottomNotConsume};
    }

    public void setFitInsetPaddingNotConsume(boolean left, boolean top, boolean right, boolean bottom) {
        if (mFitInsetPaddingLeftNotConsume != left
                || mFitInsetPaddingTopNotConsume != top
                || mFitInsetPaddingRightNotConsume != right
                || mFitInsetPaddingBottomNotConsume != bottom) {
            mFitInsetPaddingLeftNotConsume = left;
            mFitInsetPaddingTopNotConsume = top;
            mFitInsetPaddingRightNotConsume = right;
            mFitInsetPaddingBottomNotConsume = bottom;
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

            Rect remain = dispatchFitInsets(insetLeft, insetTop, insetRight, insetBottom);

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

            Rect remain = dispatchFitInsets(insetLeft, insetTop, insetRight, insetBottom);

            insets.set(remain);

            return super.fitSystemWindows(insets);
        } finally {
            mPrivateFlags &= ~FLAG_FIT_SYSTEM_WINDOWS;
        }
    }

    private final Rect mLastInsets = new Rect();

    public Rect getLastInsets() {
        return new Rect(mLastInsets);
    }

    /**
     * @return 返回剩余的 insets 值
     */
    protected Rect dispatchFitInsets(int left, int top, int right, int bottom) {
        mLastInsets.set(left, top, right, bottom);
        return onFitInsets(left, top, right, bottom);
    }

    /**
     * @return 返回剩余的 insets 值
     */
    protected Rect onFitInsets(int left, int top, int right, int bottom) {
        Rect targetInsets = new Rect();

        targetInsets.left = calculateTargetInsetPaddingValue(left, mFitInsetPaddingLeft);
        targetInsets.top = calculateTargetInsetPaddingValue(top, mFitInsetPaddingTop);
        targetInsets.right = calculateTargetInsetPaddingValue(right, mFitInsetPaddingRight);
        targetInsets.bottom = calculateTargetInsetPaddingValue(bottom, mFitInsetPaddingBottom);

        // adjust padding
        boolean[] notSet = getFitInsetPaddingNotSet();
        Rect padding = new Rect();
        padding.left = notSet[Index.LEFT] ? 0 : targetInsets.left;
        padding.top = notSet[Index.TOP] ? 0 : targetInsets.top;
        padding.right = notSet[Index.RIGHT] ? 0 : targetInsets.right;
        padding.bottom = notSet[Index.BOTTOM] ? 0 : targetInsets.bottom;
        setPadding(padding.left, padding.top, padding.right, padding.bottom);

        // adjust remain
        boolean[] notConsume = getFitInsetPaddingNotConsume();
        Rect remain = new Rect();
        remain.left = notConsume[Index.LEFT] ? left : left - targetInsets.left;
        remain.top = notConsume[Index.TOP] ? top : top - targetInsets.top;
        remain.right = notConsume[Index.RIGHT] ? right : right - targetInsets.right;
        remain.bottom = notConsume[Index.BOTTOM] ? bottom : bottom - targetInsets.bottom;

        if (DEBUG) {
            Timber.v(
                    "onFitInsets:%s=> targetInsets:%s->%s padding:%s->%s remain:%s->%s",
                    new Rect(left, top, right, bottom),
                    getFitInsetPadding(), targetInsets,
                    Arrays.toString(notSet), padding,
                    Arrays.toString(notConsume), remain);
        }

        return remain;
    }


    private int calculateTargetInsetPaddingValue(int value, int target) {
        int resultValue = 0;
        int resultValueTarget = NONE;
        if (value > 0) {
            if (target != NONE) {
                if (target == ALL) {
                    resultValueTarget = value;
                } else if (target >= 0) {
                    resultValueTarget = target;
                } else {
                    throw new IllegalArgumentException("invalid target value " + target);
                }
            }

            if (resultValueTarget >= 0) {
                resultValue = Math.min(resultValueTarget, value);
            }
        }

        return resultValue;
    }

}

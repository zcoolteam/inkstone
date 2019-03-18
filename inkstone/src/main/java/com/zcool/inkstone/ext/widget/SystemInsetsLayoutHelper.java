package com.zcool.inkstone.ext.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

import com.zcool.inkstone.Debug;
import com.zcool.inkstone.R;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import timber.log.Timber;

/**
 * 辅助处理自定义 window insets, 屏蔽版本差异 {@link #onSystemInsets(int, int, int, int)}
 */
public class SystemInsetsLayoutHelper {

    private final boolean DEBUG = Debug.isDebugWidget();

    private interface Index {
        int LEFT = 0;
        int TOP = 1;
        int RIGHT = 2;
        int BOTTOM = 3;
    }

    public static final int NONE = -1;
    public static final int ALL = -2;

    private final View mView;
    private final SystemInsetsLayout mSystemInsetsLayout;

    private int mSystemInsetPaddingLeft = NONE;
    private int mSystemInsetPaddingTop = NONE;
    private int mSystemInsetPaddingRight = NONE;
    private int mSystemInsetPaddingBottom = NONE;

    private boolean mSystemInsetPaddingLeftNotApply = false; // 是否不设置 left 值到 padding
    private boolean mSystemInsetPaddingLeftNotConsume = false; // 是否不消耗输入的 left 值

    private boolean mSystemInsetPaddingTopNotApply = false; // 是否不设置 top 值到 padding
    private boolean mSystemInsetPaddingTopNotConsume = false;// 是否不消耗输入的 top 值

    private boolean mSystemInsetPaddingRightNotApply = false; // 是否不设置 right 值到 padding
    private boolean mSystemInsetPaddingRightNotConsume = false;// 是否不消耗输入的 right 值

    private boolean mSystemInsetPaddingBottomNotApply = false; // 是否不设置 bottom 值到 padding
    private boolean mSystemInsetPaddingBottomNotConsume = false;// 是否不消耗输入的 bottom 值

    public int mPrivateFlags;

    /**
     * Flag indicating that we're in the process of dispatchApplyWindowInsets.
     */
    private static final int FLAG_DISPATCH_APPLY_WINDOW_INSETS = 0x001;
    /**
     * Flag indicating that we're in the process of fitSystemWindows.
     */
    private static final int FLAG_FIT_SYSTEM_WINDOWS = 0x002;

    public SystemInsetsLayoutHelper(View view) {
        mView = view;
        if (!(mView instanceof SystemInsetsLayout)) {
            throw new IllegalArgumentException("view need implement SystemInsetsLayout");
        }
        mSystemInsetsLayout = (SystemInsetsLayout) mView;
    }

    public void init(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a =
                context.obtainStyledAttributes(
                        attrs, R.styleable.SystemInsetsLayout, defStyleAttr, defStyleRes);

        mSystemInsetPaddingLeft =
                a.getLayoutDimension(
                        R.styleable.SystemInsetsLayout_systemInsetPaddingLeft,
                        mSystemInsetPaddingLeft);
        mSystemInsetPaddingTop =
                a.getLayoutDimension(
                        R.styleable.SystemInsetsLayout_systemInsetPaddingTop,
                        mSystemInsetPaddingTop);
        mSystemInsetPaddingRight =
                a.getLayoutDimension(
                        R.styleable.SystemInsetsLayout_systemInsetPaddingRight,
                        mSystemInsetPaddingRight);
        mSystemInsetPaddingBottom =
                a.getLayoutDimension(
                        R.styleable.SystemInsetsLayout_systemInsetPaddingBottom,
                        mSystemInsetPaddingBottom);

        mSystemInsetPaddingLeftNotApply = a.getBoolean(R.styleable.SystemInsetsLayout_systemInsetPaddingLeftNotApply, mSystemInsetPaddingLeftNotApply);
        mSystemInsetPaddingTopNotApply = a.getBoolean(R.styleable.SystemInsetsLayout_systemInsetPaddingTopNotApply, mSystemInsetPaddingTopNotApply);
        mSystemInsetPaddingRightNotApply = a.getBoolean(R.styleable.SystemInsetsLayout_systemInsetPaddingRightNotApply, mSystemInsetPaddingRightNotApply);
        mSystemInsetPaddingBottomNotApply = a.getBoolean(R.styleable.SystemInsetsLayout_systemInsetPaddingBottomNotApply, mSystemInsetPaddingBottomNotApply);

        mSystemInsetPaddingLeftNotConsume = a.getBoolean(R.styleable.SystemInsetsLayout_systemInsetPaddingLeftNotConsume, mSystemInsetPaddingLeftNotConsume);
        mSystemInsetPaddingTopNotConsume = a.getBoolean(R.styleable.SystemInsetsLayout_systemInsetPaddingTopNotConsume, mSystemInsetPaddingTopNotConsume);
        mSystemInsetPaddingRightNotConsume = a.getBoolean(R.styleable.SystemInsetsLayout_systemInsetPaddingRightNotConsume, mSystemInsetPaddingRightNotConsume);
        mSystemInsetPaddingBottomNotConsume = a.getBoolean(R.styleable.SystemInsetsLayout_systemInsetPaddingBottomNotConsume, mSystemInsetPaddingBottomNotConsume);

        a.recycle();

        if (DEBUG) {
            Timber.d("fit inset padding %s", getSystemInsetsPadding());
        }
    }

    @NonNull
    public Rect getSystemInsetsPadding() {
        return new Rect(
                mSystemInsetPaddingLeft,
                mSystemInsetPaddingTop,
                mSystemInsetPaddingRight,
                mSystemInsetPaddingBottom);
    }

    public void setSystemInsetsPadding(int left, int top, int right, int bottom) {
        if (mSystemInsetPaddingLeft != left
                || mSystemInsetPaddingTop != top
                || mSystemInsetPaddingRight != right
                || mSystemInsetPaddingBottom != bottom) {
            mSystemInsetPaddingLeft = left;
            mSystemInsetPaddingTop = top;
            mSystemInsetPaddingRight = right;
            mSystemInsetPaddingBottom = bottom;
            ViewCompat.requestApplyInsets(mView);
        }
    }

    @NonNull
    public boolean[] getSystemInsetsPaddingNotApply() {
        return new boolean[]{
                mSystemInsetPaddingLeftNotApply,
                mSystemInsetPaddingTopNotApply,
                mSystemInsetPaddingRightNotApply,
                mSystemInsetPaddingBottomNotApply};
    }

    @NonNull
    public void setSystemInsetsPaddingNotApply(boolean left, boolean top, boolean right, boolean bottom) {
        if (mSystemInsetPaddingLeftNotApply != left
                || mSystemInsetPaddingTopNotApply != top
                || mSystemInsetPaddingRightNotApply != right
                || mSystemInsetPaddingBottomNotApply != bottom) {
            mSystemInsetPaddingLeftNotApply = left;
            mSystemInsetPaddingTopNotApply = top;
            mSystemInsetPaddingRightNotApply = right;
            mSystemInsetPaddingBottomNotApply = bottom;
            ViewCompat.requestApplyInsets(mView);
        }
    }

    @NonNull
    public boolean[] getFitInsetPaddingNotConsume() {
        return new boolean[]{
                mSystemInsetPaddingLeftNotConsume,
                mSystemInsetPaddingTopNotConsume,
                mSystemInsetPaddingRightNotConsume,
                mSystemInsetPaddingBottomNotConsume};
    }

    public void setFitInsetPaddingNotConsume(boolean left, boolean top, boolean right, boolean bottom) {
        if (mSystemInsetPaddingLeftNotConsume != left
                || mSystemInsetPaddingTopNotConsume != top
                || mSystemInsetPaddingRightNotConsume != right
                || mSystemInsetPaddingBottomNotConsume != bottom) {
            mSystemInsetPaddingLeftNotConsume = left;
            mSystemInsetPaddingTopNotConsume = top;
            mSystemInsetPaddingRightNotConsume = right;
            mSystemInsetPaddingBottomNotConsume = bottom;
            ViewCompat.requestApplyInsets(mView);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        if ((mPrivateFlags & FLAG_FIT_SYSTEM_WINDOWS) != 0) {
            // start call from #fitSystemWindows
            return mSystemInsetsLayout.callSuperDispatchApplyWindowInsets(insets);
        }

        if ((mPrivateFlags & FLAG_DISPATCH_APPLY_WINDOW_INSETS) != 0) {
            // looper call from this method
            return mSystemInsetsLayout.callSuperDispatchApplyWindowInsets(insets);
        }

        try {
            mPrivateFlags |= FLAG_DISPATCH_APPLY_WINDOW_INSETS;

            int insetLeft = insets.getSystemWindowInsetLeft();
            int insetTop = insets.getSystemWindowInsetTop();
            int insetRight = insets.getSystemWindowInsetRight();
            int insetBottom = insets.getSystemWindowInsetBottom();

            Rect remain = dispatchSystemInsets(insetLeft, insetTop, insetRight, insetBottom);

            insets = insets.replaceSystemWindowInsets(remain.left, remain.top, remain.right, remain.bottom);

            return mSystemInsetsLayout.callSuperDispatchApplyWindowInsets(insets);
        } finally {
            mPrivateFlags &= ~FLAG_DISPATCH_APPLY_WINDOW_INSETS;
        }
    }

    public boolean fitSystemWindows(Rect insets) {
        if ((mPrivateFlags & FLAG_DISPATCH_APPLY_WINDOW_INSETS) != 0) {
            // start call from #dispatchApplyWindowInsets
            return mSystemInsetsLayout.callSuperFitSystemWindows(insets);
        }

        if ((mPrivateFlags & FLAG_FIT_SYSTEM_WINDOWS) != 0) {
            // looper call from this method
            return mSystemInsetsLayout.callSuperFitSystemWindows(insets);
        }

        try {
            mPrivateFlags |= FLAG_FIT_SYSTEM_WINDOWS;

            int insetLeft = insets.left;
            int insetTop = insets.top;
            int insetRight = insets.right;
            int insetBottom = insets.bottom;

            Rect remain = dispatchSystemInsets(insetLeft, insetTop, insetRight, insetBottom);

            insets.set(remain);

            return mSystemInsetsLayout.callSuperFitSystemWindows(insets);
        } finally {
            mPrivateFlags &= ~FLAG_FIT_SYSTEM_WINDOWS;
        }
    }

    private final Rect mLastSystemInsets = new Rect();

    public Rect getLastSystemInsets() {
        return new Rect(mLastSystemInsets);
    }

    /**
     * @return 返回剩余的 insets 值
     */
    public Rect dispatchSystemInsets(int left, int top, int right, int bottom) {
        mLastSystemInsets.set(left, top, right, bottom);
        return onSystemInsets(left, top, right, bottom);
    }

    /**
     * @return 返回剩余的 insets 值
     */
    public Rect onSystemInsets(int left, int top, int right, int bottom) {
        Rect targetInsets = new Rect();

        targetInsets.left = calculateTargetInsetPaddingValue(left, mSystemInsetPaddingLeft);
        targetInsets.top = calculateTargetInsetPaddingValue(top, mSystemInsetPaddingTop);
        targetInsets.right = calculateTargetInsetPaddingValue(right, mSystemInsetPaddingRight);
        targetInsets.bottom = calculateTargetInsetPaddingValue(bottom, mSystemInsetPaddingBottom);

        // apply padding
        boolean[] notApply = getSystemInsetsPaddingNotApply();
        Rect padding = new Rect();
        padding.left = notApply[Index.LEFT] ? 0 : targetInsets.left;
        padding.top = notApply[Index.TOP] ? 0 : targetInsets.top;
        padding.right = notApply[Index.RIGHT] ? 0 : targetInsets.right;
        padding.bottom = notApply[Index.BOTTOM] ? 0 : targetInsets.bottom;
        mView.setPadding(padding.left, padding.top, padding.right, padding.bottom);

        // adjust remain
        boolean[] notConsume = getFitInsetPaddingNotConsume();
        Rect remain = new Rect();
        remain.left = notConsume[Index.LEFT] ? left : left - targetInsets.left;
        remain.top = notConsume[Index.TOP] ? top : top - targetInsets.top;
        remain.right = notConsume[Index.RIGHT] ? right : right - targetInsets.right;
        remain.bottom = notConsume[Index.BOTTOM] ? bottom : bottom - targetInsets.bottom;

        if (DEBUG) {
            Timber.v(
                    "onSystemInsets:%s=> targetInsets:%s->%s padding:%s->%s remain:%s->%s",
                    new Rect(left, top, right, bottom),
                    getSystemInsetsPadding(), targetInsets,
                    Arrays.toString(notApply), padding,
                    Arrays.toString(notConsume), remain);
        }

        return remain;
    }

    protected int calculateTargetInsetPaddingValue(int value, int target) {
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

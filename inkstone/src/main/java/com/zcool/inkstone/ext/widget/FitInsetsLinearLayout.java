package com.zcool.inkstone.ext.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.LinearLayout;

/**
 * 辅助处理自定义 window insets
 */
public class FitInsetsLinearLayout extends LinearLayout implements FitInsetsLayoutInterface {

    private final FitInsetsLayoutHelper mFitInsetsLayoutHelper;

    public FitInsetsLinearLayout(Context context) {
        this(context, null);
    }

    public FitInsetsLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitInsetsLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFitInsetsLayoutHelper = createFitInsetsLayoutHelper();
        mFitInsetsLayoutHelper.init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FitInsetsLinearLayout(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mFitInsetsLayoutHelper = createFitInsetsLayoutHelper();
        mFitInsetsLayoutHelper.init(context, attrs, defStyleAttr, defStyleRes);
    }

    protected FitInsetsLayoutHelper createFitInsetsLayoutHelper() {
        return new FitInsetsLayoutHelper(this);
    }

    @Override
    @NonNull
    public Rect getFitInsetPadding() {
        return mFitInsetsLayoutHelper.getFitInsetPadding();
    }

    @Override
    public void setFitInsetPadding(int left, int top, int right, int bottom) {
        mFitInsetsLayoutHelper.setFitInsetPadding(left, top, right, bottom);
    }

    @Override
    @NonNull
    public boolean[] getFitInsetPaddingNotSet() {
        return mFitInsetsLayoutHelper.getFitInsetPaddingNotSet();
    }

    @Override
    public void setFitInsetPaddingNotSet(boolean left, boolean top, boolean right, boolean bottom) {
        mFitInsetsLayoutHelper.setFitInsetPaddingNotSet(left, true, right, bottom);
    }

    @NonNull
    @Override
    public boolean[] getFitInsetPaddingNotConsume() {
        return mFitInsetsLayoutHelper.getFitInsetPaddingNotConsume();
    }

    public void setFitInsetPaddingNotConsume(boolean left, boolean top, boolean right, boolean bottom) {
        mFitInsetsLayoutHelper.setFitInsetPaddingNotConsume(left, top, right, bottom);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        return mFitInsetsLayoutHelper.dispatchApplyWindowInsets(insets);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets callSuperDispatchApplyWindowInsets(WindowInsets insets) {
        return super.dispatchApplyWindowInsets(insets);
    }

    @Override
    public boolean fitSystemWindows(Rect insets) {
        return mFitInsetsLayoutHelper.fitSystemWindows(insets);
    }

    @Override
    public boolean callSuperFitSystemWindows(Rect insets) {
        return super.fitSystemWindows(insets);
    }

    @NonNull
    @Override
    public Rect getLastInsets() {
        return mFitInsetsLayoutHelper.getLastInsets();
    }

}

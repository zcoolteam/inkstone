package com.zcool.inkstone.ext.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 辅助处理自定义 window insets
 */
public class SystemInsetsLinearLayout extends LinearLayout implements SystemInsetsLayout {

    private final SystemInsetsLayoutHelper mSystemInsetsLayoutHelper;

    public SystemInsetsLinearLayout(Context context) {
        this(context, null);
    }

    public SystemInsetsLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SystemInsetsLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSystemInsetsLayoutHelper = createFitInsetsLayoutHelper();
        mSystemInsetsLayoutHelper.init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SystemInsetsLinearLayout(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mSystemInsetsLayoutHelper = createFitInsetsLayoutHelper();
        mSystemInsetsLayoutHelper.init(context, attrs, defStyleAttr, defStyleRes);
    }

    protected SystemInsetsLayoutHelper createFitInsetsLayoutHelper() {
        return new SystemInsetsLayoutHelper(this);
    }

    @Override
    @NonNull
    public Rect getSystemInsetsPadding() {
        return mSystemInsetsLayoutHelper.getSystemInsetsPadding();
    }

    @Override
    public void setSystemInsetsPadding(int left, int top, int right, int bottom) {
        mSystemInsetsLayoutHelper.setSystemInsetsPadding(left, top, right, bottom);
    }

    @Override
    @NonNull
    public boolean[] getSystemInsetsPaddingNotApply() {
        return mSystemInsetsLayoutHelper.getSystemInsetsPaddingNotApply();
    }

    @Override
    public void setSystemInsetsPaddingNotApply(boolean left, boolean top, boolean right, boolean bottom) {
        mSystemInsetsLayoutHelper.setSystemInsetsPaddingNotApply(left, true, right, bottom);
    }

    @NonNull
    @Override
    public boolean[] getSystemInsetsPaddingNotConsume() {
        return mSystemInsetsLayoutHelper.getFitInsetPaddingNotConsume();
    }

    public void setSystemInsetsPaddingNotConsume(boolean left, boolean top, boolean right, boolean bottom) {
        mSystemInsetsLayoutHelper.setFitInsetPaddingNotConsume(left, top, right, bottom);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        return mSystemInsetsLayoutHelper.dispatchApplyWindowInsets(insets);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets callSuperDispatchApplyWindowInsets(WindowInsets insets) {
        return super.dispatchApplyWindowInsets(insets);
    }

    @Override
    public boolean fitSystemWindows(Rect insets) {
        return mSystemInsetsLayoutHelper.fitSystemWindows(insets);
    }

    @Override
    public boolean callSuperFitSystemWindows(Rect insets) {
        return super.fitSystemWindows(insets);
    }

    @NonNull
    @Override
    public Rect getLastSystemInsets() {
        return mSystemInsetsLayoutHelper.getLastSystemInsets();
    }

}

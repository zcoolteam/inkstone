package com.zcool.inkstone.ext.widget;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.view.WindowInsets;

import androidx.annotation.NonNull;

/**
 * 辅助处理自定义 window insets
 *
 * @see SystemInsetsLayoutHelper
 */
public interface SystemInsetsLayout {

    @NonNull
    Rect getSystemInsetsPadding();

    void setSystemInsetsPadding(int left, int top, int right, int bottom);

    @NonNull
    boolean[] getSystemInsetsPaddingNotApply();

    void setSystemInsetsPaddingNotApply(boolean left, boolean top, boolean right, boolean bottom);

    @NonNull
    boolean[] getSystemInsetsPaddingNotConsume();

    void setSystemInsetsPaddingNotConsume(boolean left, boolean top, boolean right, boolean bottom);

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    WindowInsets dispatchApplyWindowInsets(WindowInsets insets);

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    WindowInsets callSuperDispatchApplyWindowInsets(WindowInsets insets);

    boolean fitSystemWindows(Rect insets);

    boolean callSuperFitSystemWindows(Rect insets);

    @NonNull
    Rect getLastSystemInsets();

}

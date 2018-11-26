package com.zcool.inkstone.ext.widget;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.WindowInsets;

/**
 * 辅助处理自定义 window insets
 *
 * @see FitInsetsLayoutHelper
 */
public interface FitInsetsLayoutInterface {

    @NonNull
    Rect getFitInsetPadding();

    void setFitInsetPadding(int left, int top, int right, int bottom);

    @NonNull
    boolean[] getFitInsetPaddingNotSet();

    void setFitInsetPaddingNotSet(boolean left, boolean top, boolean right, boolean bottom);

    @NonNull
    boolean[] getFitInsetPaddingNotConsume();

    void setFitInsetPaddingNotConsume(boolean left, boolean top, boolean right, boolean bottom);

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    WindowInsets dispatchApplyWindowInsets(WindowInsets insets);

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    WindowInsets callSuperDispatchApplyWindowInsets(WindowInsets insets);

    boolean fitSystemWindows(Rect insets);

    boolean callSuperFitSystemWindows(Rect insets);

    @NonNull
    Rect getLastInsets();

}

package com.zcool.inkstone.ext.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewCompatHelper;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.zcool.inkstone.Debug;
import com.zcool.inkstone.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * 辅助处理自定义 window insets。使 window insets 在每一个 child view 都拥有等同的分发值。
 */
public class SystemInsetsLayerLayout extends FrameLayout {

    private final boolean DEBUG = Debug.isDebugWidget();

    public SystemInsetsLayerLayout(Context context) {
        this(context, null);
    }

    public SystemInsetsLayerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SystemInsetsLayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SystemInsetsLayerLayout(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private static final int RESULT_ALWAYS_TRUE = 0;
    private static final int RESULT_ALWAYS_FALSE = 1;
    private static final int RESULT_MERGE_CHILD = 2;

    @IntDef({RESULT_ALWAYS_TRUE, RESULT_ALWAYS_FALSE, RESULT_MERGE_CHILD})
    @Retention(RetentionPolicy.SOURCE)
    private @interface DispatchResult {
    }

    @DispatchResult
    private int mSystemInsetsLayerDispatchResult = RESULT_MERGE_CHILD;

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a =
                context.obtainStyledAttributes(
                        attrs, R.styleable.SystemInsetsLayerLayout, defStyleAttr, defStyleRes);

        mSystemInsetsLayerDispatchResult = a.getLayoutDimension(
                R.styleable.SystemInsetsLayerLayout_systemInsetsLayerDispatchResult,
                mSystemInsetsLayerDispatchResult);

        a.recycle();

        if (mSystemInsetsLayerDispatchResult != RESULT_ALWAYS_TRUE
                && mSystemInsetsLayerDispatchResult != RESULT_ALWAYS_FALSE
                && mSystemInsetsLayerDispatchResult != RESULT_MERGE_CHILD) {
            throw new IllegalArgumentException("invalid [SystemInsetsLayerLayout_systemInsetsLayerDispatchResult] " + mSystemInsetsLayerDispatchResult);
        }

        if (DEBUG) {
            Timber.d("system insets layer dispatch result %s", mSystemInsetsLayerDispatchResult);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        boolean mergeSystemWindowInsetConsumed = false;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            WindowInsets childInsets = getChildAt(i).dispatchApplyWindowInsets(new WindowInsets(insets));
            boolean systemWindowInsetConsumed = childInsets.getSystemWindowInsetLeft() == 0
                    && childInsets.getSystemWindowInsetTop() == 0
                    && childInsets.getSystemWindowInsetRight() == 0
                    && childInsets.getSystemWindowInsetBottom() == 0;
            mergeSystemWindowInsetConsumed |= systemWindowInsetConsumed;
        }

        if (mSystemInsetsLayerDispatchResult == RESULT_ALWAYS_TRUE) {
            return insets.consumeSystemWindowInsets();
        }

        if (mSystemInsetsLayerDispatchResult == RESULT_ALWAYS_FALSE) {
            return insets;
        }

        // RESULT_MERGE_CHILD

        if (mergeSystemWindowInsetConsumed) {
            return insets.consumeSystemWindowInsets();
        }
        return insets;
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        boolean mergeSystemWindowInsetConsumed = false;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            mergeSystemWindowInsetConsumed |= ViewCompatHelper.callFitSystemWindows(getChildAt(i), new Rect(insets));
        }

        if (mSystemInsetsLayerDispatchResult == RESULT_ALWAYS_TRUE) {
            return true;
        }

        if (mSystemInsetsLayerDispatchResult == RESULT_ALWAYS_FALSE) {
            return false;
        }

        // RESULT_MERGE_CHILD

        return mergeSystemWindowInsetConsumed;
    }

}

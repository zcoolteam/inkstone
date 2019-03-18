package com.zcool.inkstone.ext.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowInsets;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.zcool.inkstone.Debug;

import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import timber.log.Timber;

public class MinHeightInsetsCollapsingToolbarLayout extends CollapsingToolbarLayout implements SystemInsetsLayout {

    private final boolean DEBUG = Debug.isDebugWidget();

    private final SystemInsetsLayoutHelper mSystemInsetsLayoutHelper;

    public MinHeightInsetsCollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public MinHeightInsetsCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MinHeightInsetsCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ViewCompat.setOnApplyWindowInsetsListener(this, null);
        mSystemInsetsLayoutHelper = createFitInsetsLayoutHelper();
        mSystemInsetsLayoutHelper.init(context, attrs, defStyleAttr, 0);
    }

    @Override
    public int getMinimumHeight() {
        int insetsTop = getLastSystemInsets().top;
        int childCount = getChildCount();
        if (childCount > 0) {
            int childMinHeight = getChildAt(0).getMinimumHeight();
            insetsTop += childMinHeight;
        }

        return insetsTop;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ViewParent parent = this.getParent();
        if (parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).addOnOffsetChangedListener(mOnOffsetChangedListener);
            ViewCompat.requestApplyInsets(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        ViewParent parent = this.getParent();
        if (parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(mOnOffsetChangedListener);
        }
    }

    private final AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener = (appBarLayout, verticalOffset) -> {
        if (DEBUG) {
            Timber.v("onOffsetChanged AppBarLayout totalScrollRange:%s, verticalOffset:%s, childCount:%s", appBarLayout.getTotalScrollRange(), verticalOffset, getChildCount());
        }

        int maxRange = appBarLayout.getTotalScrollRange();
        int offset = -verticalOffset;
        float progress = offset * 1f / maxRange;
        progress = MathUtils.clamp(progress, 0f, 1f);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            if (childView instanceof ProgressView) {
                int viewHeight = childView.getHeight();
                ((ProgressView) childView).onProgressUpdate(appBarLayout, verticalOffset, progress, maxRange, offset, viewHeight);
            }
        }
    };

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

    public interface ProgressView {

        /**
         * progress [0.0f, 1.0f]
         */
        void onProgressUpdate(AppBarLayout appBarLayout, int verticalOffset, float progress, int maxRange, int offset, int viewHeight);

    }

}

package com.zcool.sample.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.math.MathUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowInsets;

import com.zcool.inkstone.Debug;
import com.zcool.inkstone.ext.widget.FitInsetsLayout;
import com.zcool.inkstone.ext.widget.FitInsetsLayoutHelper;

import timber.log.Timber;

public class MinHeightInsetsCollapsingToolbarLayout extends CollapsingToolbarLayout implements FitInsetsLayout {

    private final boolean DEBUG = Debug.isDebugWidget();

    private final FitInsetsLayoutHelper mFitInsetsLayoutHelper;

    public MinHeightInsetsCollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public MinHeightInsetsCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MinHeightInsetsCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ViewCompat.setOnApplyWindowInsetsListener(this, null);
        mFitInsetsLayoutHelper = createFitInsetsLayoutHelper();
        mFitInsetsLayoutHelper.init(context, attrs, defStyleAttr, 0);
    }

    @Override
    public int getMinimumHeight() {
        int insetsTop = getLastInsets().top;
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

    private final AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
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
        }
    };

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

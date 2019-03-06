package com.zcool.sample.widget.refreshlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import timber.log.Timber;

public class PullHeader extends FrameLayout implements PullLayout.Header {

    public PullHeader(@NonNull Context context) {
        this(context, null);
    }

    public PullHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    }

    private ProgressBar mProgressBar;

    private void ensureProgressBar() {
        if (mProgressBar != null) {
            return;
        }

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof ProgressBar) {
                mProgressBar = (ProgressBar) childView;
                break;
            }
        }
    }

    @Override
    public void updateOffset(boolean refreshing, int offsetX, int offsetY, int absThreshold, int windowOffsetX, int windowOffsetY, PullLayout pullLayout) {
        ensureProgressBar();
        if (mProgressBar == null) {
            Timber.v("progress bar is null");
            return;
        }

        setTranslationX(windowOffsetX);
        setTranslationY(windowOffsetY);
    }

}

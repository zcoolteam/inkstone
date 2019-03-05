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

    private int mTotalUsedDx;
    private int mTotalUsedDy;

    @Override
    public void applyOffset(int dx, int dy, @NonNull int[] consumed, PullLayout pullLayout) {
        ensureProgressBar();
        if (mProgressBar == null) {
            Timber.v("progress bar is null");
            return;
        }

        int pullPosition = pullLayout.getPullPosition();
        if (pullPosition == PullLayout.PULL_POSITION_TOP) {
            consumed[1] = adjustOffset(-getMinimumHeight(), -getHeight(), mTotalUsedDy, dy);
            mTotalUsedDy += consumed[1];
        } else if (pullPosition == PullLayout.PULL_POSITION_BOTTOM) {
            consumed[1] = adjustOffset(getMinimumHeight(), getHeight(), mTotalUsedDy, dy);
            mTotalUsedDy += consumed[1];
        } else if (pullPosition == PullLayout.PULL_POSITION_LEFT) {
            consumed[0] = adjustOffset(-getMinimumWidth(), -getWidth(), mTotalUsedDx, dx);
            mTotalUsedDx += consumed[0];
        } else if (pullPosition == PullLayout.PULL_POSITION_RIGHT) {
            consumed[0] = adjustOffset(getMinimumWidth(), getWidth(), mTotalUsedDx, dx);
            mTotalUsedDx += consumed[0];
        }

        setTranslationX(mTotalUsedDx);
        setTranslationY(mTotalUsedDy);
    }

    protected int adjustOffset(int shortDistance, int longDistance, int current, int offset) {
        // TODO 边界
        if (longDistance < 0) {
            // longDistance < 0, shortDistance < 0
            if (current > shortDistance) {
                return offset;
            } else if (current <= longDistance) {
                return 0;
            } else {
                return (int) (0.5f * offset);
            }
        } else {
            // longDistance >= 0, shortDistance >= 0
            if (current < shortDistance) {
                return offset;
            } else if (current >= longDistance) {
                return 0;
            } else {
                return (int) (0.5f * offset);
            }
        }
    }

    @Override
    public void finishOffset(boolean refreshing, PullLayout pullLayout) {
        ensureProgressBar();
        if (mProgressBar == null) {
            Timber.v("progress bar is null");
            return;
        }

        if (!refreshing) {
            animate().translationX(0).translationY(0).setDuration(PullLayout.ANIMATION_DURATION).start();
            return;
        }

        int translationX;
        int translationY;
        int pullPosition = pullLayout.getPullPosition();
        if (pullPosition == PullLayout.PULL_POSITION_TOP) {
            translationX = 0;
            translationY = -getMinimumHeight();
        } else if (pullPosition == PullLayout.PULL_POSITION_BOTTOM) {
            translationX = 0;
            translationY = getMinimumHeight();
        } else if (pullPosition == PullLayout.PULL_POSITION_LEFT) {
            translationX = -getMinimumWidth();
            translationY = 0;
        } else if (pullPosition == PullLayout.PULL_POSITION_RIGHT) {
            translationX = getMinimumWidth();
            translationY = 0;
        } else {
            throw new RuntimeException("unknown pull position: " + pullPosition);
        }

        animate().translationX(translationX).translationY(translationY).setDuration(PullLayout.ANIMATION_DURATION).start();
    }

}

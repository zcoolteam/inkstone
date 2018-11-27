package com.zcool.sample.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zcool.inkstone.Debug;
import com.zcool.sample.R;

import timber.log.Timber;

public class ProgressViewFrameLayout extends FrameLayout implements ProgressView {

    private final boolean DEBUG = Debug.isDebugWidget();

    public ProgressViewFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ProgressViewFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressViewFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressViewFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int mLastAlpha = -1;

    @Override
    public void onProgressUpdate(AppBarLayout appBarLayout, int verticalOffset, float progress, int maxRange, int offset, int viewHeight) {
        int alpha;
        if (progress <= 0.1f) {
            alpha = 0;
        } else if (progress <= 0.9) {
            // [0.1, 0.9] -> [0, 255]
            alpha = (int) (255f * (progress - 0.1f) / (0.9f - 0.1f));
        } else {
            alpha = 255;
        }

        if (DEBUG) {
            Timber.v("verticalOffset:%s, progress:%s, maxRange:%s, offset:%s, viewHeight:%s, alpha:%s", verticalOffset, progress, maxRange, offset, viewHeight, alpha);
        }

        updateWithAlpha(alpha);
    }

    private void updateWithAlpha(int alpha) {
        if (alpha < 0) {
            if (DEBUG) {
                Timber.v("ignore invalid alpha:%s", alpha);
            }
            return;
        }

        boolean childUpdate = updateChildren();
        if (mLastAlpha == alpha && !childUpdate) {
            return;
        }

        mLastAlpha = alpha;
        if (mTitleText != null) {
            mTitleText.setTextColor(Color.argb(alpha, 0, 0, 0));
        } else {
            if (DEBUG) {
                Timber.v("mTitleText is null");
            }
        }
        if (mTitleBarBackground != null) {
            mTitleBarBackground.setBackgroundColor(Color.argb(alpha, 255, 255, 255));
        } else {
            if (DEBUG) {
                Timber.v("mTitleBarBackground is null");
            }
        }
    }

    private View mTitleBarBackground;
    private TextView mTitleText;

    private boolean updateChildren() {
        boolean update = false;

        if (mTitleBarBackground == null) {
            mTitleBarBackground = findViewById(R.id.title_bar_background);
            update |= mTitleBarBackground != null;
        }

        if (mTitleText == null) {
            mTitleText = findViewById(R.id.title_text);
            update |= mTitleText != null;
        }

        if (DEBUG) {
            Timber.v("updateChildren update:%s, childCount:%s", update, getChildCount());
        }
        return update;
    }

}

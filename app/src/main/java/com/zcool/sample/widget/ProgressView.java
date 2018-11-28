package com.zcool.sample.widget;

import com.google.android.material.appbar.AppBarLayout;

public interface ProgressView {

    /**
     * progress [0.0f, 1.0f]
     */
    void onProgressUpdate(AppBarLayout appBarLayout, int verticalOffset, float progress, int maxRange, int offset, int viewHeight);

}

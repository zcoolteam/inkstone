package com.zcool.sample.module.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zcool.inkstone.ext.widget.pulllayout.header.SimplePullHeader;
import com.zcool.inkstone.ext.widget.pulllayout.PullLayout;
import com.zcool.sample.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RefreshHeader extends SimplePullHeader {
    public RefreshHeader(@NonNull Context context) {
        super(context);
    }

    public RefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super.initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    private TextView mLoadingStatus;

    private void ensureWidgets() {
        mLoadingStatus = findViewById(R.id.loading_status);
    }

    @Override
    public void updateOffset(@NonNull PullLayout.OffsetHelper offsetHelper, boolean animating, int windowOffsetX, int windowOffsetY, @NonNull PullLayout pullLayout) {
        super.updateOffset(offsetHelper, animating, windowOffsetX, windowOffsetY, pullLayout);

        ensureWidgets();
        if (mLoadingStatus != null) {
            String statusText = "pull to refresh";
            if (pullLayout.getOffsetHelper().isRefreshSuccess()) {
                statusText = "success";
            } else if (pullLayout.isRefreshing()) {
                statusText = "loading...";
            }
            mLoadingStatus.setText(statusText);
        }
    }

}

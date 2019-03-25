package com.zcool.inkstone.ext.widget.pulllayout.header;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zcool.inkstone.ext.widget.pulllayout.PullLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

public class SimplePullHeader extends FrameLayout implements PullLayout.Header {

    public SimplePullHeader(@NonNull Context context) {
        this(context, null);
    }

    public SimplePullHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimplePullHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimplePullHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    }

    @Override
    public void updateOffset(@NonNull PullLayout.OffsetHelper offsetHelper, boolean animating, int windowOffsetX, int windowOffsetY, @NonNull PullLayout pullLayout) {
        setTranslationX(windowOffsetX);
        setTranslationY(windowOffsetY);
    }

}

package com.zcool.sample.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zcool.inkstone.ext.widget.FitInsetsFrameLayout;

public class MinHeightInsetsLayout extends FitInsetsFrameLayout {

    public MinHeightInsetsLayout(Context context) {
        super(context);
    }

    public MinHeightInsetsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MinHeightInsetsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MinHeightInsetsLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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

}

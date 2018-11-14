package com.zcool.inkstone.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.zcool.inkstone.Constants;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * View 相关辅助类
 */
public class ViewUtil {

    private ViewUtil() {
    }

    public static void onClick(View view, View.OnClickListener listener) {
        onClick(view, Constants.VIEW_CLICK_THROTTLE_MS, listener);
    }

    public static void onClick(View view, long throttleMs, View.OnClickListener listener) {
        if (view == null) {
            Timber.e("view is null");
            return;
        }

        Observable<Object> observable = RxView.clicks(view);
        if (throttleMs > 0) {
            observable = observable.throttleFirst(throttleMs, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
        }
        observable.subscribe(o -> {
            if (listener != null) {
                listener.onClick(view);
            }
        }, e -> {
            e.printStackTrace();
            Timber.e(e);
        });
    }

    @Nullable
    public static <T extends View> T findViewById(View view, @IdRes int... ids) {
        if (view == null) {
            return null;
        }

        if (ids == null || ids.length <= 0) {
            return null;
        }

        View targetView = view;
        for (int id : ids) {
            if (targetView == null) {
                break;
            }
            targetView = targetView.findViewById(id);
        }
        return (T) targetView;
    }

    /**
     * 将文本绘制在指定区域内(仅支持单行的形式), 可指定对齐方式
     */
    public static void drawText(Canvas canvas, String text, Paint paint, RectF area, int gravity) {
        float textWith = paint.measureText(text);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;

        Rect areaIn = new Rect();
        area.round(areaIn);
        Rect areaOut = new Rect();

        GravityCompat.apply(
                gravity,
                (int) Math.ceil(textWith),
                (int) Math.ceil(textHeight),
                areaIn,
                areaOut,
                ViewCompat.LAYOUT_DIRECTION_LTR);

        canvas.drawText(text, areaOut.left, areaOut.top - fontMetrics.ascent, paint);
    }

    public static void setPaddingIfChanged(View view, int left, int top, int right, int bottom) {
        if (view == null) {
            return;
        }

        if (view.getPaddingLeft() != left
                || view.getPaddingTop() != top
                || view.getPaddingRight() != right
                || view.getPaddingBottom() != bottom) {
            view.setPadding(left, top, right, bottom);
        }
    }

}

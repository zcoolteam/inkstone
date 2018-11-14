package com.zcool.inkstone.util;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;

/**
 * util for touch event
 */
public class MotionEventUtil {

    /**
     * 构建一个 cancel action MotionEvent with touch source.
     */
    public static MotionEvent createCancelTouchMotionEvent() {
        final long now = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
        event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        return event;
    }

}

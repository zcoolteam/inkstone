package com.zcool.inkstone.ext.backstack;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.Window;

import androidx.appcompat.view.WindowCallbackWrapper;

@SuppressLint("RestrictedApi")
public class WindowBackStackDispatcher extends WindowCallbackWrapper implements KeyEvent.Callback {

    private final BackStack mBackStack = new BackStack();
    private final KeyEvent.DispatcherState mDispatcherState = new KeyEvent.DispatcherState();

    private WindowBackStackDispatcher(Window.Callback wrapped) {
        super(wrapped);
    }

    public static WindowBackStackDispatcher from(Window window) {
        Window.Callback originalCallback = window.getCallback();
        if (originalCallback instanceof WindowBackStackDispatcher) {
            return (WindowBackStackDispatcher) originalCallback;
        }

        WindowBackStackDispatcher callback = new WindowBackStackDispatcher(originalCallback);
        window.setCallback(callback);
        return callback;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.dispatch(this, mDispatcherState, this)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    public BackStack getBackStack() {
        return mBackStack;
    }

    protected boolean onBackPressed() {
        return mBackStack.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mBackStack.isEmpty()) {
                event.startTracking();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            return onBackPressed();
        }

        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }
}

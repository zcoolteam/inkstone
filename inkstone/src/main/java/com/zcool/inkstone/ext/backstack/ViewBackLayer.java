package com.zcool.inkstone.ext.backstack;

import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import timber.log.Timber;

public class ViewBackLayer implements BackStack.BackLayer {

    protected final View mDecorView;
    protected final ViewGroup mParentView;
    protected final WindowBackStackDispatcher mDispatcher;

    protected boolean mCancelable = true;
    protected boolean mShown;

    public ViewBackLayer(WindowBackStackDispatcher dispatcher, View decorView, ViewGroup parentView) {
        mDispatcher = dispatcher;
        mDecorView = decorView;
        mParentView = parentView;
    }

    public ViewBackLayer setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

    public View getDecorView() {
        return mDecorView;
    }

    public ViewGroup getParentView() {
        return mParentView;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return mDecorView.findViewById(id);
    }

    @Override
    public boolean onBackPressed() {
        if (mOnBackPressedListener != null) {
            if (mOnBackPressedListener.onBackPressed()) {
                return true;
            }
        }

        if (mCancelable) {
            hide(true);
        }
        return true;
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    public boolean isShown() {
        return mShown;
    }

    public void show() {
        showInternal(true);
    }

    protected void showInternal(boolean attach) {
        if (mShown) {
            Timber.e("already shown");
            return;
        }

        mShown = true;
        mDispatcher.getBackStack().add(this);

        notifyShowListener();

        if (attach) {
            attachViewToParent();
        }
    }

    public void hide(boolean cancel) {
        hideInternal(cancel, true);
    }

    protected void hideInternal(boolean cancel, boolean detach) {
        if (!mShown) {
            Timber.e("not shown");
            return;
        }

        mShown = false;
        mDispatcher.getBackStack().remove(this);

        notifyHideListener(cancel);

        if (detach) {
            detachViewFromParent();
        }
    }

    protected void attachViewToParent() {
        if (mParentView == null) {
            Timber.e("parent view is null");
            return;
        }

        if (mDecorView.getParent() != null) {
            Timber.e("decor view's parent is not null %s", mDecorView.getParent());
            return;
        }

        mParentView.addView(mDecorView);
    }

    protected void detachViewFromParent() {
        ViewParent parent = mDecorView.getParent();
        if (!(parent instanceof ViewGroup)) {
            Timber.e("decor view's parent is not instance of ViewGroup %s", parent);
            return;
        }

        if (parent != mParentView) {
            Timber.e("decor view's parent changed to another, require:%s, found:%s", mParentView, parent);
        }

        ((ViewGroup) parent).removeView(mDecorView);
    }

    public interface OnHideListener {
        void onHide(boolean cancel);
    }

    private OnHideListener mOnHideListener;

    public void setOnHideListener(OnHideListener onHideListener) {
        mOnHideListener = onHideListener;
    }

    protected void notifyHideListener(boolean cancel) {
        if (mOnHideListener != null) {
            mOnHideListener.onHide(cancel);
        }
    }

    public interface OnShowListener {
        void onShow();
    }

    private OnShowListener mOnShowListener;

    public void setOnShowListener(OnShowListener onShowListener) {
        mOnShowListener = onShowListener;
    }

    protected void notifyShowListener() {
        if (mOnShowListener != null) {
            mOnShowListener.onShow();
        }
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    private OnBackPressedListener mOnBackPressedListener;

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        mOnBackPressedListener = onBackPressedListener;
    }

}

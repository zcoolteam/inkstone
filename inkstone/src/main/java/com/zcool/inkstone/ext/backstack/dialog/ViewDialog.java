package com.zcool.inkstone.ext.backstack.dialog;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.support.annotation.AnimatorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.zcool.inkstone.R;
import com.zcool.inkstone.ext.backstack.ViewBackLayer;
import com.zcool.inkstone.ext.backstack.WindowBackStackDispatcher;
import com.zcool.inkstone.util.ViewUtil;

import timber.log.Timber;

public class ViewDialog extends ViewBackLayer {

    private final Activity mActivity;
    private final ViewGroup mContentParentView;

    private View mContentView;
    private Animator mContentViewShowAnimator;
    private Animator mContentViewHideAnimator;

    private ViewDialog(Activity activity, View decorView, ViewGroup parentView, ViewGroup contentParentView) {
        super(WindowBackStackDispatcher.from(activity.getWindow()), decorView, parentView);
        mActivity = activity;
        mContentParentView = contentParentView;

        ViewUtil.onClick(decorView, v -> {
            Timber.v("decor view onClick");
            onBackPressed();
        });
    }

    public Activity getActivity() {
        return mActivity;
    }

    public ViewGroup getContentParentView() {
        return mContentParentView;
    }

    private void setContentView(@Nullable View contentView) {
        mContentParentView.removeAllViews();
        if (contentView != null) {
            mContentParentView.addView(contentView);
        }
        mContentView = contentView;
    }

    @Nullable
    public View getContentView() {
        return mContentView;
    }

    private void setContentViewAnimator(Animator showAnimator, Animator hideAnimator) {
        mContentViewShowAnimator = showAnimator;
        mContentViewHideAnimator = hideAnimator;

        if (mContentViewHideAnimator != null) {
            mContentViewHideAnimator.addListener(new Animator.AnimatorListener() {

                private boolean mCanceled;

                @Override
                public void onAnimationStart(Animator animation) {
                    mCanceled = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mCanceled) {
                        Timber.v("ViewDialog content view hide animator end with cancel");
                        return;
                    }

                    if (isShown()) {
                        Timber.e("ViewDialog is shown after content view hide animator end");
                        return;
                    }

                    if (mContentView == null) {
                        Timber.e("ViewDialog content view is null after content view hide animator end");
                        return;
                    }

                    detachViewFromParent();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCanceled = true;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }

    @Override
    public void show() {
        if (isShown()) {
            Timber.e("already shown");
            return;
        }

        clearContentViewAnimator();
        if (mContentView == null || mContentViewShowAnimator == null) {
            super.show();
            return;
        }

        super.show();
        mContentViewShowAnimator.setTarget(mContentView);
        mContentViewShowAnimator.start();
    }

    @Override
    public void hide(boolean cancel) {
        if (!isShown()) {
            Timber.e("not shown");
            return;
        }

        clearContentViewAnimator();
        if (mContentView == null || mContentViewHideAnimator == null) {
            super.hide(cancel);
            return;
        }

        super.hideInternal(cancel, false);
        mContentViewHideAnimator.setTarget(mContentView);
        mContentViewHideAnimator.start();
    }

    private void clearContentViewAnimator() {
        if (mContentViewShowAnimator != null) {
            mContentViewShowAnimator.cancel();
        }
        if (mContentViewHideAnimator != null) {
            mContentViewHideAnimator.cancel();
        }
    }

    public static class Builder {

        private Activity mActivity;
        private int mDecorViewLayoutRes = R.layout.inkstone_ext_view_dialog_dim_background;
        private ViewGroup mParentView;

        private boolean mCancelable = true;
        private OnHideListener mOnHideListener;
        private OnShowListener mOnShowListener;

        private OnBackPressedListener mOnBackPressedListener;

        private int mContentViewLayoutRes;
        private int mContentViewShowAnimatorRes;
        private int mContentViewHideAnimatorRes;

        public Builder(Activity activity) {
            mActivity = activity;
        }

        public Builder setDecorView(@LayoutRes int layoutRes) {
            mDecorViewLayoutRes = layoutRes;
            return this;
        }

        public Builder dimBackground(boolean dim) {
            setDecorView(dim ? R.layout.inkstone_ext_view_dialog_dim_background : R.layout.inkstone_ext_view_dialog);
            return this;
        }

        public Builder setParentView(ViewGroup parentView) {
            mParentView = parentView;
            return this;
        }

        public Builder setContentView(@LayoutRes int layoutRes) {
            mContentViewLayoutRes = layoutRes;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setOnHideListener(OnHideListener onHideListener) {
            mOnHideListener = onHideListener;
            return this;
        }

        public Builder setOnShowListener(OnShowListener onShowListener) {
            mOnShowListener = onShowListener;
            return this;
        }

        public Builder setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
            mOnBackPressedListener = onBackPressedListener;
            return this;
        }

        public Builder setContentViewShowAnimator(@AnimatorRes int animatorRes) {
            mContentViewShowAnimatorRes = animatorRes;
            return this;
        }

        public Builder setContentViewHideAnimator(@AnimatorRes int animatorRes) {
            mContentViewHideAnimatorRes = animatorRes;
            return this;
        }

        public Builder defaultAnimator() {
            setContentViewShowAnimator(R.animator.inkstone_dialog_default_in);
            setContentViewHideAnimator(R.animator.inkstone_dialog_default_out);
            return this;
        }

        public ViewDialog create() {
            Preconditions.checkArgument(mActivity != null, "Activity is null");
            Preconditions.checkArgument(mDecorViewLayoutRes > 0, "invalid decor view layout res %s", mDecorViewLayoutRes);
            Preconditions.checkArgument(mParentView != null, "parent view not set or null");

            View decorView = mActivity.getLayoutInflater().inflate(mDecorViewLayoutRes, mParentView, false);
            ViewGroup contentParentView = decorView.findViewById(R.id.content_parent);

            Preconditions.checkArgument(contentParentView != null, "content parent view not found,\ndecor view layout res must define one ViewGroup with id R.id.content_parent");

            View contentView = null;
            if (mContentViewLayoutRes > 0) {
                contentView = mActivity.getLayoutInflater().inflate(mContentViewLayoutRes, contentParentView, false);
            }

            ViewDialog viewDialog = new ViewDialog(mActivity, decorView, mParentView, contentParentView);
            viewDialog.setContentView(contentView);
            viewDialog.setCancelable(mCancelable);
            viewDialog.setOnHideListener(mOnHideListener);
            viewDialog.setOnShowListener(mOnShowListener);
            viewDialog.setOnBackPressedListener(mOnBackPressedListener);
            viewDialog.setContentViewAnimator(
                    mContentViewShowAnimatorRes > 0 ? AnimatorInflater.loadAnimator(mActivity, mContentViewShowAnimatorRes) : null,
                    mContentViewHideAnimatorRes > 0 ? AnimatorInflater.loadAnimator(mActivity, mContentViewHideAnimatorRes) : null);
            return viewDialog;
        }

    }

}

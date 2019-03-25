package com.zcool.inkstone.ext.widget.pulllayout;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.zcool.inkstone.R;
import com.zcool.inkstone.util.DimenUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.Nonnull;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import timber.log.Timber;

public class PullLayout extends FrameLayout implements NestedScrollingParent2, NestedScrollingChild2 {

    public PullLayout(Context context) {
        this(context, null);
    }

    public PullLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    public static final int PULL_POSITION_LEFT = 0;
    public static final int PULL_POSITION_TOP = 1;
    public static final int PULL_POSITION_RIGHT = 2;
    public static final int PULL_POSITION_BOTTOM = 3;

    @IntDef({PULL_POSITION_LEFT, PULL_POSITION_TOP, PULL_POSITION_RIGHT, PULL_POSITION_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PullPosition {
    }

    private int mTouchSlop;
    @PullPosition
    private int mPullPosition = PULL_POSITION_TOP;
    private boolean mPullOverlay = false;
    // 触发刷新的距离阀值
    private int mPullThreshold = DimenUtil.dp2px(50);
    private View mHeader; // 下拉头
    private View mTarget; // 主要内容

    private int mActivePointerId = -1; // 用于计算滑动的手指
    private int mLastMotionX;
    private int mLastMotionY;

    private boolean mIsBeingDragged;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    private OffsetHelper mOffsetHelper;

    private void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullLayout, defStyleAttr,
                defStyleRes);
        mPullPosition = a.getInt(R.styleable.PullLayout_pull_position, mPullPosition);
        mPullOverlay = a.getBoolean(R.styleable.PullLayout_pull_overlay, mPullOverlay);
        mPullThreshold = a.getDimensionPixelOffset(R.styleable.PullLayout_pull_threshold, mPullThreshold);
        if (mPullThreshold <= 0) {
            throw new IllegalArgumentException("pull threshold must > 0");
        }
        a.recycle();

        mOffsetHelper = new OffsetHelper((offsetHelper, animating) -> {
            ensureTargetAndHeader();

            if (mTarget == null || mHeader == null) {
                Timber.e("target or header not found");
                return;
            }

            int windowOffsetX = -offsetHelper.getThresholdTransform(offsetHelper.getCurrentOffsetX());
            int windowOffsetY = -offsetHelper.getThresholdTransform(offsetHelper.getCurrentOffsetY());

            mTarget.setTranslationX(windowOffsetX);
            mTarget.setTranslationY(windowOffsetY);

            Header header = (Header) mHeader;
            header.updateOffset(offsetHelper, animating, windowOffsetX, windowOffsetY, PullLayout.this);
        });
    }

    @PullPosition
    public int getPullPosition() {
        return mPullPosition;
    }

    public boolean isPullOverlay() {
        return mPullOverlay;
    }

    public int getPullThreshold() {
        return mPullThreshold;
    }

    @Nullable
    public View getTarget() {
        ensureTargetAndHeader();
        return mTarget;
    }

    @Nullable
    public View getHeader() {
        ensureTargetAndHeader();
        return mHeader;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }

        if (!isEnabled()
                || canChildScroll()
                || getNestedScrollAxes() != 0) {
            return false;
        }

        final int action = event.getActionMasked();
        int pointerIndex;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                mIsBeingDragged = false;
                mLastMotionX = (int) event.getX(0);
                mLastMotionY = (int) event.getY(0);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId < 0) {
                    Timber.e("onInterceptTouchEvent ACTION_MOVE but no active pointer id.");
                    return false;
                }

                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Timber.e("onInterceptTouchEvent ACTION_MOVE but active pointer id invalid.");
                    return false;
                }

                int x = (int) event.getX(pointerIndex);
                int y = (int) event.getY(pointerIndex);
                startDragging(x, y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = -1;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }

        if (!isEnabled()
                || canChildScroll()
                || getNestedScrollAxes() != 0) {
            return false;
        }

        final int action = event.getActionMasked();
        int pointerIndex;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                mIsBeingDragged = false;
                mLastMotionX = (int) event.getX(0);
                mLastMotionY = (int) event.getY(0);
                break;

            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId < 0) {
                    Timber.e("onTouchEvent ACTION_MOVE but no active pointer id.");
                    return false;
                }

                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Timber.e("onTouchEvent ACTION_MOVE but active pointer id invalid.");
                    return false;
                }

                int x = (int) event.getX(pointerIndex);
                int y = (int) event.getY(pointerIndex);

                if (!mIsBeingDragged) {
                    startDragging(x, y);
                } else {
                    final int dx = mLastMotionX - x;
                    final int dy = mLastMotionY - y;
                    applyPullOffset(dx, dy);
                    mLastMotionX = x;
                    mLastMotionY = y;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;

            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    finishOffset(false);
                    mIsBeingDragged = false;
                }
                mActivePointerId = -1;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    finishOffset(true);
                    mIsBeingDragged = false;
                }
                mActivePointerId = -1;
                break;
        }

        return true;
    }

    protected interface OnOffsetChangedListener {
        void onOffsetChanged(@Nonnull OffsetHelper offsetHelper, boolean animating);
    }

    public class OffsetHelper {

        @NonNull
        private final OnOffsetChangedListener mOffsetChangedListener;

        int mCurrentOffsetX;
        int mCurrentOffsetY;

        int mTargetOffsetX;
        int mTargetOffsetY;

        private Animator mAnimator;

        private OffsetHelper(OnOffsetChangedListener offsetChangedListener) {
            mOffsetChangedListener = offsetChangedListener;
        }

        public int getCurrentOffsetX() {
            return mCurrentOffsetX;
        }

        public int getCurrentOffsetY() {
            return mCurrentOffsetY;
        }

        public int getTargetOffsetX() {
            return mTargetOffsetX;
        }

        public int getTargetOffsetY() {
            return mTargetOffsetY;
        }

        public int getThresholdTransform(int offset) {
            if (isPullOverlay()) {
                return 0;
            }
            if (offset == 0) {
                return 0;
            }

            if (offset < 0) {
                if (offset >= -mPullThreshold) {
                    return offset;
                } else {
                    return -mPullThreshold + (int) ((offset + mPullThreshold) * 0.5f);
                }
            } else {
                if (offset <= mPullThreshold) {
                    return offset;
                } else {
                    return mPullThreshold + (int) ((offset - mPullThreshold) * 0.5f);
                }
            }
        }

        public void clear() {
            Animator animator = mAnimator;
            mAnimator = null;
            if (animator != null) {
                animator.cancel();
            }
        }

        private boolean canTriggerRefresh() {
            switch (mPullPosition) {
                case PULL_POSITION_TOP: {
                    return mCurrentOffsetY <= -mPullThreshold;
                }
                case PULL_POSITION_BOTTOM: {
                    return mCurrentOffsetY >= mPullThreshold;
                }
                case PULL_POSITION_LEFT: {
                    return mCurrentOffsetX <= -mPullThreshold;
                }
                case PULL_POSITION_RIGHT: {
                    return mCurrentOffsetX >= mPullThreshold;
                }
                default:
                    throw new IllegalArgumentException("unknown pull position: " + mPullPosition);
            }
        }

        private boolean appendOffset(int dx, int dy) {
            return appendOffset(dx, dy, false);
        }

        private boolean appendOffset(int dx, int dy, boolean animate) {
            return setOffset(mCurrentOffsetX + dx, mCurrentOffsetY + dy, animate);
        }

        private boolean setOffset(int targetOffsetX, int targetOffsetY, boolean animate) {
            clear();

            switch (mPullPosition) {
                case PULL_POSITION_TOP: {
                    targetOffsetX = 0;
                    if (targetOffsetY >= 0) {
                        targetOffsetY = 0;
                        if (mCurrentOffsetY >= 0) {
                            // header already hide
                            return false;
                        }
                    }
                    break;
                }
                case PULL_POSITION_BOTTOM: {
                    targetOffsetX = 0;
                    if (targetOffsetY <= 0) {
                        targetOffsetY = 0;
                        if (mCurrentOffsetY <= 0) {
                            // header already hide
                            return false;
                        }
                    }
                    break;
                }
                case PULL_POSITION_LEFT: {
                    targetOffsetY = 0;
                    if (targetOffsetX >= 0) {
                        targetOffsetX = 0;
                        if (mCurrentOffsetX >= 0) {
                            // header already hide
                            return false;
                        }
                    }
                    break;
                }
                case PULL_POSITION_RIGHT: {
                    targetOffsetY = 0;
                    if (targetOffsetX <= 0) {
                        targetOffsetX = 0;
                        if (mCurrentOffsetX <= 0) {
                            // header already hide
                            return false;
                        }
                    }
                    break;
                }
                default:
                    return false;
            }

            mTargetOffsetX = targetOffsetX;
            mTargetOffsetY = targetOffsetY;

            if (!animate) {
                mCurrentOffsetX = targetOffsetX;
                mCurrentOffsetY = targetOffsetY;
                mOffsetChangedListener.onOffsetChanged(this, false);

                return true;
            }

            mOffsetChangedListener.onOffsetChanged(this, true);

            int duration = 200;
            int absDistance = Math.max(Math.abs(mCurrentOffsetX - targetOffsetX), Math.abs(mCurrentOffsetY - targetOffsetY));
            if (absDistance < mPullThreshold) {
                duration = (int) (absDistance * 1f / mPullThreshold * 200);
            }

            ValueAnimator animator = new ValueAnimator();
            mAnimator = animator;

            animator.setValues(
                    PropertyValuesHolder.ofInt("offsetX", mCurrentOffsetX, targetOffsetX),
                    PropertyValuesHolder.ofInt("offsetY", mCurrentOffsetY, targetOffsetY));
            animator.setDuration(duration);
            animator.addUpdateListener(animation -> {
                if (mAnimator != animator) {
                    return;
                }
                mCurrentOffsetX = (int) animation.getAnimatedValue("offsetX");
                mCurrentOffsetY = (int) animation.getAnimatedValue("offsetY");
                mOffsetChangedListener.onOffsetChanged(this, true);
            });
            animator.start();
            return true;
        }

    }

    public boolean applyPullOffset(int dx, int dy) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }

        return mOffsetHelper.appendOffset(dx, dy);
    }

    private int[] getFinalOffset(boolean refreshing) {
        switch (mPullPosition) {
            case PULL_POSITION_TOP: {
                if (refreshing) {
                    return new int[]{0, -mPullThreshold};
                } else {
                    return new int[]{0, 0};
                }
            }
            case PULL_POSITION_BOTTOM: {
                if (refreshing) {
                    return new int[]{0, mPullThreshold};
                } else {
                    return new int[]{0, 0};
                }
            }
            case PULL_POSITION_LEFT: {
                if (refreshing) {
                    return new int[]{-mPullThreshold, 0};
                } else {
                    return new int[]{0, 0};
                }
            }
            case PULL_POSITION_RIGHT: {
                if (refreshing) {
                    return new int[]{mPullThreshold, 0};
                } else {
                    return new int[]{0, 0};
                }
            }
            default:
                throw new IllegalArgumentException("unknown pull position: " + mPullPosition);
        }
    }

    /**
     * @param cancel 如果是 cancel, 则忽略计算是否触发刷新，直接滚动到初始状态
     */
    private void finishOffset(boolean cancel) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return;
        }

        boolean refreshing = mRefreshing;
        if (!refreshing) {
            if (!cancel) {
                refreshing = mOffsetHelper.canTriggerRefresh();
            }
        }

        boolean notifyRefreshing = !mRefreshing && refreshing;
        setRefreshing(refreshing);

        if (notifyRefreshing && mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh(this);
        }
    }

    private boolean isPullVertical() {
        return mPullPosition == PULL_POSITION_TOP || mPullPosition == PULL_POSITION_BOTTOM;
    }

    private void startDragging(int x, int y) {
        if (mIsBeingDragged) {
            return;
        }

        final int dx = mLastMotionX - x;
        final int dy = mLastMotionY - y;
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        switch (mPullPosition) {
            case PULL_POSITION_TOP:
                if ((dy < -mTouchSlop || (dy > mTouchSlop && mOffsetHelper.getCurrentOffsetY() < 0))
                        && absDy > absDx) {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mIsBeingDragged = true;
                }
                break;
            case PULL_POSITION_BOTTOM:
                if ((dy > mTouchSlop || (dy < -mTouchSlop && mOffsetHelper.getCurrentOffsetY() > 0))
                        && absDy > absDx) {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mIsBeingDragged = true;
                }
                break;
            case PULL_POSITION_LEFT:
                if ((dx < -mTouchSlop || (dx > mTouchSlop && mOffsetHelper.getCurrentOffsetX() < 0))
                        && absDx > absDy) {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mIsBeingDragged = true;
                }
                break;
            case PULL_POSITION_RIGHT:
                if ((dx > mTouchSlop || (dx < -mTouchSlop && mOffsetHelper.getCurrentOffsetX() > 0))
                        && absDx > absDy) {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mIsBeingDragged = true;
                }
                break;
        }

        if (mIsBeingDragged) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    private void onSecondaryPointerUp(MotionEvent event) {
        final int pointerIndex = event.getActionIndex();
        final int pointerId = event.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // 抬起的手指正是当前用于计算滑动的手指
            // 重新设置计算滑动的手指和对应的滑动坐标
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = event.getPointerId(newPointerIndex);
            mLastMotionX = (int) event.getX(newPointerIndex);
            mLastMotionY = (int) event.getY(newPointerIndex);
        }
    }

    private boolean canChildScroll() {
        switch (mPullPosition) {
            case PULL_POSITION_LEFT:
                return canChildScrollLeft();
            case PULL_POSITION_TOP:
                return canChildScrollUp();
            case PULL_POSITION_RIGHT:
                return canChildScrollRight();
            case PULL_POSITION_BOTTOM:
                return canChildScrollDown();
        }
        throw new RuntimeException();
    }

    private boolean canChildScrollUp() {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }

        return mTarget.canScrollVertically(-1);
    }

    private boolean canChildScrollDown() {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }

        return mTarget.canScrollVertically(1);
    }

    private boolean canChildScrollLeft() {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }

        return mTarget.canScrollHorizontally(-1);
    }

    private boolean canChildScrollRight() {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }

        return mTarget.canScrollHorizontally(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /*
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return;
        }

        measureChild(mTarget, widthMeasureSpec, heightMeasureSpec);
        measureChild(mHeader, widthMeasureSpec, heightMeasureSpec);
        */
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return;
        }

        switch (mPullPosition) {
            case PULL_POSITION_LEFT:
                mHeader.layout(mHeader.getLeft() - mHeader.getRight(),
                        mHeader.getTop(),
                        mHeader.getRight() - mHeader.getRight(),
                        mHeader.getBottom());
                break;
            case PULL_POSITION_TOP:
                mHeader.layout(mHeader.getLeft(),
                        mHeader.getTop() - mHeader.getBottom(),
                        mHeader.getRight(),
                        mHeader.getBottom() - mHeader.getBottom());
                break;
            case PULL_POSITION_RIGHT:
                mHeader.layout(mHeader.getLeft() - mHeader.getLeft() + getWidth(),
                        mHeader.getTop(),
                        mHeader.getRight() - mHeader.getLeft() + getWidth(),
                        mHeader.getBottom());
                break;
            case PULL_POSITION_BOTTOM:
                mHeader.layout(mHeader.getLeft(),
                        mHeader.getTop() - mHeader.getTop() + getHeight(),
                        mHeader.getRight(),
                        mHeader.getBottom() - mHeader.getTop() + getHeight());
                break;
        }
    }

    private void ensureTargetAndHeader() {
        // 此时可能还没有 layout
        if (mHeader == null || mTarget == null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child instanceof Header) {
                    if (mHeader == null) {
                        mHeader = child;
                    }
                } else {
                    if (mTarget == null) {
                        mTarget = child;
                    }
                }
            }
        }
    }

    private boolean mRefreshing;

    public boolean isRefreshing() {
        return mRefreshing;
    }

    public void setRefreshing(boolean refreshing) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return;
        }

        mRefreshing = refreshing;

        int[] targetOffset = getFinalOffset(mRefreshing);
        mOffsetHelper.setOffset(targetOffset[0], targetOffset[1], true);
    }

    // nested scroll parent

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        return onStartNestedScroll(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }

        if (type != ViewCompat.TYPE_TOUCH) {
            return false;
        }

        if (!isEnabled()) {
            return false;
        }

        if (isPullVertical()) {
            return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        } else {
            return (axes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0;
        }
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        if (isPullVertical()) {
            axes &= ViewCompat.SCROLL_AXIS_VERTICAL;
        } else {
            axes &= ViewCompat.SCROLL_AXIS_HORIZONTAL;
        }

        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(axes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type);

        finishOffset(false);

        stopNestedScroll(type);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        ensureTargetAndHeader();

        // 记录父滑动后, 当前控件实际产生的窗口偏移 (滑动后的位置减去滑动前的位置, 因此如果是向下滑动, 偏移值为正)
        // dx, dy, 向上,向左是正, 向下,向右是负 (上一个位置减去当前位置)
        int[] parentOffsetInWindow = new int[2]; // 数组默认初始化为 0

        if (mTarget == null || mHeader == null) {
            dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, parentOffsetInWindow, type);
            return;
        }

        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, parentOffsetInWindow, type);

        // parentOffsetInWindow 与 dx dy 方向是反的
        final int dx = dxUnconsumed + parentOffsetInWindow[0]; // 减去父容器消耗的 dx
        final int dy = dyUnconsumed + parentOffsetInWindow[1]; // 减去父容器消耗的 dy

        if ((mPullPosition == PULL_POSITION_TOP && dy < 0 && !canChildScrollUp())
                || (mPullPosition == PULL_POSITION_BOTTOM && dy > 0 && !canChildScrollDown())
                || (mPullPosition == PULL_POSITION_LEFT && dx < 0 && !canChildScrollLeft())
                || (mPullPosition == PULL_POSITION_RIGHT && dx > 0 && !canChildScrollRight())) {
            applyPullOffset(dx, dy);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            dispatchNestedPreScroll(dx, dy, consumed, null, type);
            return;
        }

        // dx, dy, 向上,向左是正, 向下,向右是负 (上一个位置减去当前位置)
        if ((mPullPosition == PULL_POSITION_TOP && dy > 0)
                || (mPullPosition == PULL_POSITION_BOTTOM && dy < 0)
                || (mPullPosition == PULL_POSITION_LEFT && dx > 0)
                || (mPullPosition == PULL_POSITION_RIGHT && dx < 0)) {

            if (applyPullOffset(dx, dy)) {
                consumed[0] = dx;
                consumed[1] = dy;
            } else {
                dispatchNestedPreScroll(dx, dy, consumed, null, type);
            }
        } else {
            dispatchNestedPreScroll(dx, dy, consumed, null, type);
        }
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    // nested scroll child

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mNestedScrollingChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void stopNestedScroll(int type) {
        mNestedScrollingChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mNestedScrollingChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    public interface OnRefreshListener {
        void onRefresh(PullLayout pullLayout);
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public interface Header {

        void updateOffset(@NonNull OffsetHelper offsetHelper, boolean animating, int windowOffsetX, int windowOffsetY, @NonNull PullLayout pullLayout);

    }

}

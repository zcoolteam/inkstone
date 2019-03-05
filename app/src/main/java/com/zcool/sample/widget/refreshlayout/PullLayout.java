package com.zcool.sample.widget.refreshlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.zcool.sample.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import timber.log.Timber;

public class PullLayout extends ViewGroup implements NestedScrollingParent2, NestedScrollingChild2 {

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

    private static final int PULL_POSITION_LEFT = 0;
    private static final int PULL_POSITION_TOP = 1;
    private static final int PULL_POSITION_RIGHT = 2;
    private static final int PULL_POSITION_BOTTOM = 3;

    private int mTouchSlop;
    private int mPullPosition = PULL_POSITION_TOP;
    private View mHeader; // 下拉头
    private View mTarget; // 主要内容

    private int mActivePointerId = -1; // 用于计算滑动的手指
    private int mLastMotionX;
    private int mLastMotionY;

    private boolean mIsBeingDragged;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    private void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullLayout, defStyleAttr,
                defStyleRes);
        mPullPosition = a.getInt(R.styleable.PullLayout_pull_position, PULL_POSITION_TOP);
        a.recycle();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }

        if (!isEnabled()
                // || isHeaderStatusBusy()
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
                // || isHeaderStatusBusy()
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

    /*
    private boolean isHeaderStatusBusy() {
        if (mHeader == null) {
            return true;
        }
        Header header = (Header) mHeader;
        return header.isStatusBusy();
    }
    */

    public int applyPullOffset(int dx, int dy) {
        if (isPullVertical()) {
            // vertical
            return applyPullOffset(dy);
        } else {
            // horizontal
            return applyPullOffset(dx);
        }
    }

    public int applyPullOffset(int offset) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return 0;
        }

        Header header = (Header) mHeader;
        return header.applyOffset(offset, mTarget, this);
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

        Header header = (Header) mHeader;
        header.finishOffset(cancel, mTarget, this);
    }

    private boolean isPullVertical() {
        return mPullPosition == PULL_POSITION_TOP || mPullPosition == PULL_POSITION_BOTTOM;
    }

    private void startDragging(int x, int y) {
        final int dx = mLastMotionX - x;
        final int dy = mLastMotionY - y;
        if (mIsBeingDragged) {
            return;
        }

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        if (isPullVertical()) {
            // vertical
            if (absDy > mTouchSlop && absDy > absDx) {
                mLastMotionX = x;
                mLastMotionY = y;
                mIsBeingDragged = true;
            }
        } else {
            // horizontal
            if (absDx > mTouchSlop && absDx > absDy) {
                mLastMotionX = x;
                mLastMotionY = y;
                mIsBeingDragged = true;
            }
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
            default:
                throw new IllegalArgumentException("unknown pull position: " + mPullPosition);
        }
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

        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return;
        }

        measureChild(mTarget, widthMeasureSpec, heightMeasureSpec);
        measureChild(mHeader, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return;
        }

        int targetWidth = mTarget.getMeasuredWidth();
        int targetHeight = mTarget.getMeasuredHeight();
        mTarget.layout(getPaddingLeft(),
                getPaddingTop(),
                getPaddingLeft() + targetWidth,
                getPaddingTop() + targetHeight);

        int headerWidth = mHeader.getMeasuredWidth();
        int headerHeight = mHeader.getMeasuredHeight();
        switch (mPullPosition) {
            case PULL_POSITION_LEFT:
                mHeader.layout(getPaddingLeft() - headerWidth,
                        getPaddingTop(),
                        getPaddingLeft(),
                        getPaddingTop() + headerHeight);
                break;
            case PULL_POSITION_TOP:
                mHeader.layout(getPaddingLeft(),
                        getPaddingTop() - headerHeight,
                        getPaddingLeft() + headerWidth,
                        getPaddingTop());
                break;
            case PULL_POSITION_RIGHT:
                mHeader.layout(getPaddingRight(), getPaddingTop(), getPaddingRight() + headerWidth, getPaddingTop() + headerHeight);
                break;
            case PULL_POSITION_BOTTOM:
                mHeader.layout(getPaddingLeft(), getPaddingBottom(), getPaddingLeft() + headerWidth, getPaddingBottom() + headerHeight);
                break;
            default:
                throw new IllegalArgumentException("unknown pull position: " + mPullPosition);
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

        if (mHeader != null) {
            Header header = (Header) mHeader;
            header.setOnRefreshListener(() -> {
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            });
        }
    }

    public void setRefreshing(boolean refreshing) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return;
        }

        Header header = (Header) mHeader;
        header.setRefreshing(refreshing, false, mTarget, this);
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
            return false;
        }

        return type == ViewCompat.TYPE_TOUCH && isEnabled() && (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL, type);
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
        int[] parentOffsetInWindow = new int[2];

        if (mTarget == null || mHeader == null) {
            dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, parentOffsetInWindow, type);
            return;
        }

        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, parentOffsetInWindow, type);

        final int dy = dyUnconsumed + parentOffsetInWindow[1];

        if (dy < 0 && !canChildScrollUp()) {
            applyPullOffset(-dy);
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

        // 向上滑动 dy > 0

        if (dy > 0) {
            float usedDy = applyPullOffset(-dy);
            usedDy = -usedDy;
            dy -= usedDy;

            final int[] parentConsumed = new int[2];
            dispatchNestedPreScroll(dx, dy, parentConsumed, null, type);

            consumed[0] = 0;
            consumed[1] = (int) usedDy;
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
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
        void onRefresh();
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public interface Header {

        void setOnRefreshListener(OnRefreshListener onRefreshListener);

        void setRefreshing(boolean refreshing, boolean notifyRefresh, View target, PullLayout pullLayout);

        /**
         * 处理拉动距离变更值, 返回实际消耗的变更值
         */
        int applyOffset(int offset, View target, PullLayout pullLayout);

        void finishOffset(boolean cancel, View target, PullLayout pullLayout);

    }

}

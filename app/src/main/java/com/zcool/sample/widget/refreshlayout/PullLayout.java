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

import com.zcool.inkstone.util.DimenUtil;
import com.zcool.sample.R;

import androidx.annotation.IntDef;
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

    public static final int PULL_POSITION_LEFT = 0;
    public static final int PULL_POSITION_TOP = 1;
    public static final int PULL_POSITION_RIGHT = 2;
    public static final int PULL_POSITION_BOTTOM = 3;

    @IntDef({PULL_POSITION_LEFT, PULL_POSITION_TOP, PULL_POSITION_RIGHT, PULL_POSITION_BOTTOM})
    public @interface PullPosition {
    }

    public static final int ANIMATION_DURATION = 200;
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

    private int[] mTotalConsumedOffset = new int[2];

    public boolean applyPullOffset(int dx, int dy) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return false;
        }


        int[] tmpTotalConsumedOffset = new int[]{mTotalConsumedOffset[0] + dx, mTotalConsumedOffset[1] + dy};
        switch (mPullPosition) {
            case PULL_POSITION_TOP: {
                tmpTotalConsumedOffset[0] = 0;
                if (tmpTotalConsumedOffset[1] >= 0) {
                    tmpTotalConsumedOffset[1] = 0;
                    if (mTotalConsumedOffset[1] >= 0) {
                        // header already hide
                        return false;
                    }
                }
                break;
            }
            case PULL_POSITION_BOTTOM: {
                tmpTotalConsumedOffset[0] = 0;
                if (tmpTotalConsumedOffset[1] <= 0) {
                    tmpTotalConsumedOffset[1] = 0;
                    if (mTotalConsumedOffset[1] <= 0) {
                        // header already hide
                        return false;
                    }
                }
                break;
            }
            case PULL_POSITION_LEFT: {
                tmpTotalConsumedOffset[1] = 0;
                if (tmpTotalConsumedOffset[0] >= 0) {
                    tmpTotalConsumedOffset[0] = 0;
                    if (mTotalConsumedOffset[0] >= 0) {
                        // header already hide
                        return false;
                    }
                }
                break;
            }
            case PULL_POSITION_RIGHT: {
                tmpTotalConsumedOffset[1] = 0;
                if (tmpTotalConsumedOffset[0] <= 0) {
                    tmpTotalConsumedOffset[0] = 0;
                    if (mTotalConsumedOffset[0] <= 0) {
                        // header already hide
                        return false;
                    }
                }
                break;
            }
            default:
                return false;
        }

        mTotalConsumedOffset[0] = tmpTotalConsumedOffset[0];
        mTotalConsumedOffset[1] = tmpTotalConsumedOffset[1];

        int[] windowOffset = calculateBestWindowOffset(mTotalConsumedOffset[0], mTotalConsumedOffset[1], false, false);

        Header header = (Header) mHeader;
        header.updateOffset(mRefreshing, mTotalConsumedOffset[0], mTotalConsumedOffset[1], mPullThreshold, windowOffset[0], windowOffset[1], this);

        if (!mPullOverlay) {
            mTarget.animate().cancel();
            mTarget.setTranslationX(windowOffset[0]);
            mTarget.setTranslationY(windowOffset[1]);
        }

        return true;
    }

    // TODO
    private int[] calculateBestWindowOffset(int totalConsumedOffsetX, int totalConsumedOffsetY, boolean refreshing, boolean cancel) {
        switch (mPullPosition) {
            case PULL_POSITION_TOP: {
                if (refreshing) {
                    return new int[]{0, mPullThreshold};
                } else if (cancel) {
                    return new int[]{0, 0};
                } else {
                    return new int[]{0, Math.max(0, -totalConsumedOffsetY)};
                }
            }
            case PULL_POSITION_BOTTOM: {
                if (refreshing) {
                    return new int[]{0, -mPullThreshold};
                } else if (cancel) {
                    return new int[]{0, 0};
                } else {
                    return new int[]{0, Math.min(0, -totalConsumedOffsetY)};
                }
            }
            case PULL_POSITION_LEFT: {
                if (refreshing) {
                    return new int[]{mPullThreshold, 0};
                } else if (cancel) {
                    return new int[]{0, 0};
                } else {
                    return new int[]{Math.max(0, -totalConsumedOffsetX), 0};
                }
            }
            case PULL_POSITION_RIGHT: {
                if (refreshing) {
                    return new int[]{-mPullThreshold, 0};
                } else if (cancel) {
                    return new int[]{0, 0};
                } else {
                    return new int[]{Math.min(0, -totalConsumedOffsetX), 0};
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
        int[] totalConsumedOffset = {mTotalConsumedOffset[0], mTotalConsumedOffset[1]};

        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return;
        }

        boolean refreshing = mRefreshing;
        if (!refreshing) {
            if (!cancel) {
                if ((mPullPosition == PULL_POSITION_TOP && totalConsumedOffset[1] <= -mPullThreshold)
                        || (mPullPosition == PULL_POSITION_BOTTOM && totalConsumedOffset[1] >= mPullThreshold)
                        || (mPullPosition == PULL_POSITION_LEFT && totalConsumedOffset[0] <= -mPullThreshold)
                        || (mPullPosition == PULL_POSITION_RIGHT && totalConsumedOffset[0] >= mPullThreshold)) {
                    refreshing = true;
                }
            }
        }

        boolean notifyRefreshing = !mRefreshing && refreshing;
        setRefreshing(refreshing);

        if (notifyRefreshing && mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh(this);
        }

        mTotalConsumedOffset[0] = mTotalConsumedOffset[1] = 0;
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
                if (dy < -mTouchSlop && absDy > absDx) {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mIsBeingDragged = true;
                }
                break;
            case PULL_POSITION_BOTTOM:
                if (dy > mTouchSlop && absDy > absDx) {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mIsBeingDragged = true;
                }
                break;
            case PULL_POSITION_LEFT:
                if (dx < -mTouchSlop && absDx > absDy) {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mIsBeingDragged = true;
                }
                break;
            case PULL_POSITION_RIGHT:
                if (dx > mTouchSlop && absDx > absDy) {
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
                mHeader.layout(getPaddingRight(),
                        getPaddingTop(),
                        getPaddingRight() + headerWidth,
                        getPaddingTop() + headerHeight);
                break;
            case PULL_POSITION_BOTTOM:
                mHeader.layout(getPaddingLeft(),
                        getPaddingBottom(),
                        getPaddingLeft() + headerWidth,
                        getPaddingBottom() + headerHeight);
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

    public void setRefreshing(boolean refreshing) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Timber.e("target or header not found");
            return;
        }

        mRefreshing = refreshing;

        int[] windowOffset = calculateBestWindowOffset(mTotalConsumedOffset[0], mTotalConsumedOffset[1], mRefreshing, true);

        Header header = (Header) mHeader;
        header.updateOffset(mRefreshing, mTotalConsumedOffset[0], mTotalConsumedOffset[1], mPullThreshold, windowOffset[0], windowOffset[1], this);

        if (!mPullOverlay) {
            mTarget.animate().translationX(windowOffset[0]).translationY(windowOffset[1]).setDuration(ANIMATION_DURATION).start();
        }
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

        void updateOffset(boolean refreshing, int offsetX, int offsetY, int absThreshold, int windowOffsetX, int windowOffsetY, PullLayout pullLayout);

    }

}

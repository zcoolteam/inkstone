package com.zcool.inkstone.lang;

/**
 * 进度辅助类，协助进度计算
 */
public class Progress {

    private long mTotal;
    private long mCurrent;

    public Progress() {
    }

    public Progress(long total, long current) {
        mTotal = total;
        mCurrent = current;
    }

    public void set(long total, long current) {
        mTotal = total;
        mCurrent = current;
        notifyUpdate();
    }

    public long getTotal() {
        return mTotal;
    }

    public void setTotal(long total) {
        this.mTotal = total;
        notifyUpdate();
    }

    public long getCurrent() {
        return mCurrent;
    }

    public void setCurrent(long current) {
        this.mCurrent = current;
        notifyUpdate();
    }

    public void notifyUpdate() {
        onUpdate();
    }

    /**
     * 当进度信息发生了变更时被调用
     */
    protected void onUpdate() {
    }

    /**
     * 进度 [0 - 100], 默认0
     */
    public int getPercent() {
        int percent = 0;
        if (mTotal > 0 && mCurrent > 0) {
            if (mCurrent <= mTotal) {
                percent = Float.valueOf(1f * mCurrent / mTotal * 100).intValue();
            } else {
                new IllegalStateException(
                        "error progress mCurrent/mTotal " + mCurrent + "/" + mTotal)
                        .printStackTrace();
            }
        }
        return percent;
    }

    /**
     * 在现有的进度上追加进度值
     */
    public static Progress append(Progress progress, long increase) {
        if (progress == null) {
            return null;
        }
        progress.setCurrent(progress.getCurrent() + increase);
        return progress;
    }

    /**
     * 设置新的进度值
     */
    public static Progress set(Progress progress, long current) {
        if (progress == null) {
            return null;
        }
        progress.setCurrent(current);
        return progress;
    }
}

package com.zcool.inkstone.thread;

import android.os.Handler;
import android.os.Looper;

import com.zcool.inkstone.manager.ProcessManager;

/**
 * 线程辅助类
 */
public class Threads {

    private static final Handler sHandlerUi = new Handler(Looper.getMainLooper());

    /**
     * 如果当前已经处于 ui 线程，则直接执行，否则 post 到 ui 线程执行。
     */
    public static void runOnUi(Runnable runnable) {
        if (isUi()) {
            runnable.run();
        } else {
            sHandlerUi.post(runnable);
        }
    }

    /**
     * 总是将任务 post 到 ui 线程执行，即使当前已经处于 ui 线程。
     */
    public static void postUi(Runnable runnable) {
        sHandlerUi.post(runnable);
    }

    public static void postUi(Runnable runnable, long delayMillis) {
        sHandlerUi.postDelayed(runnable, delayMillis);
    }

    public static void postBackgroundAfterLooper(final Runnable runnable) {
        postUi(new Runnable() {
            @Override
            public void run() {
                postBackground(runnable);
            }
        });
    }

    public static void postBackground(Runnable runnable) {
        ThreadPool.getInstance().post(runnable);
    }

    public static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean isUi() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void mustUi() {
        if (!isUi()) {
            Throwable e = new IllegalAccessError("此处需要是 ui 线程");
            e.printStackTrace();
        }
    }

    public static void mustNotUi() {
        if (isUi()) {
            Throwable e = new IllegalAccessError("此处不允许为 ui 线程");
            e.printStackTrace();
        }
    }

    public static void mustMainProcess() {
        if (!ProcessManager.getInstance().isMainProcess()) {
            Throwable e = new IllegalAccessError("此处需要是主进程");
            e.printStackTrace();
        }
    }

    public static void mustNotMainProcess() {
        if (ProcessManager.getInstance().isMainProcess()) {
            Throwable e = new IllegalAccessError("此处不允许为主进程");
            e.printStackTrace();
        }
    }
}

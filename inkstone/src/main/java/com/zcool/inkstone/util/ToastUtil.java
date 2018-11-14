package com.zcool.inkstone.util;

import android.widget.Toast;

import com.zcool.inkstone.thread.Threads;

public class ToastUtil {

    private ToastUtil() {
    }

    public static void show(final String msg) {
        Threads.postUi(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextUtil.getContext(), String.valueOf(msg), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showLong(final String msg) {
        Threads.postUi(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextUtil.getContext(), String.valueOf(msg), Toast.LENGTH_LONG).show();
            }
        });
    }

}

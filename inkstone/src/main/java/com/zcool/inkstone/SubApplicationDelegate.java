package com.zcool.inkstone;

import android.content.Context;

import androidx.annotation.Keep;

@Keep
public interface SubApplicationDelegate {

    void onCreate(Context context);

    void onStartBackgroundService();

}

package com.zcool.inkstone;

import android.content.Context;

import androidx.annotation.Keep;

@Keep
public interface ModuleApplicationDelegate {

    void onCreate(Context context);

    void onStartBackgroundService();

}

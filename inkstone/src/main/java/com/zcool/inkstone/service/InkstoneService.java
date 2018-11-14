package com.zcool.inkstone.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zcool.inkstone.Inkstone;

public class InkstoneService extends Service {

    public static void start(Context context) {
        Intent intent = new Intent(context, InkstoneService.class);
        context.getApplicationContext().startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Inkstone.init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
package com.zcool.inkstone.service;

import android.os.IBinder;

interface ICoreService {

    IBinder getService(String serviceName);

}

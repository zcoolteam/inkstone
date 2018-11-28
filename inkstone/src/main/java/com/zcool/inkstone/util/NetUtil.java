package com.zcool.inkstone.util;

import android.content.Context;
import android.net.ConnectivityManager;

import androidx.core.net.ConnectivityManagerCompat;

/**
 * 网络相关
 */
public class NetUtil {

    /**
     * 是否联网
     */
    public static boolean hasActiveNetwork() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)
                        ContextUtil.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    /**
     * 当前活动网络是否是计量网络(计量网络通常会产生流量费用)
     */
    public static boolean isActiveNetworkMetered() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)
                        ContextUtil.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        return ConnectivityManagerCompat.isActiveNetworkMetered(connectivityManager);
    }

}

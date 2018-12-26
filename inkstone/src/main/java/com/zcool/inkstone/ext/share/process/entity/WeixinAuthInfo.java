package com.zcool.inkstone.ext.share.process.entity;

import android.os.Bundle;

import timber.log.Timber;

public class WeixinAuthInfo {
    public String code;
    public String openId;

    private static final String _LOCAL_FLAG_KEY = "__inkstone_local_flag";
    private static final String _LOCAL_FLAG_VALUE = "WeixinAuthInfo";

    @Override
    public String toString() {
        return "WeixinAuthInfo{" +
                "code='" + code + '\'' +
                ", openId='" + openId + '\'' +
                '}';
    }

    public void writeToBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return;
        }

        bundle.putString(_LOCAL_FLAG_KEY, _LOCAL_FLAG_VALUE);
        bundle.putString("code", this.code);
        bundle.putString("openId", this.openId);
    }

    public static WeixinAuthInfo readFromBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return null;
        }

        if (!(_LOCAL_FLAG_VALUE.equals(bundle.getString(_LOCAL_FLAG_KEY)))) {
            Timber.e("__inkstone_local_flag not match: %s", bundle.getString(_LOCAL_FLAG_KEY));
            return null;
        }

        WeixinAuthInfo target = new WeixinAuthInfo();
        target.code = bundle.getString("code");
        target.openId = bundle.getString("openId");
        return target;
    }
}

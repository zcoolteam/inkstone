package com.zcool.inkstone.ext.share.process;

import android.os.Bundle;

import androidx.annotation.Nullable;
import timber.log.Timber;

public class WeiboAuthInfo {
    public String access_token;
    public String refresh_token;
    public String expires_in;
    public String uid;
    public String phone_num;

    private static final String _LOCAL_FLAG_KEY = "__inkstone_local_flag";
    private static final String _LOCAL_FLAG_VALUE = "WeiboAuthInfo";

    @Override
    public String toString() {
        return "WeiboAuthInfo{" +
                "access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", uid='" + uid + '\'' +
                ", phone_num='" + phone_num + '\'' +
                '}';
    }

    public void writeToBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return;
        }

        bundle.putString(_LOCAL_FLAG_KEY, _LOCAL_FLAG_VALUE);

        bundle.putString("access_token", this.access_token);
        bundle.putString("refresh_token", this.refresh_token);
        bundle.putString("expires_in", this.expires_in);
        bundle.putString("uid", this.uid);
        bundle.putString("phone_num", this.phone_num);
    }

    @Nullable
    public static WeiboAuthInfo readFromBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return null;
        }

        if (!(_LOCAL_FLAG_VALUE.equals(bundle.getString(_LOCAL_FLAG_KEY)))) {
            Timber.e("__inkstone_local_flag not match: %s", bundle.getString(_LOCAL_FLAG_KEY));
            return null;
        }

        WeiboAuthInfo target = new WeiboAuthInfo();
        target.access_token = bundle.getString("access_token");
        target.refresh_token = bundle.getString("refresh_token");
        target.expires_in = bundle.getString("expires_in");
        target.uid = bundle.getString("uid");
        target.phone_num = bundle.getString("phone_num");
        return target;
    }

}

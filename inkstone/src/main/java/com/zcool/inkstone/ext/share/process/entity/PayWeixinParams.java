package com.zcool.inkstone.ext.share.process.entity;

import android.os.Bundle;

import com.zcool.inkstone.ext.share.ShareConfig;

import timber.log.Timber;

/**
 * 微信支付
 */
public class PayWeixinParams {

    private static final String _LOCAL_FLAG_KEY = "__inkstone_local_flag";
    private static final String _LOCAL_FLAG_VALUE = "PayWeixinParams";

    public String appId = ShareConfig.getWeixinAppKey();
    public String partnerId;
    public String prepayId;
    public String nonceStr;
    public String timeStamp;
    public String packageValue;
    public String sign;

    public void writeToBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return;
        }

        bundle.putString(_LOCAL_FLAG_KEY, _LOCAL_FLAG_VALUE);

        bundle.putString("appId", this.appId);
        bundle.putString("partnerId", this.partnerId);
        bundle.putString("prepayId", this.prepayId);
        bundle.putString("nonceStr", this.nonceStr);
        bundle.putString("timeStamp", this.timeStamp);
        bundle.putString("packageValue", this.packageValue);
        bundle.putString("sign", this.sign);
    }

    public static PayWeixinParams readFromBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return null;
        }

        if (!(_LOCAL_FLAG_VALUE.equals(bundle.getString(_LOCAL_FLAG_KEY)))) {
            Timber.e("__inkstone_local_flag not match: %s", bundle.getString(_LOCAL_FLAG_KEY));
            return null;
        }

        PayWeixinParams target = new PayWeixinParams();
        target.appId = bundle.getString("appId");
        target.partnerId = bundle.getString("partnerId");
        target.prepayId = bundle.getString("prepayId");
        target.nonceStr = bundle.getString("nonceStr");
        target.timeStamp = bundle.getString("timeStamp");
        target.packageValue = bundle.getString("packageValue");
        target.sign = bundle.getString("sign");
        return target;
    }

}

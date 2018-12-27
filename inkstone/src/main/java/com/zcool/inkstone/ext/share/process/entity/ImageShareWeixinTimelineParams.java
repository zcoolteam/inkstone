package com.zcool.inkstone.ext.share.process.entity;

import android.os.Bundle;

import timber.log.Timber;

/**
 * 分享单图到朋友圈
 */
public class ImageShareWeixinTimelineParams {

    private static final String _LOCAL_FLAG_KEY = "__inkstone_local_flag";
    private static final String _LOCAL_FLAG_VALUE = "ImageShareWeixinTimelineParams";

    /**
     * 分享的图片，本地地址
     */
    public String localImage;
    /**
     * 缩略图 不大于 32k
     */
    public byte[] thumbImage;

    public void writeToBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return;
        }

        bundle.putString(_LOCAL_FLAG_KEY, _LOCAL_FLAG_VALUE);

        bundle.putString("localImage", this.localImage);
        bundle.putByteArray("thumbImage", this.thumbImage);
    }

    public static ImageShareWeixinTimelineParams readFromBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return null;
        }

        if (!(_LOCAL_FLAG_VALUE.equals(bundle.getString(_LOCAL_FLAG_KEY)))) {
            Timber.e("__inkstone_local_flag not match: %s", bundle.getString(_LOCAL_FLAG_KEY));
            return null;
        }

        ImageShareWeixinTimelineParams target = new ImageShareWeixinTimelineParams();
        target.localImage = bundle.getString("localImage");
        target.thumbImage = bundle.getByteArray("thumbImage");
        return target;
    }

}

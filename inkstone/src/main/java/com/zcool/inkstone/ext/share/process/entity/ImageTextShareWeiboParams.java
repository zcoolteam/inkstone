package com.zcool.inkstone.ext.share.process.entity;

import android.os.Bundle;

import timber.log.Timber;

/**
 * 分享图文到微博
 */
public class ImageTextShareWeiboParams {

    private static final String _LOCAL_FLAG_KEY = "__inkstone_local_flag";
    private static final String _LOCAL_FLAG_VALUE = "ImageTextShareWeiboParams";

    /**
     * 分享的正文
     */
    public String content;

    /**
     * 分享的图片，仅支持本地地址 (文件大小不能超过 10M)
     */
    public String image;

    public void writeToBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return;
        }

        bundle.putString(_LOCAL_FLAG_KEY, _LOCAL_FLAG_VALUE);

        bundle.putString("content", this.content);
        bundle.putString("image", this.image);
    }

    public static ImageTextShareWeiboParams readFromBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return null;
        }

        if (!(_LOCAL_FLAG_VALUE.equals(bundle.getString(_LOCAL_FLAG_KEY)))) {
            Timber.e("__inkstone_local_flag not match: %s", bundle.getString(_LOCAL_FLAG_KEY));
            return null;
        }

        ImageTextShareWeiboParams target = new ImageTextShareWeiboParams();
        target.content = bundle.getString("content");
        target.image = bundle.getString("image");
        return target;
    }

}

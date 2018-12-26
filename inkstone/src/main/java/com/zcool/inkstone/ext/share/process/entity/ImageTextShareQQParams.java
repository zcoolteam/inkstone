package com.zcool.inkstone.ext.share.process.entity;

import android.os.Bundle;

import timber.log.Timber;

/**
 * 分享图文到 QQ 好友
 */
public class ImageTextShareQQParams {

    private static final String _LOCAL_FLAG_KEY = "__inkstone_local_flag";
    private static final String _LOCAL_FLAG_VALUE = "ImageTextShareQQParams";

    /**
     * 分享的标题
     */
    public String title;
    /**
     * 分享的正文
     */
    public String content;
    /**
     * 点击链接
     */
    public String targetUrl;
    /**
     * 分享的图片，本地或者网络地址
     */
    public String image;

    public void writeToBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return;
        }

        bundle.putString(_LOCAL_FLAG_KEY, _LOCAL_FLAG_VALUE);

        bundle.putString("title", this.title);
        bundle.putString("content", this.content);
        bundle.putString("targetUrl", this.targetUrl);
        bundle.putString("image", this.image);
    }

    public static ImageTextShareQQParams readFromBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return null;
        }

        if (!(_LOCAL_FLAG_VALUE.equals(bundle.getString(_LOCAL_FLAG_KEY)))) {
            Timber.e("__inkstone_local_flag not match: %s", bundle.getString(_LOCAL_FLAG_KEY));
            return null;
        }

        ImageTextShareQQParams target = new ImageTextShareQQParams();
        target.title = bundle.getString("title");
        target.content = bundle.getString("content");
        target.targetUrl = bundle.getString("targetUrl");
        target.image = bundle.getString("image");
        return target;
    }

}

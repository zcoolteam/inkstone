package com.zcool.inkstone.ext.share.process.entity;

import android.os.Bundle;

import timber.log.Timber;

/**
 * 分享图文到微信朋友圈
 */
public class ImageTextShareWeixinTimelineParams {

    private static final String _LOCAL_FLAG_KEY = "__inkstone_local_flag";
    private static final String _LOCAL_FLAG_VALUE = "ImageTextShareWeixinTimelineParams";

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
     * 缩略图 不大于 32k
     */
    public byte[] image;

    public void writeToBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return;
        }

        bundle.putString(_LOCAL_FLAG_KEY, _LOCAL_FLAG_VALUE);

        bundle.putString("title", this.title);
        bundle.putString("content", this.content);
        bundle.putString("targetUrl", this.targetUrl);
        bundle.putByteArray("image", this.image);
    }

    public static ImageTextShareWeixinTimelineParams readFromBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return null;
        }

        if (!(_LOCAL_FLAG_VALUE.equals(bundle.getString(_LOCAL_FLAG_KEY)))) {
            Timber.e("__inkstone_local_flag not match: %s", bundle.getString(_LOCAL_FLAG_KEY));
            return null;
        }

        ImageTextShareWeixinTimelineParams target = new ImageTextShareWeixinTimelineParams();
        target.title = bundle.getString("title");
        target.content = bundle.getString("content");
        target.targetUrl = bundle.getString("targetUrl");
        target.image = bundle.getByteArray("image");
        return target;
    }

}

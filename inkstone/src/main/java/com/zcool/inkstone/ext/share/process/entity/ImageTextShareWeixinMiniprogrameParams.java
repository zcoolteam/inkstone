package com.zcool.inkstone.ext.share.process.entity;

import android.os.Bundle;

import timber.log.Timber;

/**
 * 分享图文到微信小程序
 */
public class ImageTextShareWeixinMiniprogrameParams {

    private static final String _LOCAL_FLAG_KEY = "__inkstone_local_flag";
    private static final String _LOCAL_FLAG_VALUE = "ImageTextShareWeixinMiniprogrameParams";

    /**
     * 分享的标题
     */
    public String title;
    /**
     * 分享的正文
     */
    public String content;
    /**
     * 低版本微信上兼容的网页链接地址
     */
    public String defaultTargetUrl;
    /**
     * 缩略图 不大于 128k
     */
    public byte[] image;

    public String miniProgramId;
    public String miniProgramPath;

    public void writeToBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return;
        }

        bundle.putString(_LOCAL_FLAG_KEY, _LOCAL_FLAG_VALUE);

        bundle.putString("title", this.title);
        bundle.putString("content", this.content);
        bundle.putString("defaultTargetUrl", this.defaultTargetUrl);
        bundle.putByteArray("image", this.image);
        bundle.putString("miniProgramId", this.miniProgramId);
        bundle.putString("miniProgramPath", this.miniProgramPath);
    }

    public static ImageTextShareWeixinMiniprogrameParams readFromBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return null;
        }

        if (!(_LOCAL_FLAG_VALUE.equals(bundle.getString(_LOCAL_FLAG_KEY)))) {
            Timber.e("__inkstone_local_flag not match: %s", bundle.getString(_LOCAL_FLAG_KEY));
            return null;
        }

        ImageTextShareWeixinMiniprogrameParams target = new ImageTextShareWeixinMiniprogrameParams();
        target.title = bundle.getString("title");
        target.content = bundle.getString("content");
        target.defaultTargetUrl = bundle.getString("defaultTargetUrl");
        target.image = bundle.getByteArray("image");
        target.miniProgramId = bundle.getString("miniProgramId");
        target.miniProgramPath = bundle.getString("miniProgramPath");
        return target;
    }

}

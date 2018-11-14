package com.zcool.inkstone.ext.share;

import android.text.TextUtils;

import com.sina.weibo.sdk.utils.LogUtil;
import com.zcool.inkstone.BaseApplicationDelegate;

/**
 * 配置第三方平台参数, 在 App 入口处配置. 通常只需要在主进程 (ui 所在进程) 中配置.
 */
public final class ShareConfig {

    private static String sQQAppId;

    private static String sWeixinAppKey;
    private static String sWeixinAppSecret;

    private static String sWeiboAppKey;
    private static String sWeiboRedirectUrl;

    private static void init(Builder builder) {
        sQQAppId = builder.mQQAppId;

        sWeixinAppKey = builder.mWeixinAppKey;
        sWeixinAppSecret = builder.mWeixinAppSecret;

        sWeiboAppKey = builder.mWeiboAppKey;
        sWeiboRedirectUrl = builder.mWeiboRedirectUrl;

        if (BaseApplicationDelegate.getInstance().isDebug()) {
            LogUtil.enableLog();
        }
    }

    public static boolean hasConfigQQ() {
        return !TextUtils.isEmpty(ShareConfig.getQQAppId());
    }

    public static boolean hasConfigWeixin() {
        return !TextUtils.isEmpty(ShareConfig.getWeixinAppKey());
    }

    public static boolean hasConfigWeibo() {
        return !TextUtils.isEmpty(ShareConfig.getWeiboAppKey());
    }

    public static String getQQAppId() {
        return sQQAppId;
    }

    public static String getWeiboAppKey() {
        return sWeiboAppKey;
    }

    public static String getWeiboRedirectUrl() {
        return sWeiboRedirectUrl;
    }

    public static String getWeixinAppKey() {
        return sWeixinAppKey;
    }

    public static String getWeixinAppSecret() {
        return sWeixinAppSecret;
    }

    public static final class Builder {
        private String mQQAppId;

        private String mWeixinAppKey;
        // 用 code 换取 access_token 时会用到
        private String mWeixinAppSecret;

        private String mWeiboAppKey;
        private String mWeiboRedirectUrl = "https://api.weibo.com/oauth2/default.html";

        public Builder setQQ(String appId) {
            mQQAppId = appId;
            return this;
        }

        public Builder setWeixin(String appKey, String appSecret) {
            mWeixinAppKey = appKey;
            mWeixinAppSecret = appSecret;
            return this;
        }

        public Builder setWeibo(String appKey) {
            mWeiboAppKey = appKey;
            return this;
        }

        public Builder setWeiboRedirectUrl(String redirectUrl) {
            mWeiboRedirectUrl = redirectUrl;
            return this;
        }

        public void init() {
            ShareConfig.init(this);
        }
    }

}

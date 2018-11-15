package com.zcool.inkstone.ext.share.util;

import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zcool.inkstone.ext.share.ShareHelper;
import com.zcool.inkstone.ext.share.qq.ShareQQHelper;
import com.zcool.inkstone.ext.share.weibo.ShareWeiboHelper;
import com.zcool.inkstone.ext.share.weixin.ShareWeixinHelper;

import java.util.ArrayList;
import java.util.UUID;

import timber.log.Timber;

public class ShareUtil {

    private ShareUtil() {
    }

    public static class QQShareContent {

        public String title;
        public String content;
        /**
         * 点击链接
         */
        public String targetUrl;
        /**
         * 分享的图片，本地或者网络地址
         */
        public String image;
    }

    private static Bundle convertQQShareContent(QQShareContent shareContent) {
        Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        if (!TextUtils.isEmpty(shareContent.title)) {
            bundle.putString(QQShare.SHARE_TO_QQ_TITLE, shareContent.title);
        }
        if (!TextUtils.isEmpty(shareContent.content)) {
            bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent.content);
        }
        if (!TextUtils.isEmpty(shareContent.targetUrl)) {
            bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareContent.targetUrl);
        }
        if (!TextUtils.isEmpty(shareContent.image)) {
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareContent.image);
        }
        return bundle;
    }

    /**
     * 分享到 QQ 好友
     */
    public static boolean shareToQQ(ShareHelper shareHelper, QQShareContent shareContent) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (shareContent == null) {
            Timber.e("shareContent is null");
            return false;
        }

        ShareQQHelper shareQQHelper = shareHelper.getShareQQHelper();
        if (shareQQHelper == null) {
            Timber.e("shareQQHelper is null");
            return false;
        }

        Tencent tencent = shareQQHelper.getTencent(shareHelper.getActivity());
        if (tencent == null) {
            Timber.e("tencent is null");
            return false;
        }

        tencent.shareToQQ(
                shareHelper.getActivity(),
                convertQQShareContent(shareContent),
                shareHelper.getShareQQHelper().getShareListener());
        return true;
    }

    public static class QzoneShareContent {

        public String title;
        public String content;
        /**
         * 点击链接
         */
        public String targetUrl;
        /**
         * 分享的图片，仅支持网络地址
         */
        public String image;
    }

    private static Bundle convertQzoneShareContent(QzoneShareContent shareContent) {
        Bundle bundle = new Bundle();
        bundle.putInt(
                QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        if (!TextUtils.isEmpty(shareContent.title)) {
            bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareContent.title);
        }
        if (!TextUtils.isEmpty(shareContent.content)) {
            bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.content);
        }
        if (!TextUtils.isEmpty(shareContent.targetUrl)) {
            bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareContent.targetUrl);
        }
        if (!TextUtils.isEmpty(shareContent.image)) {
            ArrayList<String> images = new ArrayList<>();
            images.add(shareContent.image);
            bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);
        }
        return bundle;
    }

    /**
     * 分享到 QQ 空间
     */
    public static boolean shareToQzone(ShareHelper shareHelper, QzoneShareContent shareContent) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (shareContent == null) {
            Timber.e("shareContent is null");
            return false;
        }

        ShareQQHelper shareQQHelper = shareHelper.getShareQQHelper();
        if (shareQQHelper == null) {
            Timber.e("shareQQHelper is null");
            return false;
        }

        Tencent tencent = shareQQHelper.getTencent(shareHelper.getActivity());
        if (tencent == null) {
            Timber.e("tencent is null");
            return false;
        }

        tencent.shareToQzone(
                shareHelper.getActivity(),
                convertQzoneShareContent(shareContent),
                shareHelper.getShareQQHelper().getShareListener());
        return true;
    }

    public static class WeixinShareContent {
        public String title;
        public String content;
        /**
         * 点击链接
         */
        public String targetUrl;
        /**
         * 缩略图 不大于 32k
         */
        public byte[] image;
    }

    private static WXMediaMessage covertWeixinShareContent(WeixinShareContent shareContent) {
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = shareContent.targetUrl;

        WXMediaMessage mediaMessage = new WXMediaMessage(webpageObject);
        mediaMessage.title = shareContent.title;
        mediaMessage.description = shareContent.content;
        mediaMessage.thumbData = shareContent.image;
        return mediaMessage;
    }

    /**
     * 分享到微信好友
     */
    public static boolean shareToWeixin(ShareHelper shareHelper, WeixinShareContent shareContent) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (shareContent == null) {
            Timber.e("shareContent is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        IWXAPI api = shareWeixinHelper.getApi();
        if (api == null) {
            Timber.e("api is null");
            return false;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = UUID.randomUUID().toString();
        req.message = covertWeixinShareContent(shareContent);
        req.scene = SendMessageToWX.Req.WXSceneSession;
        return api.sendReq(req);
    }

    public static class WeixinMiniProgramShareContent {
        public String title;
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
    }

    private static WXMediaMessage covertWeixinMiniProgrameShareContent(
            WeixinMiniProgramShareContent shareContent) {
        WXMiniProgramObject miniProgramObject = new WXMiniProgramObject();
        miniProgramObject.webpageUrl = shareContent.defaultTargetUrl;
        miniProgramObject.userName = shareContent.miniProgramId;
        miniProgramObject.path = shareContent.miniProgramPath;

        WXMediaMessage mediaMessage = new WXMediaMessage(miniProgramObject);
        mediaMessage.title = shareContent.title;
        mediaMessage.description = shareContent.content;
        mediaMessage.thumbData = shareContent.image;
        return mediaMessage;
    }

    /**
     * 分享微信小程序到微信好友
     */
    public static boolean shareToWeixin(
            ShareHelper shareHelper, WeixinMiniProgramShareContent shareContent) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (shareContent == null) {
            Timber.e("shareContent is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        IWXAPI api = shareWeixinHelper.getApi();
        if (api == null) {
            Timber.e("api is null");
            return false;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = UUID.randomUUID().toString();
        req.message = covertWeixinMiniProgrameShareContent(shareContent);
        req.scene = SendMessageToWX.Req.WXSceneSession;
        return api.sendReq(req);
    }

    /**
     * 分享到微信朋友圈
     */
    public static boolean shareToWeixinTimeline(
            ShareHelper shareHelper, WeixinShareContent shareContent) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (shareContent == null) {
            Timber.e("shareContent is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        IWXAPI api = shareWeixinHelper.getApi();
        if (api == null) {
            Timber.e("api is null");
            return false;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = UUID.randomUUID().toString();
        req.message = covertWeixinShareContent(shareContent);
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        return api.sendReq(req);
    }

    public static class WeiboShareContent {

        public String content;

        /**
         * 分享的图片，仅支持本地地址 (文件大小不能超过 10M)
         */
        public String image;
    }

    private static WeiboMultiMessage convertWeiboShareContent(WeiboShareContent shareContent) {
        WeiboMultiMessage multiMessage = new WeiboMultiMessage();

        if (!TextUtils.isEmpty(shareContent.content)) {
            multiMessage.textObject = new TextObject();
            multiMessage.textObject.text = shareContent.content;
        }

        if (!TextUtils.isEmpty(shareContent.image)) {
            multiMessage.imageObject = new ImageObject();
            multiMessage.imageObject.imagePath = shareContent.image;
        }

        return multiMessage;
    }

    /**
     * 分享到微博
     */
    public static boolean shareToWeibo(ShareHelper shareHelper, WeiboShareContent shareContent) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (shareContent == null) {
            Timber.e("shareContent is null");
            return false;
        }

        ShareWeiboHelper shareWeiboHelper = shareHelper.getShareWeiboHelper();
        if (shareWeiboHelper == null) {
            Timber.e("shareWeiboHelper is null");
            return false;
        }

        WbShareHandler api = shareWeiboHelper.getIWeiboShareAPI();
        if (api == null) {
            Timber.e("api is null");
            return false;
        }

        api.shareMessage(convertWeiboShareContent(shareContent), false);
        return true;
    }

    public interface ShareListener {
        void onQQShareSuccess();

        void onQQShareFail();

        void onQQShareCancel();

        void onWeixinShareSuccess();

        void onWeixinShareFail();

        void onWeixinShareCancel();

        void onWeiboShareSuccess();

        void onWeiboShareFail();

        void onWeiboShareCancel();
    }

    public static ShareHelper.ShareListener newShareListener(final ShareListener shareListener) {
        return new ShareHelper.ShareListener() {
            @Override
            public void onQQComplete(Object o) {
                shareListener.onQQShareSuccess();
            }

            @Override
            public void onQQError(UiError uiError) {
                if (uiError == null) {
                    Timber.e("uiError is null");
                } else {
                    Timber.v(+uiError.errorCode
                            + " "
                            + uiError.errorMessage
                            + " "
                            + uiError.errorDetail);
                }
                shareListener.onQQShareFail();
            }

            @Override
            public void onQQCancel() {
                shareListener.onQQShareCancel();
            }

            @Override
            public void onWeixinCallback(BaseResp baseResp) {
                if (baseResp instanceof SendMessageToWX.Resp) {
                    SendMessageToWX.Resp shareResp = (SendMessageToWX.Resp) baseResp;
                    switch (shareResp.errCode) {
                        case SendMessageToWX.Resp.ErrCode.ERR_OK: {
                            shareListener.onWeixinShareSuccess();
                            break;
                        }
                        case SendMessageToWX.Resp.ErrCode.ERR_USER_CANCEL: {
                            shareListener.onWeixinShareCancel();
                            break;
                        }
                        default: {
                            shareListener.onWeixinShareFail();
                            break;
                        }
                    }
                    return;
                }

                Timber.e("unknown baseResp " + baseResp);
            }

            @Override
            public void onWeiboShareSuccess() {
                shareListener.onWeiboShareSuccess();
            }

            @Override
            public void onWeiboShareFail() {
                shareListener.onWeiboShareFail();
            }

            @Override
            public void onWeiboShareCancel() {
                shareListener.onWeiboShareCancel();
            }

        };
    }

    public static class SimpleShareListener implements ShareListener {

        @Override
        public void onQQShareSuccess() {
            Timber.v("onQQShareSuccess");
        }

        @Override
        public void onQQShareFail() {
            Timber.v("onQQShareFail");
        }

        @Override
        public void onQQShareCancel() {
            Timber.v("onQQShareCancel");
        }

        @Override
        public void onWeixinShareSuccess() {
            Timber.v("onWeixinShareSuccess");
        }

        @Override
        public void onWeixinShareFail() {
            Timber.v("onWeixinShareFail");
        }

        @Override
        public void onWeixinShareCancel() {
            Timber.v("onWeixinShareCancel");
        }

        @Override
        public void onWeiboShareSuccess() {
            Timber.v("onWeiboShareSuccess");
        }

        @Override
        public void onWeiboShareFail() {
            Timber.v("onWeiboShareFail");
        }

        @Override
        public void onWeiboShareCancel() {
            Timber.v("onWeiboShareCancel");
        }
    }

}

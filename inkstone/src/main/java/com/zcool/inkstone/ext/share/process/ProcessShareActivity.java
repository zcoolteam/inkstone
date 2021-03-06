package com.zcool.inkstone.ext.share.process;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;
import com.zcool.inkstone.ext.share.LifecycleShareHelper;
import com.zcool.inkstone.ext.share.ShareHelper;
import com.zcool.inkstone.ext.share.process.entity.ImageShareQQParams;
import com.zcool.inkstone.ext.share.process.entity.ImageShareWeixinParams;
import com.zcool.inkstone.ext.share.process.entity.ImageShareWeixinTimelineParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareQQParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareQzoneParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareWeiboParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareWeixinMiniprogrameParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareWeixinParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareWeixinTimelineParams;
import com.zcool.inkstone.ext.share.process.entity.PayWeixinParams;
import com.zcool.inkstone.ext.share.process.entity.QQAuthInfo;
import com.zcool.inkstone.ext.share.process.entity.WeiboAuthInfo;
import com.zcool.inkstone.ext.share.process.entity.WeixinAuthInfo;
import com.zcool.inkstone.ext.share.qq.ShareQQHelper;
import com.zcool.inkstone.ext.share.util.AuthUtil;
import com.zcool.inkstone.ext.share.util.PayUtil;
import com.zcool.inkstone.ext.share.util.ShareUtil;
import com.zcool.inkstone.ext.share.weibo.ShareWeiboHelper;
import com.zcool.inkstone.ext.share.weixin.ShareWeixinHelper;
import com.zcool.inkstone.thread.Threads;

import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import timber.log.Timber;

public class ProcessShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.v("onCreate %s", savedInstanceState);

        if (!startRedirect(getIntent())) {
            finish();
        }
    }

    @UiThread
    private boolean startRedirect(@NonNull Intent intent) {
        final String processShareAction = ProcessShareHelper.getProcessShareAction(intent);
        final String subType = ProcessShareHelper.getProcessShareActionSubType(intent);

        ShareHelper shareHelper = getShareHelper(true);
        if (shareHelper == null) {
            Timber.e("share helper is null");
            return false;
        }

        if (ProcessShareHelper.isQQAuth(processShareAction)) {
            return requestQQAuth(shareHelper);
        } else if (ProcessShareHelper.isWeixinAuth(processShareAction)) {
            return requestWeixinAuth(shareHelper);
        } else if (ProcessShareHelper.isWeiboAuth(processShareAction)) {
            return requestWeiboAuth(shareHelper);
        } else if (ProcessShareHelper.isQQShare(processShareAction)) {
            if (ProcessShareHelper.isProcessShareActionSubTypeImageText(subType)) {
                // 图文分享到 QQ 好友
                return requestQQShare(shareHelper, ImageTextShareQQParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else if (ProcessShareHelper.isProcessShareActionSubTypeImage(subType)) {
                // 分享单图到 QQ 好友
                return requestQQShare(shareHelper, ImageShareQQParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else {
                Timber.e("unknown sub type %s for processShareAction %s", subType, processShareAction);
                return false;
            }
        } else if (ProcessShareHelper.isQzoneShare(processShareAction)) {
            if (ProcessShareHelper.isProcessShareActionSubTypeImageText(subType)) {
                // 图文分享到 QQ 空间
                return requestQzoneShare(shareHelper, ImageTextShareQzoneParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else {
                Timber.e("unknown sub type %s for processShareAction %s", subType, processShareAction);
                return false;
            }
        } else if (ProcessShareHelper.isWeixinShare(processShareAction)) {
            if (ProcessShareHelper.isProcessShareActionSubTypeImageText(subType)) {
                // 图文分享到微信好友
                return requestWeixinShare(shareHelper, ImageTextShareWeixinParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else if (ProcessShareHelper.isProcessShareActionSubTypeImage(subType)) {
                // 分享单图到微信好友
                return requestWeixinShare(shareHelper, ImageShareWeixinParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else {
                Timber.e("unknown sub type %s for processShareAction %s", subType, processShareAction);
                return false;
            }
        } else if (ProcessShareHelper.isWeixinPay(processShareAction)) {
            if (ProcessShareHelper.isProcessShareActionSubTypePay(subType)) {
                // 微信支付
                return requestWeixinPay(shareHelper, PayWeixinParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else {
                Timber.e("unknown sub type %s for processShareAction %s", subType, processShareAction);
                return false;
            }
        } else if (ProcessShareHelper.isWeixinTimelineShare(processShareAction)) {
            if (ProcessShareHelper.isProcessShareActionSubTypeImageText(subType)) {
                // 图文分享到朋友圈
                return requestWeixinTimelineShare(shareHelper, ImageTextShareWeixinTimelineParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else if (ProcessShareHelper.isProcessShareActionSubTypeImage(subType)) {
                // 分享单图到朋友圈
                return requestWeixinTimelineShare(shareHelper, ImageShareWeixinTimelineParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else {
                Timber.e("unknown sub type %s for processShareAction %s", subType, processShareAction);
                return false;
            }
        } else if (ProcessShareHelper.isWeixinMiniprogrameShare(processShareAction)) {
            if (ProcessShareHelper.isProcessShareActionSubTypeImageText(subType)) {
                // 图文分享到小程序
                return requestWeixinMiniprogrameShare(shareHelper, ImageTextShareWeixinMiniprogrameParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else {
                Timber.e("unknown sub type %s for processShareAction %s", subType, processShareAction);
                return false;
            }
        } else if (ProcessShareHelper.isWeiboShare(processShareAction)) {
            if (ProcessShareHelper.isProcessShareActionSubTypeImageText(subType)) {
                // 图文分享到微博
                return requestWeiboShare(shareHelper, ImageTextShareWeiboParams.readFromBundle(
                        ProcessShareHelper.getProcessShareActionRequestData(intent)));
            } else {
                Timber.e("unknown sub type %s for processShareAction %s", subType, processShareAction);
                return false;
            }
        } else {
            Timber.e("unknown process share action:%s", processShareAction);
            return false;
        }
    }

    @UiThread
    private boolean requestQQAuth(ShareHelper shareHelper) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        ShareQQHelper shareQQHelper = shareHelper.getShareQQHelper();
        if (shareQQHelper == null) {
            Timber.e("shareQQHelper is null");
            return false;
        }

        final Tencent tencent = shareQQHelper.getTencent();

        tencent.logout(shareHelper.getActivity());
        tencent.login(
                shareHelper.getActivity(),
                "get_simple_userinfo",
                shareQQHelper.getAuthListener());
        return true;
    }

    @UiThread
    private boolean requestWeixinAuth(ShareHelper shareHelper) {
        if (shareHelper == null) {
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        IWXAPI api = shareWeixinHelper.getApi();

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = UUID.randomUUID().toString();
        return api.sendReq(req);
    }

    @UiThread
    private boolean requestWeiboAuth(ShareHelper shareHelper) {
        if (shareHelper == null) {
            return false;
        }

        ShareWeiboHelper shareWeiboHelper = shareHelper.getShareWeiboHelper();
        if (shareWeiboHelper == null) {
            Timber.e("shareWeiboHelper is null");
            return false;
        }

        SsoHandler ssoHandler = shareWeiboHelper.getSsoHandler();
        ssoHandler.authorize(shareWeiboHelper.getAuthListener());
        return true;
    }

    @UiThread
    private boolean requestQQShare(ShareHelper shareHelper, ImageTextShareQQParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareQQHelper shareQQHelper = shareHelper.getShareQQHelper();
        if (shareQQHelper == null) {
            Timber.e("shareQQHelper is null");
            return false;
        }

        final Tencent tencent = shareQQHelper.getTencent();

        Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        if (!TextUtils.isEmpty(params.title)) {
            bundle.putString(QQShare.SHARE_TO_QQ_TITLE, params.title);
        }
        if (!TextUtils.isEmpty(params.content)) {
            bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, params.content);
        }
        if (!TextUtils.isEmpty(params.targetUrl)) {
            bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, params.targetUrl);
        }
        if (!TextUtils.isEmpty(params.image)) {
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, params.image);
        }

        tencent.shareToQQ(
                shareHelper.getActivity(),
                bundle,
                shareQQHelper.getShareListener());
        return true;
    }

    @UiThread
    private boolean requestQQShare(ShareHelper shareHelper, ImageShareQQParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareQQHelper shareQQHelper = shareHelper.getShareQQHelper();
        if (shareQQHelper == null) {
            Timber.e("shareQQHelper is null");
            return false;
        }

        final Tencent tencent = shareQQHelper.getTencent();

        Bundle bundle = new Bundle();

        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, params.localImage);

        tencent.shareToQQ(
                shareHelper.getActivity(),
                bundle,
                shareQQHelper.getShareListener());
        return true;
    }

    @UiThread
    private boolean requestQzoneShare(ShareHelper shareHelper, ImageTextShareQzoneParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareQQHelper shareQQHelper = shareHelper.getShareQQHelper();
        if (shareQQHelper == null) {
            Timber.e("shareQQHelper is null");
            return false;
        }

        final Tencent tencent = shareQQHelper.getTencent();

        Bundle bundle = new Bundle();
        bundle.putInt(
                QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        if (!TextUtils.isEmpty(params.title)) {
            bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, params.title);
        }
        if (!TextUtils.isEmpty(params.content)) {
            bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, params.content);
        }
        if (!TextUtils.isEmpty(params.targetUrl)) {
            bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, params.targetUrl);
        }
        if (!TextUtils.isEmpty(params.image)) {
            ArrayList<String> images = new ArrayList<>();
            images.add(params.image);
            bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);
        }

        tencent.shareToQzone(
                shareHelper.getActivity(),
                bundle,
                shareQQHelper.getShareListener());
        return true;
    }

    @UiThread
    private boolean requestWeixinShare(ShareHelper shareHelper, ImageTextShareWeixinParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        final IWXAPI iwxapi = shareWeixinHelper.getApi();

        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = params.targetUrl;
        WXMediaMessage mediaMessage = new WXMediaMessage(webpageObject);
        mediaMessage.title = params.title;
        mediaMessage.description = params.content;
        mediaMessage.thumbData = params.image;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = UUID.randomUUID().toString();
        req.message = mediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        return iwxapi.sendReq(req);
    }

    @UiThread
    private boolean requestWeixinShare(ShareHelper shareHelper, ImageShareWeixinParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        final IWXAPI iwxapi = shareWeixinHelper.getApi();

        WXImageObject imageObject = new WXImageObject();
        imageObject.imagePath = params.localImage;
        WXMediaMessage mediaMessage = new WXMediaMessage(imageObject);
        mediaMessage.thumbData = params.thumbImage;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = UUID.randomUUID().toString();
        req.message = mediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        return iwxapi.sendReq(req);
    }

    @UiThread
    private boolean requestWeixinPay(ShareHelper shareHelper, PayWeixinParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        final IWXAPI iwxapi = shareWeixinHelper.getApi();

        PayReq req = new PayReq();
        req.appId = params.appId;
        req.partnerId = params.partnerId;
        req.prepayId = params.prepayId;
        req.nonceStr = params.nonceStr;
        req.timeStamp = params.timeStamp;
        req.packageValue = params.packageValue;
        req.sign = params.sign;

        return iwxapi.sendReq(req);
    }

    @UiThread
    private boolean requestWeixinTimelineShare(ShareHelper shareHelper, ImageTextShareWeixinTimelineParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        final IWXAPI iwxapi = shareWeixinHelper.getApi();

        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = params.targetUrl;
        WXMediaMessage mediaMessage = new WXMediaMessage(webpageObject);
        mediaMessage.title = params.title;
        mediaMessage.description = params.content;
        mediaMessage.thumbData = params.image;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = UUID.randomUUID().toString();
        req.message = mediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        return iwxapi.sendReq(req);
    }

    @UiThread
    private boolean requestWeixinTimelineShare(ShareHelper shareHelper, ImageShareWeixinTimelineParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        final IWXAPI iwxapi = shareWeixinHelper.getApi();

        WXImageObject imageObject = new WXImageObject();
        imageObject.imagePath = params.localImage;
        WXMediaMessage mediaMessage = new WXMediaMessage(imageObject);
        mediaMessage.thumbData = params.thumbImage;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = UUID.randomUUID().toString();
        req.message = mediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        return iwxapi.sendReq(req);
    }

    @UiThread
    private boolean requestWeixinMiniprogrameShare(ShareHelper shareHelper, ImageTextShareWeixinMiniprogrameParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = shareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null");
            return false;
        }

        final IWXAPI iwxapi = shareWeixinHelper.getApi();

        WXMiniProgramObject miniProgramObject = new WXMiniProgramObject();
        miniProgramObject.webpageUrl = params.defaultTargetUrl;
        miniProgramObject.userName = params.miniProgramId;
        miniProgramObject.path = params.miniProgramPath;

        WXMediaMessage mediaMessage = new WXMediaMessage(miniProgramObject);
        mediaMessage.title = params.title;
        mediaMessage.description = params.content;
        mediaMessage.thumbData = params.image;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = UUID.randomUUID().toString();
        req.message = mediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        return iwxapi.sendReq(req);
    }

    @UiThread
    private boolean requestWeiboShare(ShareHelper shareHelper, ImageTextShareWeiboParams params) {
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return false;
        }

        if (params == null) {
            Timber.e("params is null");
            return false;
        }

        ShareWeiboHelper shareWeiboHelper = shareHelper.getShareWeiboHelper();
        if (shareWeiboHelper == null) {
            Timber.e("shareWeiboHelper is null");
            return false;
        }

        final WbShareHandler shareHandler = shareWeiboHelper.getIWeiboShareAPI();

        WeiboMultiMessage multiMessage = new WeiboMultiMessage();

        if (!TextUtils.isEmpty(params.content)) {
            multiMessage.textObject = new TextObject();
            multiMessage.textObject.text = params.content;
        }

        if (!TextUtils.isEmpty(params.image)) {
            multiMessage.imageObject = new ImageObject();
            multiMessage.imageObject.imagePath = params.image;
        }

        shareHandler.shareMessage(multiMessage, false);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Timber.v("onActivityResult requestCode:%s, resultCode:%s, data:%s", requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        ShareHelper shareHelper = getShareHelper(false);
        if (shareHelper != null) {
            shareHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ShareHelper mShareHelper;

    @Nullable
    public ShareHelper getShareHelper(boolean autoCreate) {
        if (mShareHelper == null && autoCreate) {
            mShareHelper = LifecycleShareHelper.create(
                    this,
                    mAuthListener,
                    mShareListener,
                    mPayListener);
        }
        return mShareHelper;
    }

    private ShareHelper.PayListener mPayListener = PayUtil.newPayListener(new PayUtil.SimplePayListener() {
        @Override
        public void onWeixinPaySuccess() {
            super.onWeixinPaySuccess();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_PAY);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinPayFail() {
            super.onWeixinPayFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_PAY);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinPayCancel() {
            super.onWeixinPayCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_PAY);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }
    });

    private ShareHelper.AuthListener mAuthListener = AuthUtil.newAuthListener(new AuthUtil.SimpleAuthListener() {
        @Override
        public void onQQAuthSuccess(@NonNull QQAuthInfo info) {
            super.onQQAuthSuccess(info);

            Intent data = new Intent();
            Bundle extrasData = new Bundle();
            if (info != null) {
                info.writeToBundle(extrasData);
            }
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT_DATA, extrasData);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onQQAuthFail() {
            super.onQQAuthFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onQQAuthCancel() {
            super.onQQAuthCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinAuthSuccess(@NonNull WeixinAuthInfo info) {
            super.onWeixinAuthSuccess(info);

            Intent data = new Intent();
            Bundle extrasData = new Bundle();
            if (info != null) {
                info.writeToBundle(extrasData);
            }
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT_DATA, extrasData);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinAuthFail() {
            super.onWeixinAuthFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinAuthCancel() {
            super.onWeixinAuthCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboAuthSuccess(@NonNull WeiboAuthInfo info) {
            super.onWeiboAuthSuccess(info);

            Intent data = new Intent();
            Bundle extrasData = new Bundle();
            if (info != null) {
                info.writeToBundle(extrasData);
            }
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT_DATA, extrasData);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboAuthFail() {
            super.onWeiboAuthFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboAuthCancel() {
            super.onWeiboAuthCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }
    });

    private ShareHelper.ShareListener mShareListener = ShareUtil.newShareListener(new ShareUtil.SimpleShareListener() {
        @Override
        public void onQQShareSuccess() {
            super.onQQShareSuccess();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onQQShareFail() {
            super.onQQShareFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onQQShareCancel() {
            super.onQQShareCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinShareSuccess() {
            super.onWeixinShareSuccess();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinShareFail() {
            super.onWeixinShareFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinShareCancel() {
            super.onWeixinShareCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboShareSuccess() {
            super.onWeiboShareSuccess();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboShareFail() {
            super.onWeiboShareFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboShareCancel() {
            super.onWeiboShareCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }
    });

    @Override
    protected void onResume() {
        Timber.v("onResume");
        super.onResume();

        // bug fix: 当分享到微信时, 如果选择了留在微信，那么将不会接收到任何回调通知.
        if (mHasPause) {
            Threads.postUi(new Runnable() {
                @Override
                public void run() {
                    ProcessShareActivity activity = ProcessShareActivity.this;
                    if (activity.isFinishing()) {
                        return;
                    }

                    if (activity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                        activity.finish();
                    }
                }
            }, 100);
        }
    }

    private boolean mHasPause;

    @Override
    protected void onPause() {
        Timber.v("onPause");
        super.onPause();

        mHasPause = true;
    }

    @Override
    protected void onRestart() {
        Timber.v("onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Timber.v("onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Timber.v("onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Timber.v("onDestroy");
        super.onDestroy();
    }

}

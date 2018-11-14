package com.zcool.inkstone.ext.share;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.zcool.inkstone.ext.share.qq.ShareQQHelper;
import com.zcool.inkstone.ext.share.weibo.ShareWeiboHelper;
import com.zcool.inkstone.ext.share.weixin.ShareWeixinHelper;
import com.zcool.inkstone.util.IOUtil;

import java.io.Closeable;
import java.io.IOException;

public class ShareHelper implements Closeable {

    private Activity mActivity;
    private ShareQQHelper mShareQQHelper;
    private ShareWeixinHelper mShareWeixinHelper;
    private ShareWeiboHelper mShareWeiboHelper;

    public ShareHelper(@NonNull Activity activity, @NonNull IShareListener listener) {
        mActivity = activity;

        if (ShareConfig.hasConfigQQ()) {
            mShareQQHelper = new ShareQQHelper(new IShareQQUiListenerAdapter(listener));
        }

        if (ShareConfig.hasConfigWeixin()) {
            mShareWeixinHelper = new ShareWeixinHelper(new IShareWeixinListenerAdapter(listener));
        }

        if (ShareConfig.hasConfigWeibo()) {
            mShareWeiboHelper =
                    new ShareWeiboHelper(
                            activity,
                            new IShareWeiboAuthListenerAdapter(listener),
                            new IShareWeiboShareListenerAdapter(listener));
        }
    }

    public Activity getActivity() {
        return mActivity;
    }

    public ShareQQHelper getShareQQHelper() {
        return mShareQQHelper;
    }

    public ShareWeixinHelper getShareWeixinHelper() {
        return mShareWeixinHelper;
    }

    public ShareWeiboHelper getShareWeiboHelper() {
        return mShareWeiboHelper;
    }

    public void resume() {
        if (mShareWeixinHelper != null) {
            mShareWeixinHelper.resume();
        }

        if (mShareWeiboHelper != null) {
            mShareWeiboHelper.resume();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mShareQQHelper != null) {
            if (mShareQQHelper.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }

        if (mShareWeiboHelper != null) {
            mShareWeiboHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void close() throws IOException {
        mActivity = null;
        IOUtil.closeQuietly(mShareQQHelper);
        IOUtil.closeQuietly(mShareWeixinHelper);
        IOUtil.closeQuietly(mShareWeiboHelper);
    }

    private static class IShareQQUiListenerAdapter implements IUiListener {

        @NonNull
        private final IShareListener mOutListener;

        private IShareQQUiListenerAdapter(@NonNull IShareListener outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onComplete(Object o) {
            mOutListener.onQQComplete(o);
        }

        @Override
        public void onError(UiError uiError) {
            mOutListener.onQQError(uiError);
        }

        @Override
        public void onCancel() {
            mOutListener.onQQCancel();
        }
    }

    private static class IShareWeixinListenerAdapter implements ShareWeixinHelper.IWXListener {

        @NonNull
        private final IShareListener mOutListener;

        private IShareWeixinListenerAdapter(@NonNull IShareListener outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onWXCallback(BaseResp baseResp) {
            mOutListener.onWeixinCallback(baseResp);
        }
    }

    private static class IShareWeiboAuthListenerAdapter implements WbAuthListener {

        @NonNull
        private final IShareListener mOutListener;

        private IShareWeiboAuthListenerAdapter(@NonNull IShareListener outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
            mOutListener.onWeiboAuthComplete(oauth2AccessToken);
        }

        @Override
        public void cancel() {
            mOutListener.onWeiboAuthCancel();
        }

        @Override
        public void onFailure(WbConnectErrorMessage e) {
            mOutListener.onWeiboAuthException(e);
        }
    }

    private static class IShareWeiboShareListenerAdapter implements WbShareCallback {

        @NonNull
        private final IShareListener mOutListener;

        private IShareWeiboShareListenerAdapter(@NonNull IShareListener outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onWbShareSuccess() {
            mOutListener.onWeiboShareSuccess();
        }

        @Override
        public void onWbShareCancel() {
            mOutListener.onWeiboShareCancel();
        }

        @Override
        public void onWbShareFail() {
            mOutListener.onWeiboShareFail();
        }
    }

    public interface IShareListener {

        void onQQComplete(Object o);

        void onQQError(UiError uiError);

        void onQQCancel();

        void onWeixinCallback(BaseResp baseResp);

        void onWeiboAuthComplete(Oauth2AccessToken oauth2AccessToken);

        void onWeiboAuthException(WbConnectErrorMessage e);

        void onWeiboAuthCancel();

        void onWeiboShareSuccess();

        void onWeiboShareFail();

        void onWeiboShareCancel();
    }

    public static class SimpleShareListener implements IShareListener {

        @Override
        public void onQQComplete(Object o) {
            // ignore
        }

        @Override
        public void onQQError(UiError uiError) {
            // ignore
        }

        @Override
        public void onQQCancel() {
            // ignore
        }

        @Override
        public void onWeixinCallback(BaseResp baseResp) {
            // ignore
        }

        @Override
        public void onWeiboAuthComplete(Oauth2AccessToken oauth2AccessToken) {
            // ignore
        }

        @Override
        public void onWeiboAuthException(WbConnectErrorMessage e) {
            // ignore
        }

        @Override
        public void onWeiboAuthCancel() {
            // ignore
        }

        @Override
        public void onWeiboShareSuccess() {
            // ignore
        }

        @Override
        public void onWeiboShareFail() {
            // ignore
        }

        @Override
        public void onWeiboShareCancel() {
            // ignore
        }
    }
}

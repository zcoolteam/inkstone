package com.zcool.inkstone.ext.share;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    public ShareHelper(@NonNull Activity activity, @Nullable AuthListener authListener, @Nullable ShareListener shareListener) {
        mActivity = activity;

        if (ShareConfig.hasConfigQQ()) {
            mShareQQHelper = new ShareQQHelper(AuthListenerQQAdapter.create(authListener), ShareListenerQQAdapter.create(shareListener));
        }

        if (ShareConfig.hasConfigWeixin()) {
            mShareWeixinHelper = new ShareWeixinHelper(AuthListenerWeixinAdapter.create(authListener), ShareListenerWeixinAdapter.create(shareListener));
        }

        if (ShareConfig.hasConfigWeibo()) {
            mShareWeiboHelper =
                    new ShareWeiboHelper(
                            activity,
                            AuthListenerWeiboAdapter.create(authListener),
                            ShareListenerWeiboAdapter.create(shareListener));
        }
    }

    public Activity getActivity() {
        return mActivity;
    }

    @Nullable
    public ShareQQHelper getShareQQHelper() {
        return mShareQQHelper;
    }

    @Nullable
    public ShareWeixinHelper getShareWeixinHelper() {
        return mShareWeixinHelper;
    }

    @Nullable
    public ShareWeiboHelper getShareWeiboHelper() {
        return mShareWeiboHelper;
    }

    public void setAuthListener(@Nullable AuthListener authListener) {
        if (mShareQQHelper != null) {
            mShareQQHelper.setAuthListener(AuthListenerQQAdapter.create(authListener));
        }

        if (mShareWeixinHelper != null) {
            mShareWeixinHelper.setAuthListener(AuthListenerWeixinAdapter.create(authListener));
        }

        if (mShareWeiboHelper != null) {
            mShareWeiboHelper.setAuthListener(AuthListenerWeiboAdapter.create(authListener));
        }
    }

    public void setShareListener(@Nullable ShareListener shareListener) {
        if (mShareQQHelper != null) {
            mShareQQHelper.setShareListener(ShareListenerQQAdapter.create(shareListener));
        }

        if (mShareWeixinHelper != null) {
            mShareWeixinHelper.setShareListener(ShareListenerWeixinAdapter.create(shareListener));
        }

        if (mShareWeiboHelper != null) {
            mShareWeiboHelper.setShareListener(ShareListenerWeiboAdapter.create(shareListener));
        }
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

    private static class ShareListenerQQAdapter implements IUiListener {

        @NonNull
        private final ShareListener mOutListener;

        private ShareListenerQQAdapter(@NonNull ShareListener outListener) {
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

        @Nullable
        public static ShareListenerQQAdapter create(@Nullable ShareListener shareListener) {
            return shareListener == null ? null : new ShareListenerQQAdapter(shareListener);
        }

    }

    private static class AuthListenerQQAdapter implements IUiListener {

        @NonNull
        private final AuthListener mOutListener;

        private AuthListenerQQAdapter(@NonNull AuthListener outListener) {
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

        @Nullable
        public static AuthListenerQQAdapter create(@Nullable AuthListener authListener) {
            return authListener == null ? null : new AuthListenerQQAdapter(authListener);
        }
    }

    private static class AuthListenerWeixinAdapter implements ShareWeixinHelper.IWXListener {

        @NonNull
        private final AuthListener mOutListener;

        private AuthListenerWeixinAdapter(@NonNull AuthListener outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onWXCallback(BaseResp baseResp) {
            mOutListener.onWeixinCallback(baseResp);
        }

        @Nullable
        public static AuthListenerWeixinAdapter create(@Nullable AuthListener authListener) {
            return authListener == null ? null : new AuthListenerWeixinAdapter(authListener);
        }
    }

    private static class ShareListenerWeixinAdapter implements ShareWeixinHelper.IWXListener {

        @NonNull
        private final ShareListener mOutListener;

        private ShareListenerWeixinAdapter(@NonNull ShareListener outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onWXCallback(BaseResp baseResp) {
            mOutListener.onWeixinCallback(baseResp);
        }

        @Nullable
        public static ShareListenerWeixinAdapter create(@Nullable ShareListener shareListener) {
            return shareListener == null ? null : new ShareListenerWeixinAdapter(shareListener);
        }
    }

    private static class AuthListenerWeiboAdapter implements WbAuthListener {

        @NonNull
        private final AuthListener mOutListener;

        private AuthListenerWeiboAdapter(@NonNull AuthListener outListener) {
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

        @Nullable
        public static AuthListenerWeiboAdapter create(@Nullable AuthListener authListener) {
            return authListener == null ? null : new AuthListenerWeiboAdapter(authListener);
        }
    }

    private static class ShareListenerWeiboAdapter implements WbShareCallback {

        @NonNull
        private final ShareListener mOutListener;

        private ShareListenerWeiboAdapter(@NonNull ShareListener outListener) {
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

        @Nullable
        public static ShareListenerWeiboAdapter create(@Nullable ShareListener shareListener) {
            return shareListener == null ? null : new ShareListenerWeiboAdapter(shareListener);
        }
    }

    public interface ShareListener {

        void onQQComplete(Object o);

        void onQQError(UiError uiError);

        void onQQCancel();

        void onWeixinCallback(BaseResp baseResp);

        void onWeiboShareSuccess();

        void onWeiboShareFail();

        void onWeiboShareCancel();
    }

    public interface AuthListener {

        void onQQComplete(Object o);

        void onQQError(UiError uiError);

        void onQQCancel();

        void onWeixinCallback(BaseResp baseResp);

        void onWeiboAuthComplete(Oauth2AccessToken oauth2AccessToken);

        void onWeiboAuthException(WbConnectErrorMessage e);

        void onWeiboAuthCancel();
    }

}

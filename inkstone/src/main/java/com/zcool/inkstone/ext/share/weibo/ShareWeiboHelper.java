package com.zcool.inkstone.ext.share.weibo;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.zcool.inkstone.ext.share.ShareConfig;
import com.zcool.inkstone.util.ContextUtil;

import java.io.Closeable;
import java.io.IOException;

import timber.log.Timber;

/**
 * 微博登陆分享
 */
public final class ShareWeiboHelper implements Closeable {

    private final Activity mActivity;
    private final WeiboAuthListenerAdapter mListener;
    private final SsoHandler mSsoHandler;

    private static final GlobalWeiboHandlerResponseAdapter sGlobalWeiboHandlerResponseAdapter = new GlobalWeiboHandlerResponseAdapter();
    private final WbShareHandler mIWeiboShareAPI;
    private final WeiboShareListenerAdapter mShareListenerAdapter;

    private static boolean sInstall;

    public ShareWeiboHelper(Activity activity, WbAuthListener listener, WbShareCallback shareListener) {

        if (!sInstall) {
            sInstall = true;
            WbSdk.install(ContextUtil.getContext(), new AuthInfo(ContextUtil.getContext(), ShareConfig.getWeiboAppKey(), ShareConfig.getWeiboRedirectUrl(), null));
        }

        mActivity = activity;
        mListener = new WeiboAuthListenerAdapter();
        mListener.setOutListener(listener);

        mSsoHandler = new SsoHandler(activity);

        mShareListenerAdapter = new WeiboShareListenerAdapter();
        mShareListenerAdapter.setOutListener(shareListener);

        mIWeiboShareAPI = new WbShareHandler(activity);
        mIWeiboShareAPI.registerApp();
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void resume() {
        sGlobalWeiboHandlerResponseAdapter.setListenerProxy(mShareListenerAdapter);
    }

    @NonNull
    public SsoHandler getSsoHandler() {
        return mSsoHandler;
    }

    @NonNull
    public WeiboAuthListenerAdapter getListener() {
        return mListener;
    }

    @NonNull
    public WbShareHandler getIWeiboShareAPI() {
        return mIWeiboShareAPI;
    }

    public boolean isAppInstall() {
        return WbSdk.isWbInstall(ContextUtil.getContext());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        mIWeiboShareAPI.doResultIntent(data, mShareListenerAdapter);
    }

    @Override
    public void close() throws IOException {
        mListener.setOutListener(null);
        mShareListenerAdapter.setOutListener(null);
    }

    private static class WeiboAuthListenerAdapter implements WbAuthListener {

        private WbAuthListener mOutListener;

        public void setOutListener(WbAuthListener outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
            if (mOutListener != null) {
                mOutListener.onSuccess(oauth2AccessToken);
            }
        }

        @Override
        public void onFailure(WbConnectErrorMessage e) {
            if (mOutListener != null) {
                mOutListener.onFailure(e);
            }
        }

        @Override
        public void cancel() {
            if (mOutListener != null) {
                mOutListener.cancel();
            }
        }

    }

    private static class WeiboShareListenerAdapter implements WbShareCallback {

        private WbShareCallback mOutListener;

        public void setOutListener(WbShareCallback outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onWbShareSuccess() {
            if (mOutListener != null) {
                mOutListener.onWbShareSuccess();
            }
        }

        @Override
        public void onWbShareCancel() {
            if (mOutListener != null) {
                mOutListener.onWbShareCancel();
            }
        }

        @Override
        public void onWbShareFail() {
            if (mOutListener != null) {
                mOutListener.onWbShareFail();
            }
        }
    }

    public static WbShareCallback getGlobalWeiboHandlerResponseAdapter() {
        return sGlobalWeiboHandlerResponseAdapter;
    }

    private static class GlobalWeiboHandlerResponseAdapter implements WbShareCallback {

        private WbShareCallback mListenerProxy;
        private PendingResponse mPendingResponse;

        private class PendingResponse {
            final boolean success;
            final boolean cancel;
            final boolean fail;

            private PendingResponse(boolean success, boolean cancel, boolean fail) {
                this.success = success;
                this.cancel = cancel;
                this.fail = fail;
            }
        }

        public void setListenerProxy(WbShareCallback listenerProxy) {
            mListenerProxy = listenerProxy;
            if (mPendingResponse != null) {
                if (mPendingResponse.success) {
                    mListenerProxy.onWbShareSuccess();
                } else if (mPendingResponse.cancel) {
                    mListenerProxy.onWbShareCancel();
                } else if (mPendingResponse.fail) {
                    mListenerProxy.onWbShareFail();
                } else {
                    Timber.e("unknown pending response");
                }
                mPendingResponse = null;
            }
        }

        @Override
        public void onWbShareSuccess() {
            if (mListenerProxy != null) {
                mPendingResponse = null;
                mListenerProxy.onWbShareSuccess();
            } else {
                mPendingResponse = new PendingResponse(true, false, false);
            }
        }

        @Override
        public void onWbShareCancel() {
            if (mListenerProxy != null) {
                mPendingResponse = null;
                mListenerProxy.onWbShareCancel();
            } else {
                mPendingResponse = new PendingResponse(false, true, false);
            }
        }

        @Override
        public void onWbShareFail() {
            if (mListenerProxy != null) {
                mPendingResponse = null;
                mListenerProxy.onWbShareFail();
            } else {
                mPendingResponse = new PendingResponse(false, false, true);
            }
        }
    }

}

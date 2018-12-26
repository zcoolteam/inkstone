package com.zcool.inkstone.ext.share.qq;

import android.app.Activity;
import android.content.Intent;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zcool.inkstone.ext.share.ShareConfig;
import com.zcool.inkstone.util.ContextUtil;

import java.io.Closeable;
import java.io.IOException;

import androidx.annotation.NonNull;

/**
 * QQ 登陆分享，Qzone 分享
 */
public final class ShareQQHelper implements Closeable {

    private final Tencent mTencent;
    private final IUiListenerAdapter mAuthListener;
    private final IUiListenerAdapter mShareListener;

    public ShareQQHelper(IUiListener authListener, IUiListener shareListener) {
        mTencent = Tencent.createInstance(ShareConfig.getQQAppId(), ContextUtil.getContext());

        mAuthListener = new IUiListenerAdapter();
        mAuthListener.setOutListener(authListener);

        mShareListener = new IUiListenerAdapter();
        mShareListener.setOutListener(shareListener);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_LOGIN:
                Tencent.onActivityResultData(requestCode, resultCode, data, mAuthListener);
                return true;
            case Constants.REQUEST_QQ_SHARE:
            case Constants.REQUEST_QZONE_SHARE: {
                Tencent.onActivityResultData(requestCode, resultCode, data, mShareListener);
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public void setAuthListener(IUiListener authListener) {
        mAuthListener.setOutListener(authListener);
    }

    public void setShareListener(IUiListener shareListener) {
        mShareListener.setOutListener(shareListener);
    }

    @NonNull
    public Tencent getTencent(Activity activity) {
        return mTencent;
    }

    public boolean isAppInstall(Activity activity) {
        return mTencent.isSupportSSOLogin(activity);
    }

    @NonNull
    public Tencent getTencent() {
        return mTencent;
    }

    @NonNull
    public IUiListenerAdapter getAuthListener() {
        return mAuthListener;
    }

    @NonNull
    public IUiListenerAdapter getShareListener() {
        return mShareListener;
    }

    @Override
    public void close() throws IOException {
        mAuthListener.setOutListener(null);
        mShareListener.setOutListener(null);
    }

    private static class IUiListenerAdapter implements IUiListener {

        private IUiListener mOutListener;

        public void setOutListener(IUiListener outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onComplete(Object o) {
            if (mOutListener != null) {
                mOutListener.onComplete(o);
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (mOutListener != null) {
                mOutListener.onError(uiError);
            }
        }

        @Override
        public void onCancel() {
            if (mOutListener != null) {
                mOutListener.onCancel();
            }
        }
    }

}

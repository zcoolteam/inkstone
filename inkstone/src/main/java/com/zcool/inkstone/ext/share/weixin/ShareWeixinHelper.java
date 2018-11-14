package com.zcool.inkstone.ext.share.weixin;

import android.support.annotation.CheckResult;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zcool.inkstone.ext.share.ShareConfig;
import com.zcool.inkstone.util.ContextUtil;

import java.io.Closeable;
import java.io.IOException;

import timber.log.Timber;

/**
 * 微信登陆分享
 */
public final class ShareWeixinHelper implements Closeable {

    private IWXAPI mApi;
    private IWXListenerAdapter mListener;

    private static final GlobalWXAPIEventHandler sGlobalWXAPIEventHandler =
            new GlobalWXAPIEventHandler();

    public ShareWeixinHelper(IWXListener listener) {
        mApi = WXAPIFactory.createWXAPI(
                ContextUtil.getContext(), ShareConfig.getWeixinAppKey(), false);
        mApi.registerApp(ShareConfig.getWeixinAppKey());
        mListener = new IWXListenerAdapter();
        mListener.setOutListener(listener);
    }

    public void resume() {
        sGlobalWXAPIEventHandler.setListenerProxy(mListener);
    }

    /**
     * 如果没有安装微信客户端，或者微信客户端版本不支持，将返回 null.
     *
     * @return
     */
    @CheckResult
    public IWXAPI getApi() {
        if (mApi.isWXAppInstalled()) {
            return mApi;
        } else {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        mListener.setOutListener(null);
    }

    public interface IWXListener {
        void onWXCallback(BaseResp baseResp);
    }

    private static class IWXListenerAdapter implements IWXListener {

        private IWXListener mOutListener;

        public void setOutListener(IWXListener outListener) {
            mOutListener = outListener;
        }

        @Override
        public void onWXCallback(BaseResp baseResp) {
            if (mOutListener != null) {
                mOutListener.onWXCallback(baseResp);
            }
        }
    }

    public static IWXAPIEventHandler getGlobalWXAPIEventHandler() {
        return sGlobalWXAPIEventHandler;
    }

    private static final class GlobalWXAPIEventHandler implements IWXAPIEventHandler {

        private IWXListener mListenerProxy;
        private BaseResp mPendingBaseResp;

        public void setListenerProxy(IWXListener listenerProxy) {
            mListenerProxy = listenerProxy;
            if (mPendingBaseResp != null) {
                mListenerProxy.onWXCallback(mPendingBaseResp);
                mPendingBaseResp = null;
            }
        }

        @Override
        public void onReq(BaseReq baseReq) {
            Timber.v("baseReq " + baseReq);
        }

        @Override
        public void onResp(BaseResp baseResp) {
            Timber.v("baseResp " + baseResp);
            if (mListenerProxy != null) {
                mPendingBaseResp = null;
                mListenerProxy.onWXCallback(baseResp);
            } else {
                mPendingBaseResp = baseResp;
            }
        }
    }
}

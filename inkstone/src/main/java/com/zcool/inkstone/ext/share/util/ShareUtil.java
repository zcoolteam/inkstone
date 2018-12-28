package com.zcool.inkstone.ext.share.util;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.tauth.UiError;
import com.zcool.inkstone.ext.share.ShareHelper;

import timber.log.Timber;

public class ShareUtil {

    private ShareUtil() {
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

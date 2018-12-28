package com.zcool.inkstone.ext.share.util;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.zcool.inkstone.ext.share.ShareHelper;

import timber.log.Timber;

public class PayUtil {

    private PayUtil() {
    }

    public interface PayListener {

        void onWeixinPaySuccess();

        void onWeixinPayFail();

        void onWeixinPayCancel();

    }

    public static ShareHelper.PayListener newPayListener(final PayListener payListener) {
        return new ShareHelper.PayListener() {
            @Override
            public void onWeixinCallback(BaseResp baseResp) {
                if (baseResp instanceof PayResp) {
                    PayResp payResp = (PayResp) baseResp;
                    switch (payResp.errCode) {
                        case BaseResp.ErrCode.ERR_OK: {
                            payListener.onWeixinPaySuccess();
                            break;
                        }
                        case BaseResp.ErrCode.ERR_USER_CANCEL: {
                            payListener.onWeixinPayCancel();
                            break;
                        }
                        default: {
                            payListener.onWeixinPayFail();
                            break;
                        }
                    }
                    return;
                }

                Timber.e("unknown baseResp " + baseResp);
            }
        };
    }

    public static class SimplePayListener implements PayListener {

        @Override
        public void onWeixinPaySuccess() {
            Timber.v("onWeixinPaySuccess");
        }

        @Override
        public void onWeixinPayFail() {
            Timber.v("onWeixinPayFail");
        }

        @Override
        public void onWeixinPayCancel() {
            Timber.v("onWeixinPayCancel");
        }

    }

}

package com.zcool.inkstone.ext.share.util;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.zcool.inkstone.ext.share.ShareConfig;
import com.zcool.inkstone.ext.share.ShareHelper;
import com.zcool.inkstone.ext.share.weixin.ShareWeixinHelper;

import timber.log.Timber;

public class PayUtil {

    private PayUtil() {
    }

    public static class WeixinPayParams {
        public String appId = ShareConfig.getWeixinAppKey();
        public String partnerId;
        public String prepayId;
        public String nonceStr;
        public String timeStamp;
        public String packageValue;
        public String sign;
    }

    /**
     * 请求微信支付
     */
    public static boolean requestWeixinPay(ShareHelper shareHelper, WeixinPayParams params) {
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

        IWXAPI wxapi = shareWeixinHelper.getApi();

        PayReq req = new PayReq();
        req.appId = params.appId;
        req.partnerId = params.partnerId;
        req.prepayId = params.prepayId;
        req.nonceStr = params.nonceStr;
        req.timeStamp = params.timeStamp;
        req.packageValue = params.packageValue;
        req.sign = params.sign;

        return wxapi.sendReq(req);
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

    public static class SamplePayListener implements PayListener {

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

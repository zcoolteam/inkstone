package com.zcool.inkstone.ext.share.util;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zcool.inkstone.ext.share.ShareHelper;
import com.zcool.inkstone.ext.share.process.QQAuthInfo;
import com.zcool.inkstone.ext.share.process.WeiboAuthInfo;
import com.zcool.inkstone.ext.share.process.WeixinAuthInfo;

import org.json.JSONObject;

import java.util.UUID;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class AuthUtil {

    private AuthUtil() {
    }

    public static boolean requestQQAuth(ShareHelper shareHelper) {
        if (shareHelper == null) {
            return false;
        }

        Tencent tencent = shareHelper.getShareQQHelper().getTencent(shareHelper.getActivity());
        if (tencent == null) {
            return false;
        }

        tencent.logout(shareHelper.getActivity());
        tencent.login(
                shareHelper.getActivity(),
                "get_simple_userinfo",
                shareHelper.getShareQQHelper().getAuthListener());
        return true;
    }

    public static boolean requestWeixinAuth(ShareHelper shareHelper) {
        if (shareHelper == null) {
            return false;
        }

        IWXAPI api = shareHelper.getShareWeixinHelper().getApi();
        if (api == null) {
            return false;
        }

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = UUID.randomUUID().toString();
        api.sendReq(req);
        return true;
    }

    public static boolean requestWeiboAuth(ShareHelper shareHelper) {
        if (shareHelper == null) {
            return false;
        }

        SsoHandler ssoHandler = shareHelper.getShareWeiboHelper().getSsoHandler();
        if (ssoHandler == null) {
            return false;
        }
        ssoHandler.authorize(shareHelper.getShareWeiboHelper().getAuthListener());
        return true;
    }

    public interface AuthListener {
        void onQQAuthSuccess(@NonNull QQAuthInfo info);

        void onQQAuthFail();

        void onQQAuthCancel();

        void onWeixinAuthSuccess(@NonNull WeixinAuthInfo info);

        void onWeixinAuthFail();

        void onWeixinAuthCancel();

        void onWeiboAuthSuccess(@NonNull WeiboAuthInfo info);

        void onWeiboAuthFail();

        void onWeiboAuthCancel();
    }

    public static ShareHelper.AuthListener newAuthListener(final AuthListener authListener) {
        return new ShareHelper.AuthListener() {
            @Override
            public void onQQComplete(Object o) {
                if (o instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) o;
                    QQAuthInfo info = new QQAuthInfo();
                    info.ret = jsonObject.optString("ret");
                    info.pay_token = jsonObject.optString("pay_token");
                    info.pf = jsonObject.optString("pf");
                    info.query_authority_cost = jsonObject.optString("query_authority_cost");
                    info.authority_cost = jsonObject.optString("authority_cost");
                    info.expires_in = jsonObject.optString("expires_in");
                    info.openid = jsonObject.optString("openid");
                    info.pfkey = jsonObject.optString("pfkey");
                    info.msg = jsonObject.optString("msg");
                    info.login_cost = jsonObject.optString("login_cost");
                    info.access_token = jsonObject.optString("access_token");
                    authListener.onQQAuthSuccess(info);
                } else {
                    Timber.e("o " + o);
                }
            }

            @Override
            public void onQQError(UiError uiError) {
                if (uiError == null) {
                    Timber.e("uiError is null");
                } else {
                    Timber.v(uiError.errorCode
                            + " "
                            + uiError.errorMessage
                            + " "
                            + uiError.errorDetail);
                }
                authListener.onQQAuthFail();
            }

            @Override
            public void onQQCancel() {
                authListener.onQQAuthCancel();
            }

            @Override
            public void onWeixinCallback(BaseResp baseResp) {
                if (baseResp instanceof SendAuth.Resp) {
                    SendAuth.Resp authResp = (SendAuth.Resp) baseResp;
                    switch (authResp.errCode) {
                        case SendAuth.Resp.ErrCode.ERR_OK: {
                            WeixinAuthInfo info = new WeixinAuthInfo();
                            info.code = authResp.code;
                            info.openId = authResp.openId;
                            authListener.onWeixinAuthSuccess(info);
                            break;
                        }
                        case SendAuth.Resp.ErrCode.ERR_USER_CANCEL: {
                            authListener.onWeixinAuthCancel();
                            break;
                        }
                        default: {
                            authListener.onWeixinAuthFail();
                            break;
                        }
                    }
                    return;
                }

                Timber.e("unknown baseResp " + baseResp);
            }

            @Override
            public void onWeiboAuthComplete(Oauth2AccessToken oauth2AccessToken) {
                if (oauth2AccessToken != null) {
                    WeiboAuthInfo info = new WeiboAuthInfo();
                    info.access_token = oauth2AccessToken.getToken();
                    info.refresh_token = oauth2AccessToken.getRefreshToken();
                    info.expires_in = String.valueOf(oauth2AccessToken.getExpiresTime());
                    info.uid = oauth2AccessToken.getUid();
                    info.phone_num = oauth2AccessToken.getPhoneNum();
                    authListener.onWeiboAuthSuccess(info);
                } else {
                    Timber.e("oauth2AccessToken is null");
                }
            }

            @Override
            public void onWeiboAuthException(WbConnectErrorMessage e) {
                authListener.onWeiboAuthFail();
            }

            @Override
            public void onWeiboAuthCancel() {
                authListener.onWeiboAuthCancel();
            }

        };
    }

    public static class SimpleAuthListener implements AuthListener {

        @Override
        public void onQQAuthSuccess(@NonNull QQAuthInfo info) {
            Timber.v("onQQAuthSuccess " + info);
        }

        @Override
        public void onQQAuthFail() {
            Timber.v("onQQAuthFail");
        }

        @Override
        public void onQQAuthCancel() {
            Timber.v("onQQAuthCancel");
        }

        @Override
        public void onWeixinAuthSuccess(@NonNull WeixinAuthInfo info) {
            Timber.v("onWeixinAuthSuccess " + info);
        }

        @Override
        public void onWeixinAuthFail() {
            Timber.v("onWeixinAuthFail");
        }

        @Override
        public void onWeixinAuthCancel() {
            Timber.v("onWeixinAuthCancel");
        }

        @Override
        public void onWeiboAuthSuccess(@NonNull WeiboAuthInfo info) {
            Timber.v("onWeiboAuthSuccess " + info);
        }

        @Override
        public void onWeiboAuthFail() {
            Timber.v("onWeiboAuthFail");
        }

        @Override
        public void onWeiboAuthCancel() {
            Timber.v("onWeiboAuthCancel");
        }
    }

}

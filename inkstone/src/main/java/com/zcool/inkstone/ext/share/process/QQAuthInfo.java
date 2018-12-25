package com.zcool.inkstone.ext.share.process;

import android.os.Bundle;

import timber.log.Timber;

public class QQAuthInfo {
    public String ret;
    public String pay_token;
    public String pf;
    public String query_authority_cost;
    public String authority_cost;
    public String expires_in;
    public String openid;
    public String pfkey;
    public String msg;
    public String login_cost;
    public String access_token;

    private static final String _LOCAL_FLAG_KEY = "__inkstone_local_flag";
    private static final String _LOCAL_FLAG_VALUE = "QQAuthInfo";

    @Override
    public String toString() {
        return "QQAuthInfo{" +
                "ret='" + ret + '\'' +
                ", pay_token='" + pay_token + '\'' +
                ", pf='" + pf + '\'' +
                ", query_authority_cost='" + query_authority_cost + '\'' +
                ", authority_cost='" + authority_cost + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", openid='" + openid + '\'' +
                ", pfkey='" + pfkey + '\'' +
                ", msg='" + msg + '\'' +
                ", login_cost='" + login_cost + '\'' +
                ", access_token='" + access_token + '\'' +
                '}';
    }

    public void writeToBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return;
        }

        bundle.putString(_LOCAL_FLAG_KEY, _LOCAL_FLAG_VALUE);
        bundle.putString("ret", this.ret);
        bundle.putString("pay_token", this.pay_token);
        bundle.putString("pf", this.pf);
        bundle.putString("query_authority_cost", this.query_authority_cost);
        bundle.putString("authority_cost", this.authority_cost);
        bundle.putString("expires_in", this.expires_in);
        bundle.putString("openid", this.openid);
        bundle.putString("pfkey", this.pfkey);
        bundle.putString("msg", this.msg);
        bundle.putString("login_cost", this.login_cost);
        bundle.putString("access_token", this.access_token);
    }

    public static QQAuthInfo readFromBundle(Bundle bundle) {
        if (bundle == null) {
            Timber.e("bundle is null");
            return null;
        }

        if (!(_LOCAL_FLAG_VALUE.equals(bundle.getString(_LOCAL_FLAG_KEY)))) {
            Timber.e("__inkstone_local_flag not match: %s", bundle.getString(_LOCAL_FLAG_KEY));
            return null;
        }

        QQAuthInfo target = new QQAuthInfo();
        target.ret = bundle.getString("ret");
        target.pay_token = bundle.getString("pay_token");
        target.pf = bundle.getString("pf");
        target.query_authority_cost = bundle.getString("query_authority_cost");
        target.authority_cost = bundle.getString("authority_cost");
        target.expires_in = bundle.getString("expires_in");
        target.openid = bundle.getString("openid");
        target.pfkey = bundle.getString("pfkey");
        target.msg = bundle.getString("msg");
        target.login_cost = bundle.getString("login_cost");
        target.access_token = bundle.getString("access_token");
        return target;
    }
}

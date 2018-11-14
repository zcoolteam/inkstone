package com.zcool.inkstone.ext.share.weixin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zcool.inkstone.ext.share.ShareConfig;
import com.zcool.inkstone.util.ContextUtil;

public class ShareWeixinAppRegister extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ShareConfig.hasConfigWeixin()) {
            IWXAPI api =
                    WXAPIFactory.createWXAPI(
                            ContextUtil.getContext(), ShareConfig.getWeixinAppKey(), false);
            api.registerApp(ShareConfig.getWeixinAppKey());
        }
    }
}

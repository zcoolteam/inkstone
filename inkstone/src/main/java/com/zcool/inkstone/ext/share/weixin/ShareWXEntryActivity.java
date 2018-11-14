package com.zcool.inkstone.ext.share.weixin;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zcool.inkstone.ext.share.ShareConfig;
import com.zcool.inkstone.ext.share.app.ShareActivity;
import com.zcool.inkstone.util.ContextUtil;

/**
 * 与微信通信页
 */
public class ShareWXEntryActivity extends ShareActivity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        finish();
    }

    private void handleIntent(Intent intent) {
        if (ShareConfig.hasConfigWeixin()) {
            IWXAPI api = WXAPIFactory.createWXAPI(
                    ContextUtil.getContext(), ShareConfig.getWeixinAppKey(), false);
            api.handleIntent(intent, this);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        ShareWeixinHelper.getGlobalWXAPIEventHandler().onReq(baseReq);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        ShareWeixinHelper.getGlobalWXAPIEventHandler().onResp(baseResp);
    }
}

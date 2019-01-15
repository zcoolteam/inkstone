package com.zcool.sample.module.share;

import android.os.Bundle;

import com.zcool.inkstone.ext.hierarchy.HierarchyDelegateHelper;
import com.zcool.inkstone.ext.share.process.ProcessShareHelper;
import com.zcool.inkstone.ext.share.process.entity.QQAuthInfo;
import com.zcool.inkstone.ext.share.process.entity.WeixinAuthInfo;
import com.zcool.inkstone.lang.SystemUiHelper;
import com.zcool.sample.entity.Session;
import com.zcool.sample.entity.User;
import com.zcool.sample.manager.SessionManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity implements ProcessShareHelper.ShareResultReceiverHost {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemUiHelper.from(getWindow())
                .layoutStatusBar()
                .layoutStable()
                .setLightStatusBar()
                .setLightNavigationBar()
                .apply();

        HierarchyDelegateHelper.showContentFragment(
                this,
                ShareFragment.class.getName(),
                null);
    }

    @Override
    public ProcessShareHelper.ShareResultReceiver getShareResultReceiver() {
        return null;
    }

    @Override
    public ProcessShareHelper.AuthResultReceiver getAuthResultReceiver() {
        return new ProcessShareHelper.SimpleAuthResultReceiver() {
            @Override
            public void onQQAuthSuccess(QQAuthInfo result) {
                super.onQQAuthSuccess(result);

                Session session = new Session();
                session.lastModify = System.currentTimeMillis();
                session.token = "qq:" + result.access_token;
                session.user = new User();
                session.user.id = 1;
                session.user.name = "qq auth";
                SessionManager.getInstance().setSession(session);
            }

            @Override
            public void onWeixinAuthSuccess(WeixinAuthInfo result) {
                super.onWeixinAuthSuccess(result);

                Session session = new Session();
                session.lastModify = System.currentTimeMillis();
                session.token = "weixin:" + result.openId;
                session.user = new User();
                session.user.id = 1;
                session.user.name = "weixin auth";
                SessionManager.getInstance().setSession(session);
            }

        };
    }

    @Override
    public ProcessShareHelper.PayResultReceiver getPayResultReceiver() {
        return null;
    }

}

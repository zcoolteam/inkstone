package com.zcool.sample.module.share;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zcool.inkstone.ext.share.ShareHelper;
import com.zcool.inkstone.ext.share.util.AuthUtil;
import com.zcool.inkstone.ext.share.util.ShareUtil;
import com.zcool.inkstone.util.ToastUtil;
import com.zcool.sample.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

public class ShareFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_share, container, false);
    }

    private Unbinder mUnbinder;

    @Nullable
    private ShareHelper getShareHelper() {
        return getShareHelper(true);
    }

    @Nullable
    private ShareHelper getShareHelper(boolean autoCreate) {
        Activity activity = getActivity();
        if (activity instanceof ShareHelperHost) {
            ShareHelper shareHelper = ((ShareHelperHost) activity).getShareHelper(autoCreate);
            return shareHelper;
        }

        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnbinder = ButterKnife.bind(this, view);
    }

    @OnClick(R.id.request_auth_qq)
    void onRequestAuthQQ() {
        ShareHelper shareHelper = getShareHelper();
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return;
        }

        shareHelper.setAuthListener(AuthUtil.newAuthListener(new AuthUtil.SimpleAuthListener() {
            @Override
            public void onQQAuthSuccess(@NonNull AuthUtil.QQAuthInfo info) {
                super.onQQAuthSuccess(info);

                ToastUtil.show("QQ 登录成功");
            }

            @Override
            public void onQQAuthFail() {
                super.onQQAuthFail();

                ToastUtil.show("QQ 登录失败");
            }
        }));

        AuthUtil.requestQQAuth(shareHelper);
    }

    @OnClick(R.id.request_auth_weibo)
    void onRequestAuthWeibo() {
        ShareHelper shareHelper = getShareHelper();
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return;
        }

        shareHelper.setAuthListener(AuthUtil.newAuthListener(new AuthUtil.SimpleAuthListener() {
            @Override
            public void onWeiboAuthSuccess(@NonNull AuthUtil.WeiboAuthInfo info) {
                super.onWeiboAuthSuccess(info);

                ToastUtil.show("微博登录成功");
            }

            @Override
            public void onWeiboAuthFail() {
                super.onWeiboAuthFail();

                ToastUtil.show("微博登录失败");
            }
        }));

        AuthUtil.requestWeiboAuth(shareHelper);
    }

    @OnClick(R.id.request_auth_weixin)
    void onRequestAuthWeixin() {
        ShareHelper shareHelper = getShareHelper();
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return;
        }

        shareHelper.setAuthListener(AuthUtil.newAuthListener(new AuthUtil.SimpleAuthListener() {
            @Override
            public void onWeixinAuthSuccess(@NonNull AuthUtil.WeixinAuthInfo info) {
                super.onWeixinAuthSuccess(info);

                ToastUtil.show("微信登录成功");
            }

            @Override
            public void onWeixinAuthFail() {
                super.onWeixinAuthFail();

                ToastUtil.show("微信登录失败");
            }
        }));

        AuthUtil.requestWeixinAuth(shareHelper);
    }

    @OnClick(R.id.request_share_qq)
    void onShareQQ() {
        ShareHelper shareHelper = getShareHelper();
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return;
        }

        shareHelper.setShareListener(ShareUtil.newShareListener(new ShareUtil.SimpleShareListener() {
            @Override
            public void onQQShareSuccess() {
                super.onQQShareSuccess();

                ToastUtil.show("QQ 分享成功");
            }

            @Override
            public void onQQShareFail() {
                super.onQQShareFail();

                ToastUtil.show("QQ 分享失败");
            }
        }));

        ShareUtil.QQShareContent shareContent = new ShareUtil.QQShareContent();
        shareContent.content = "QQ share test content";
        shareContent.title = "QQ share test title";
        ShareUtil.shareToQQ(shareHelper, shareContent);
    }

    @OnClick(R.id.request_share_weixin)
    void onShareWeixin() {
        ShareHelper shareHelper = getShareHelper();
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return;
        }

        shareHelper.setShareListener(ShareUtil.newShareListener(new ShareUtil.SimpleShareListener() {
            @Override
            public void onWeixinShareSuccess() {
                super.onWeixinShareSuccess();

                ToastUtil.show("微信分享成功");
            }

            @Override
            public void onWeixinShareFail() {
                super.onWeixinShareFail();

                ToastUtil.show("微信分享失败");
            }
        }));

        ShareUtil.WeixinShareContent shareContent = new ShareUtil.WeixinShareContent();
        shareContent.title = "weixin share test title";
        shareContent.content = "weixin share test content";
        ShareUtil.shareToWeixin(shareHelper, shareContent);
    }

    @OnClick(R.id.request_share_weibo)
    void onShareWeibo() {
        ShareHelper shareHelper = getShareHelper();
        if (shareHelper == null) {
            Timber.e("shareHelper is null");
            return;
        }

        shareHelper.setShareListener(ShareUtil.newShareListener(new ShareUtil.SimpleShareListener() {
            @Override
            public void onWeiboShareSuccess() {
                super.onWeiboShareSuccess();

                ToastUtil.show("微博分享成功");
            }

            @Override
            public void onWeiboShareFail() {
                super.onWeiboShareFail();

                ToastUtil.show("微博分享失败");
            }
        }));

        ShareUtil.WeiboShareContent shareContent = new ShareUtil.WeiboShareContent();
        shareContent.content = "weibo share test";
        ShareUtil.shareToWeibo(shareHelper, shareContent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

}

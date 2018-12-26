package com.zcool.inkstone.ext.share.process;

import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.WbSdk;
import com.tencent.tauth.Tencent;
import com.zcool.inkstone.ext.share.ShareHelper;
import com.zcool.inkstone.ext.share.qq.ShareQQHelper;
import com.zcool.inkstone.ext.share.weibo.ShareWeiboHelper;
import com.zcool.inkstone.ext.share.weixin.ShareWeixinHelper;
import com.zcool.inkstone.thread.Threads;
import com.zcool.inkstone.util.ContextUtil;
import com.zcool.inkstone.util.IOUtil;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import timber.log.Timber;

public class ProcessShareFragment extends Fragment {

    private static final String FRAGMENT_TAG = "inkstone.ProcessShareFragment";

    public static ProcessShareFragment newInstance() {
        Bundle args = new Bundle();
        ProcessShareFragment fragment = new ProcessShareFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ProcessShareFragment getOrCreate(FragmentActivity fragmentActivity) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        ProcessShareFragment fragment = (ProcessShareFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = ProcessShareFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commitNow();
        }

        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ProcessShareHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    private ShareHelper mLocalShareHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IOUtil.closeQuietly(mLocalShareHelper);
        mLocalShareHelper = new ShareHelper(getActivity(), null, null);
    }

    @UiThread
    public boolean isSupportQQAppAuth() {
        if (!Threads.mustUi()) {
            return false;
        }

        if (mLocalShareHelper == null) {
            Timber.e("mLocalShareHelper is null");
            return false;
        }

        ShareQQHelper shareQQHelper = mLocalShareHelper.getShareQQHelper();
        if (shareQQHelper == null) {
            Timber.e("shareQQHelper is null, you may not config qq share");
            return false;
        }

        Tencent tencent = shareQQHelper.getTencent();
        return tencent.isSupportSSOLogin(mLocalShareHelper.getActivity());
    }

    @UiThread
    public boolean isSupportQQAppShare() {
        if (!Threads.mustUi()) {
            return false;
        }

        if (mLocalShareHelper == null) {
            Timber.e("mLocalShareHelper is null");
            return false;
        }

        ShareQQHelper shareQQHelper = mLocalShareHelper.getShareQQHelper();
        if (shareQQHelper == null) {
            Timber.e("shareQQHelper is null, you may not config qq share");
            return false;
        }

        return Tencent.isSupportShareToQQ(mLocalShareHelper.getActivity(), false);
    }

    @UiThread
    public boolean isSupportQzoneAppShare() {
        if (!Threads.mustUi()) {
            return false;
        }

        if (mLocalShareHelper == null) {
            Timber.e("mLocalShareHelper is null");
            return false;
        }

        ShareQQHelper shareQQHelper = mLocalShareHelper.getShareQQHelper();
        if (shareQQHelper == null) {
            Timber.e("shareQQHelper is null, you may not config qq share");
            return false;
        }

        return Tencent.isSupportPushToQZone(mLocalShareHelper.getActivity());
    }

    @UiThread
    public boolean isSupportWeixinAppAuth() {
        if (!Threads.mustUi()) {
            return false;
        }

        if (mLocalShareHelper == null) {
            Timber.e("mLocalShareHelper is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = mLocalShareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null, you may not config weixin share");
            return false;
        }

        return shareWeixinHelper.getApi().isWXAppInstalled();
    }

    @UiThread
    public boolean isSupportWeixinAppShare() {
        if (!Threads.mustUi()) {
            return false;
        }

        if (mLocalShareHelper == null) {
            Timber.e("mLocalShareHelper is null");
            return false;
        }

        ShareWeixinHelper shareWeixinHelper = mLocalShareHelper.getShareWeixinHelper();
        if (shareWeixinHelper == null) {
            Timber.e("shareWeixinHelper is null, you may not config weixin share");
            return false;
        }

        return shareWeixinHelper.getApi().isWXAppInstalled();
    }

    @UiThread
    public boolean isSupportWeiboAppAuth() {
        if (!Threads.mustUi()) {
            return false;
        }

        if (mLocalShareHelper == null) {
            Timber.e("mLocalShareHelper is null");
            return false;
        }

        ShareWeiboHelper shareWeiboHelper = mLocalShareHelper.getShareWeiboHelper();
        if (shareWeiboHelper == null) {
            Timber.e("shareWeiboHelper is null, you may not config weibo share");
            return false;
        }

        return shareWeiboHelper.isAppInstall();
    }

    @UiThread
    public boolean isSupportWeiboAppShare() {
        if (!Threads.mustUi()) {
            return false;
        }

        if (mLocalShareHelper == null) {
            Timber.e("mLocalShareHelper is null");
            return false;
        }

        ShareWeiboHelper shareWeiboHelper = mLocalShareHelper.getShareWeiboHelper();
        if (shareWeiboHelper == null) {
            Timber.e("shareWeiboHelper is null, you may not config weibo share");
            return false;
        }

        return shareWeiboHelper.isAppInstall();
    }

    @UiThread
    public boolean isSupportWeiboAppShareWithMultiImage() {
        if (!Threads.mustUi()) {
            return false;
        }

        if (mLocalShareHelper == null) {
            Timber.e("mLocalShareHelper is null");
            return false;
        }

        ShareWeiboHelper shareWeiboHelper = mLocalShareHelper.getShareWeiboHelper();
        if (shareWeiboHelper == null) {
            Timber.e("shareWeiboHelper is null, you may not config weibo share");
            return false;
        }

        return WbSdk.supportMultiImage(ContextUtil.getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        IOUtil.closeQuietly(mLocalShareHelper);
        mLocalShareHelper = null;
    }

}

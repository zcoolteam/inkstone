package com.zcool.sample.module.third;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zcool.inkstone.ext.share.ShareHelper;
import com.zcool.inkstone.ext.share.util.AuthUtil;
import com.zcool.inkstone.thread.Threads;
import com.zcool.inkstone.util.ContextUtil;
import com.zcool.inkstone.util.IOUtil;
import com.zcool.sample.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ThirdFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThirdActivity activity = (ThirdActivity) getActivity();
        activity.setWeakFragmentResult(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_third, container, false);
    }

    private Unbinder mUnbinder;

    private ShareHelper mShareHelper;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnbinder = ButterKnife.bind(this, view);

        Activity activity = getActivity();
        if (activity != null) {
            mShareHelper = new ShareHelper(activity, AuthUtil.newAuthListener(new AuthUtil.AuthListener() {
                @Override
                public void onQQAuthSuccess(@NonNull AuthUtil.QQAuthInfo info) {
                    show("qq auth success " + info);
                }

                @Override
                public void onQQAuthFail() {
                    show("qq auth fail");
                }

                @Override
                public void onQQAuthCancel() {
                    show("qq auth cancel");
                }

                @Override
                public void onWeixinAuthSuccess(@NonNull AuthUtil.WeixinAuthInfo info) {
                    show("weixin auth success " + info);
                }

                @Override
                public void onWeixinAuthFail() {
                    show("weixin auth fail");
                }

                @Override
                public void onWeixinAuthCancel() {
                    show("weixin auth cancel");
                }

                @Override
                public void onWeiboAuthSuccess(@NonNull AuthUtil.WeiboAuthInfo info) {
                    show("weibo auth success " + info);
                }

                @Override
                public void onWeiboAuthFail() {
                    show("weibo auth fail");
                }

                @Override
                public void onWeiboAuthCancel() {
                    show("weibo auth cancel");
                }
            }));
        }
        if (isResumed()) {
            if (mShareHelper != null) {
                mShareHelper.resume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mShareHelper != null) {
            mShareHelper.resume();
        }
    }

    private void show(final String msg) {
        Threads.postUi(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextUtil.getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IOUtil.closeQuietly(mShareHelper);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mShareHelper != null) {
            mShareHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.request_auth_qq)
    void onRequestAuthQQ() {
        if (mShareHelper != null) {
            AuthUtil.requestQQAuth(mShareHelper);
        }
    }

    @OnClick(R.id.request_auth_weibo)
    void onRequestAuthWeibo() {
        if (mShareHelper != null) {
            AuthUtil.requestWeiboAuth(mShareHelper);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }

        if (mShareHelper != null) {
            IOUtil.closeQuietly(mShareHelper);
            mShareHelper = null;
        }
    }

}

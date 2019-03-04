package com.zcool.sample.module.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zcool.inkstone.ext.share.process.ProcessShareHelper;
import com.zcool.sample.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShareFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_share, container, false);
    }

    private Unbinder mUnbinder;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnbinder = ButterKnife.bind(this, view);

        mMsgView.setText(String.format("scrollX: %s -> %s\nscrollY: %s -> %s\nscrollLeft: %s\nscrollRight: %s\nscrollUp: %s\nscrollDown: %s",
                mScrollView.getScrollX(), mScrollView.getScrollX(), mScrollView.getScrollY(), mScrollView.getScrollY(),
                mScrollView.canScrollHorizontally(-1),
                mScrollView.canScrollHorizontally(1),
                mScrollView.canScrollVertically(-1),
                mScrollView.canScrollVertically(1)));
        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            mMsgView.setText(String.format("scrollX: %s -> %s\nscrollY: %s -> %s\nscrollLeft: %s\nscrollRight: %s\nscrollUp: %s\nscrollDown: %s",
                    oldScrollX, scrollX, oldScrollY, scrollY,
                    v.canScrollHorizontally(-1),
                    v.canScrollHorizontally(1),
                    v.canScrollVertically(-1),
                    v.canScrollVertically(1)));
        });
    }

    @OnClick(R.id.request_auth_qq)
    void onRequestAuthQQ() {
        ProcessShareHelper.requestQQAuth(getActivity());
    }

    @OnClick(R.id.request_auth_weibo)
    void onRequestAuthWeibo() {
        ProcessShareHelper.requestWeiboAuth(getActivity());
    }

    @OnClick(R.id.request_auth_weixin)
    void onRequestAuthWeixin() {
        ProcessShareHelper.requestWeixinAuth(getActivity());
    }

    @OnClick(R.id.request_share_qq)
    void onShareQQ() {
        // TODO
    }

    @OnClick(R.id.request_share_weixin)
    void onShareWeixin() {
        // TODO
    }

    @OnClick(R.id.request_share_weibo)
    void onShareWeibo() {
        // TODO
    }

    @BindView(R.id.msg_view)
    TextView mMsgView;
    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

}

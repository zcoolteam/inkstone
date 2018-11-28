package com.zcool.sample.module.design;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zcool.sample.R;
import com.zcool.sample.module.design.widget.ProgressViewFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class DesignFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_design, container, false);
    }

    private Unbinder mUnbinder;

    @BindView(R.id.app_main_content)
    ViewGroup mAppMainContent;

    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.progress_view_title)
    ProgressViewFrameLayout mProgressViewTitle;

    @BindView(R.id.pager)
    ViewPager mPager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnbinder = ButterKnife.bind(this, view);

        Activity activity = getActivity();
        if (activity != null) {
            mProgressViewTitle.setSystemUiWindow(activity.getWindow());
        } else {
            Timber.e("activity is null");
        }

        mPager.setAdapter(new DataAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

    private class DataAdapter extends FragmentPagerAdapter {

        public DataAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return DesignItemFragment.newInstance("item#" + i);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "item#" + position;
        }

        @Override
        public int getCount() {
            return 5;
        }

    }


}

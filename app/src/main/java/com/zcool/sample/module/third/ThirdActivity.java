package com.zcool.sample.module.third;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.zcool.inkstone.ext.hierarchy.HierarchyDelegateHelper;
import com.zcool.inkstone.lang.SystemUiHelper;

import java.lang.ref.WeakReference;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemUiHelper.from(getWindow())
                .layoutStatusBar()
                .layoutStable()
                .apply();

        HierarchyDelegateHelper.showContentFragment(
                this,
                ThirdFragment.class.getName(),
                null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mWeakFragmentResult != null) {
            Fragment fragmentResult = mWeakFragmentResult.get();
            if (fragmentResult != null) {
                fragmentResult.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private WeakReference<Fragment> mWeakFragmentResult;

    public void setWeakFragmentResult(Fragment fragment) {
        mWeakFragmentResult = new WeakReference<>(fragment);
    }

}

package com.zcool.sample.module.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zcool.inkstone.ext.hierarchy.HierarchyDelegateHelper;
import com.zcool.inkstone.ext.share.LifecyclerShareHelper;
import com.zcool.inkstone.ext.share.ShareHelper;
import com.zcool.inkstone.ext.share.util.AuthUtil;
import com.zcool.inkstone.ext.share.util.ShareUtil;
import com.zcool.inkstone.lang.SystemUiHelper;

public class ShareActivity extends AppCompatActivity implements ShareHelperHost {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemUiHelper.from(getWindow())
                .layoutStatusBar()
                .layoutStable()
                .apply();

        HierarchyDelegateHelper.showContentFragment(
                this,
                ShareFragment.class.getName(),
                null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ShareHelper shareHelper = getShareHelper(false);
        if (shareHelper != null) {
            shareHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ShareHelper mShareHelper;

    @Override
    public ShareHelper getShareHelper(boolean autoCreate) {
        if (mShareHelper == null && autoCreate) {
            mShareHelper = LifecyclerShareHelper.create(
                    this,
                    AuthUtil.newAuthListener(new AuthUtil.SimpleAuthListener()),
                    ShareUtil.newShareListener(new ShareUtil.SimpleShareListener()));
        }
        return mShareHelper;
    }

}

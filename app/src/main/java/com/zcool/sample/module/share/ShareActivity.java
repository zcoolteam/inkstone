package com.zcool.sample.module.share;

import android.os.Bundle;

import com.zcool.inkstone.ext.hierarchy.HierarchyDelegateHelper;
import com.zcool.inkstone.ext.share.process.ProcessShareHelper;
import com.zcool.inkstone.lang.SystemUiHelper;

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
        return new ProcessShareHelper.SampleAuthResultReceiver();
    }

}

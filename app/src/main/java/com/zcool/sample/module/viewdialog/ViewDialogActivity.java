package com.zcool.sample.module.viewdialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zcool.inkstone.ext.hierarchy.HierarchyDelegateHelper;
import com.zcool.inkstone.lang.SystemUiHelper;

public class ViewDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemUiHelper.from(getWindow())
                .layoutStatusBar()
                .layoutStable()
                .apply();

        HierarchyDelegateHelper.showContentFragment(
                this,
                ViewDialogFragment.class.getName(),
                null);
    }

}

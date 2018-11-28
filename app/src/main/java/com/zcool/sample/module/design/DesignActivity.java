package com.zcool.sample.module.design;

import android.os.Bundle;

import com.zcool.inkstone.ext.hierarchy.HierarchyDelegateHelper;
import com.zcool.inkstone.lang.SystemUiHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DesignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemUiHelper.from(getWindow())
                .layoutStatusBar()
                .layoutStable()
                .apply();

        HierarchyDelegateHelper.showContentFragment(
                this,
                DesignFragment.class.getName(),
                null);
    }

}

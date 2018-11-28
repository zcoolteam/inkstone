package com.zcool.sample.module.main;

import android.os.Bundle;

import com.zcool.inkstone.ext.hierarchy.HierarchyDelegateHelper;
import com.zcool.inkstone.lang.SystemUiHelper;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemUiHelper.from(getWindow())
                .layoutStatusBar()
                .layoutStable()
                .setStatusBarTextColorBlack()
                .apply();

        HierarchyDelegateHelper.showContentFragment(
                this,
                MainFragment.class.getName(),
                null);
    }

}

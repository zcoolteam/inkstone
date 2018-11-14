package com.zcool.sample.module.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zcool.inkstone.ext.hierarchy.HierarchyDelegateHelper;
import com.zcool.inkstone.lang.SystemUiHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemUiHelper.from(getWindow())
                .layoutStatusBar()
                .layoutStable()
                .apply();

        HierarchyDelegateHelper.showContentFragment(
                this,
                MainFragment.class.getName(),
                null);
    }

}

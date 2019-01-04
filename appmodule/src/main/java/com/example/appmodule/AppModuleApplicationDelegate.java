package com.example.appmodule;

import android.content.Context;
import android.view.View;

import com.zcool.inkstone.SubApplicationDelegate;
import com.zcool.inkstone.annotation.ApplicationDelegate;

import androidx.annotation.Keep;
import butterknife.BindView;

@Keep
@ApplicationDelegate(priority = 1)
public class AppModuleApplicationDelegate implements SubApplicationDelegate {

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public void onStartBackgroundService() {

    }

    @BindView(R2.id.title)
    View mView;

}

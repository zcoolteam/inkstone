package com.zcool.inkstone.ext.share.process;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class ProcessShareFragment extends Fragment {

    private static final String FRAGMENT_TAG = "inkstone.ProcessShareFragment";

    public static ProcessShareFragment newInstance() {
        Bundle args = new Bundle();
        ProcessShareFragment fragment = new ProcessShareFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ProcessShareFragment getOrCreate(FragmentActivity fragmentActivity) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        ProcessShareFragment fragment = (ProcessShareFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = ProcessShareFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commitNow();
        }

        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ProcessShareHelper.onActivityResult(this, requestCode, resultCode, data);
    }

}

package com.zcool.sample.module.viewdialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zcool.inkstone.ext.backstack.dialog.ViewDialog;
import com.zcool.inkstone.util.ViewUtil;
import com.zcool.sample.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

public class ViewDialogFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_view_dialog, container, false);
    }

    private Unbinder mUnbinder;

    @BindView(R.id.app_main_content)
    ViewGroup mAppMainContent;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnbinder = ButterKnife.bind(this, view);
    }

    @OnClick(R.id.item_view_dialog_1)
    void showViewDialog1() {
        Activity activity = getActivity();
        if (activity == null) {
            Timber.e("activity is null");
            return;
        }

        new ViewDialog.Builder(activity)
                .setParentView(mAppMainContent)
                .setContentView(R.layout.sample_view_dialog_1_content)
                .defaultAnimator()
                .create()
                .show();
    }

    @OnClick(R.id.item_view_dialog_2)
    void showViewDialog2() {
        Activity activity = getActivity();
        if (activity == null) {
            Timber.e("activity is null");
            return;
        }

        ViewDialog dialog = new ViewDialog.Builder(activity)
                .setParentView(mAppMainContent)
                .setCancelable(false)
                .setContentView(R.layout.sample_view_dialog_2_content)
                .defaultAnimator()
                .create();

        ViewUtil.onClick(dialog.findViewById(R.id.action_ok), v -> dialog.hide(false));

        dialog.show();
    }

    @OnClick(R.id.item_view_dialog_3)
    void showViewDialog3() {
        Activity activity = getActivity();
        if (activity == null) {
            Timber.e("activity is null");
            return;
        }

        ViewDialog dialog = new ViewDialog.Builder(activity)
                .setParentView(mAppMainContent)
                .setCancelable(true)
                .dimBackground(false)
                .setContentView(R.layout.sample_view_dialog_3_content)
                .setContentViewShowAnimator(R.animator.inkstone_dialog_slide_in_from_bottom)
                .setContentViewHideAnimator(R.animator.inkstone_dialog_slide_out_to_bottom)
                .create();

        dialog.show();
    }

    @OnClick(R.id.item_view_dialog_4)
    void showViewDialog4() {
        Activity activity = getActivity();
        if (activity == null) {
            Timber.e("activity is null");
            return;
        }

        ViewDialog dialog = new ViewDialog.Builder(activity)
                .setParentView(mAppMainContent)
                .dimBackground(false)
                .setContentView(R.layout.sample_view_dialog_4_content)
                .setContentViewShowAnimator(R.animator.inkstone_dialog_slide_in_from_right)
                .setContentViewHideAnimator(R.animator.inkstone_dialog_slide_out_to_right)
                .create();

        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

}

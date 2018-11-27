package com.zcool.sample.module.design;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zcool.inkstone.util.DimenUtil;
import com.zcool.sample.R;
import com.zcool.sample.module.design.widget.ProgressViewFrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class DesignFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_design, container, false);
    }

    private Unbinder mUnbinder;

    @BindView(R.id.app_main_content)
    ViewGroup mAppMainContent;

    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.progress_view_title)
    ProgressViewFrameLayout mProgressViewTitle;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnbinder = ButterKnife.bind(this, view);

        Activity activity = getActivity();
        if (activity != null) {
            mProgressViewTitle.setSystemUiWindow(activity.getWindow());
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new DataAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

    private class DataAdapter extends RecyclerView.Adapter<DataViewHolder> {

        private List<String> mItems = new ArrayList<>();

        {
            for (int i = 0; i < 100; i++) {
                mItems.add("item#" + i);
            }
        }

        @NonNull
        @Override
        public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            Timber.v("onCreateViewHolder position#%s", position);

            TextView textView = new AppCompatTextView(viewGroup.getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);

            final int padding = DimenUtil.dp2px(10);
            textView.setPadding(padding, padding, padding, padding);

            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.GRAY);
            textView.setTextSize(16);

            return new DataViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull DataViewHolder dataViewHolder, int position) {
            Timber.v("onBindViewHolder position#%s", position);

            dataViewHolder.onBind(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    private class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView mItemText;

        public DataViewHolder(@NonNull TextView itemView) {
            super(itemView);
            mItemText = itemView;
        }

        public void onBind(final String text) {
            mItemText.setText(text);
        }

    }

}

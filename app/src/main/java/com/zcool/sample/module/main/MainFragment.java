package com.zcool.sample.module.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zcool.inkstone.util.ViewUtil;
import com.zcool.sample.R;
import com.zcool.sample.module.imagedownload.ImageDownloadActivity;
import com.zcool.sample.module.third.ThirdActivity;
import com.zcool.sample.module.viewdialog.ViewDialogActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_main, container, false);
    }

    private Unbinder mUnbinder;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

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

        private List<Pair<String, String>> mItems = new ArrayList<>();

        {
            mItems.add(Pair.create("图片下载", ImageDownloadActivity.class.getName()));
            mItems.add(Pair.create("第三方账号登录", ThirdActivity.class.getName()));
            mItems.add(Pair.create("ViewDialog", ViewDialogActivity.class.getName()));
        }

        @NonNull
        @Override
        public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            LayoutInflater inflater = getLayoutInflater();
            View itemView = inflater.inflate(R.layout.sample_main_view_item, viewGroup, false);
            return new DataViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DataViewHolder dataViewHolder, int position) {
            Pair<String, String> item = mItems.get(position);
            dataViewHolder.onBind(item.first, item.second);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_text)
        TextView mItemText;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(final String text, final String clazz) {
            mItemText.setText(text);
            ViewUtil.onClick(itemView, v -> {
                Activity activity = getActivity();
                if (activity == null) {
                    Timber.e("activity is null");
                    return;
                }
                Intent intent = new Intent();
                intent.setClassName(activity, clazz);
                startActivity(intent);
            });
        }

    }

}

package com.zcool.sample.module.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zcool.inkstone.util.ViewUtil;
import com.zcool.sample.R;
import com.zcool.sample.module.design.DesignActivity;
import com.zcool.sample.module.imagedownload.ImageDownloadActivity;
import com.zcool.sample.module.share.ShareActivity;
import com.zcool.sample.module.viewdialog.ViewDialogActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
            mItems.add(Pair.create("第三方账号登录", ShareActivity.class.getName()));
            mItems.add(Pair.create("ViewDialog", ViewDialogActivity.class.getName()));
            mItems.add(Pair.create("Design", DesignActivity.class.getName()));

            for (int i = 0; i < 100; i++) {
                mItems.add(Pair.create("item#" + i, null));
            }
        }

        @NonNull
        @Override
        public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            Timber.v("onCreateViewHolder position#%s", position);

            LayoutInflater inflater = getLayoutInflater();
            View itemView = inflater.inflate(R.layout.sample_main_view_item, viewGroup, false);
            return new DataViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DataViewHolder dataViewHolder, int position) {
            Timber.v("onBindViewHolder position#%s", position);

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

                if (TextUtils.isEmpty(clazz)) {
                    Timber.e("clazz is empty");
                    return;
                }

                Intent intent = new Intent();
                intent.setClassName(activity, clazz);
                startActivity(intent);
            });
        }

    }

}

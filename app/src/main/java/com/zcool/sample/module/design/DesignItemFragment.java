package com.zcool.sample.module.design;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zcool.inkstone.lang.DisposableHolder;
import com.zcool.inkstone.thread.Threads;
import com.zcool.inkstone.util.DimenUtil;
import com.zcool.sample.R;
import com.zcool.sample.widget.refreshlayout.RefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DesignItemFragment extends Fragment {

    private static final String EXTRA_TITLE = "title";

    public static DesignItemFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        DesignItemFragment fragment = new DesignItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_design_item, container, false);
    }

    private Unbinder mUnbinder;

    private final DisposableHolder mRequestHolder = new DisposableHolder();

    @BindView(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new DataAdapter());

        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRequestHolder.set(Single.fromCallable(
                        new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                Threads.sleepQuietly(3000L);
                                return new Object();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                mRefreshLayout.setRefreshing(false);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                e.printStackTrace();
                                mRefreshLayout.setRefreshing(false);
                            }
                        }));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }

        mRequestHolder.clear();
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

package com.zcool.inkstone.lang;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zcool.inkstone.util.AssetUtil;

import java.lang.reflect.Type;

public class AssetsCacheDataHelper<T> extends CacheDataHelper<T> {

    private final String mAssetsPath;

    private T mData;

    public AssetsCacheDataHelper(String cacheKey, String assetsPath, Type type) {
        super(cacheKey, type);
        mAssetsPath = assetsPath;

        mData = super.getData();
        if (mData == null) {
            mData = readAssetsData(type);
        }
    }

    public T getData() {
        return mData;
    }

    private T readAssetsData(Type type) {
        try {
            String json = AssetUtil.readAllAsString(mAssetsPath, null, null);
            if (TextUtils.isEmpty(json)) {
                return null;
            }
            return new Gson().fromJson(json, type);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setData(@Nullable T data) {
        mData = data;
        super.setData(data);
    }

}

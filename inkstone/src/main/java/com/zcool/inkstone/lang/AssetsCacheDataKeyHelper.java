package com.zcool.inkstone.lang;

import java.lang.reflect.Type;
import java.util.Map;

import androidx.annotation.Nullable;

public abstract class AssetsCacheDataKeyHelper<T, K, V> extends AssetsCacheDataHelper<T> {

    private Map<K, V> mQueryMap;

    public AssetsCacheDataKeyHelper(String cacheKey, String assetsPath, Type type) {
        super(cacheKey, assetsPath, type);
        mQueryMap = transform(getData());
    }

    public abstract Map<K, V> transform(T data);

    public Map<K, V> getQueryMap() {
        return mQueryMap;
    }

    public V query(K key, V defaultValue) {
        Map<K, V> queryMap = mQueryMap;
        if (queryMap == null) {
            return defaultValue;
        }

        V value = queryMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public void setData(@Nullable T data) {
        super.setData(data);
        mQueryMap = transform(data);
    }
}

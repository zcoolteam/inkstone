package com.zcool.inkstone.service;

class StorageService extends IStorageService.Stub {
    @Override
    public void set(String namespace, String key, String value) {
        StorageServiceProvider.getInstance().set(namespace, key, value);
    }

    @Override
    public String get(String namespace, String key) {
        return StorageServiceProvider.getInstance().get(namespace, key);
    }

    @Override
    public String getOrSetLock(String namespace, String key, String setValue) {
        return StorageServiceProvider.getInstance().getOrSetLock(namespace, key, setValue);
    }

    @Override
    public void printAllRows(String namespace) {
        StorageServiceProvider.getInstance().printAllRows(namespace);
    }
}

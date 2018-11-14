package com.zcool.inkstone.service;

interface IStorageService {

    void set(in String namespace, in String key, in String value);
    String get(in String namespace, in String key);
    String getOrSetLock(in String namespace, in String key, String setValue);
    void printAllRows(in String namespace);

}

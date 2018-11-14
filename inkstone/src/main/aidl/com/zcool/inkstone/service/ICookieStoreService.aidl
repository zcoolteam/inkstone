package com.zcool.inkstone.service;

interface ICookieStoreService {

    void save(in String url, in List<String> setCookies);
    List<String> matches(in String url);
    List<String> get(in String url);
    List<String> getUrls();
    void clear();
    void clearSession();
    void printAll();

}

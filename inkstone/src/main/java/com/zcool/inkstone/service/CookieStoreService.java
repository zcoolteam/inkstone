package com.zcool.inkstone.service;

import java.util.List;

class CookieStoreService extends ICookieStoreService.Stub {

    @Override
    public void save(String url, List<String> setCookies) {
        CookieStoreServiceProvider.getInstance().save(url, setCookies);
    }

    @Override
    public List<String> matches(String url) {
        return CookieStoreServiceProvider.getInstance().matches(url);
    }

    @Override
    public List<String> get(String url) {
        return CookieStoreServiceProvider.getInstance().get(url);
    }

    @Override
    public List<String> getUrls() {
        return CookieStoreServiceProvider.getInstance().getUrls();
    }

    @Override
    public void clear() {
        CookieStoreServiceProvider.getInstance().clear();
    }

    @Override
    public void clearSession() {
        CookieStoreServiceProvider.getInstance().clearSession();
    }

    @Override
    public void printAll() {
        CookieStoreServiceProvider.getInstance().printAll();
    }

}

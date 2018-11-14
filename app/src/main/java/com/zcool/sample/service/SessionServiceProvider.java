package com.zcool.sample.service;

import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.zcool.inkstone.lang.CacheDataHelper;
import com.zcool.inkstone.lang.Singleton;
import com.zcool.sample.entity.Session;

class SessionServiceProvider {

    private static final Singleton<SessionServiceProvider> INSTANCE = new Singleton<SessionServiceProvider>() {
        @Override
        protected SessionServiceProvider create() {
            return new SessionServiceProvider();
        }
    };

    public static SessionServiceProvider getInstance() {
        return INSTANCE.get();
    }

    private static final String DATA_KEY = "session";
    private final CacheDataHelper<Session> mDataHelper = new CacheDataHelper<>(DATA_KEY, new TypeToken<Session>() {
    }.getType());

    private SessionServiceProvider() {
    }

    @Nullable
    public Session getSession() {
        return mDataHelper.getData();
    }

    public void setSession(@Nullable Session session) {
        mDataHelper.setData(session);
    }

    @Nullable
    public String getToken() {
        Session session = getSession();
        if (session != null) {
            return session.token;
        }
        return null;
    }

}

package com.zcool.sample.manager;

import android.os.RemoteException;

import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.manager.ServiceManager;
import com.zcool.sample.entity.Session;
import com.zcool.sample.service.ISessionService;
import com.zcool.sample.service.MainServicesProvider;

import androidx.annotation.Nullable;
import timber.log.Timber;

public class SessionManager {

    private static final Singleton<SessionManager> INSTANCE = new Singleton<SessionManager>() {
        @Override
        protected SessionManager create() {
            return new SessionManager();
        }
    };

    public static SessionManager getInstance() {
        return INSTANCE.get();
    }

    private SessionManager() {
    }

    @Nullable
    public String getToken() {
        try {
            return getService().getToken();
        } catch (Throwable e) {
            e.printStackTrace();
            Timber.e(e);
        }
        return null;
    }

    @Nullable
    public Session getSession() {
        try {
            return getService().getSession();
        } catch (Throwable e) {
            e.printStackTrace();
            Timber.e(e);
        }
        return null;
    }

    public void setSession(Session session) {
        try {
            getService().setSession(session);
        } catch (Throwable e) {
            e.printStackTrace();
            Timber.e(e);
        }
    }

    private ISessionService getService() throws RemoteException {
        return ISessionService.Stub.asInterface(
                ServiceManager.getInstance().fetchService(MainServicesProvider.SERVICE_SESSION));
    }

}

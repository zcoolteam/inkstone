package com.zcool.sample.service;

import android.os.RemoteException;

import com.zcool.sample.entity.Session;

class SessionService extends ISessionService.Stub {

    @Override
    public Session getSession() throws RemoteException {
        return SessionServiceProvider.getInstance().getSession();
    }

    @Override
    public void setSession(Session session) throws RemoteException {
        SessionServiceProvider.getInstance().setSession(session);
    }

    @Override
    public String getToken() throws RemoteException {
        return SessionServiceProvider.getInstance().getToken();
    }

}

package com.zcool.sample.service;

import com.zcool.sample.entity.Session;

interface ISessionService {

    Session getSession();
    void setSession(in Session session);
    String getToken();

}

# 支持多进程开发的 Android App 基础框架
- [配置](#配置)
   - [基本配置](#基本配置)
      - [引入依赖](#引入依赖)
      - [配置 ApplicationDelegate](#配置-applicationdelegate)
      - [配置 ServicesProvider](#配置-servicesprovider)
   - [其它配置](#其它配置)
      - [处理自定义 Application](#处理自定义-application)
      - [处理自定义 Service](#处理自定义-service)
      - [处理自定义 ContentProvider](#处理自定义-contentprovider)
      - [处理自定义 BroadcastReceiver](#处理自定义-broadcastreceiver)
- [功能使用及扩展](#功能使用及扩展)
   - [自定义跨进程服务示例---实现登录信息的跨进程服务 SessionManager](#自定义跨进程服务示例---实现登录信息的跨进程服务-sessionmanager)
      - [定义实体类 Session](#定义实体类-session)
      - [定义实体类 User](#定义实体类-user)
      - [定义 AIDL Session](#定义-aidl-session)
      - [定义 AIDL User](#定义-aidl-user)
      - [定义 AIDL ISessionService](#定义-aidl-isessionservice)
      - [定义业务核心类 SessionServiceProvider](#定义业务核心类-sessionserviceprovider)
      - [定义业务类 SessionService](#定义业务类-sessionservice)
      - [在 ServicesProvider 中注册 SessionService](#在-servicesprovider-中注册-sessionservice)
      - [定义对外的服务类 SessionManager](#定义对外的服务类-sessionmanager)

## 配置

### 基本配置

##### 引入依赖

```
implementation "com.zcool.inkstone:inkstone:0.1.5"
annotationProcessor "com.zcool.inkstone:inkstone-processor:0.1.5"
```

##### 配置 ApplicationDelegate

> ApplicationDelegate 是 App 启动的入口，相当于 Application 的功能。
>
> **为了提高兼容性，如果你有自定义的 Application, ContentProvider, Service, BroadcastReceiver,
> 需要在其对应的入口处调用 `Inkstone.init(Context)` 方法.**

```
package com.zcool.sample;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Keep;

import com.squareup.leakcanary.LeakCanary;
import com.zcool.inkstone.BaseApplicationDelegate;
import com.zcool.inkstone.annotation.ApplicationDelegate;
import com.zcool.inkstone.ext.share.ShareConfig;

@Keep
@ApplicationDelegate
public class MainApplicationDelegate extends BaseApplicationDelegate {
    // 注解 @ApplicationDelegate 必不可少，全局有且只有一个类标记此注解

    @Override
    public void onCreate(Context context) {
        // App 启动入口, 在此处配置自定义初始化内容

        // 示例：配置 LeakCanary
        if (!LeakCanary.isInAnalyzerProcess(context)) {
            LeakCanary.install((Application) context.getApplicationContext());
        }

        super.onCreate(context);

        // 示例：配置分享参数
        new ShareConfig.Builder()
                .setQQ(BuildConfig.QQ_APP_ID)
                .setWeixin(BuildConfig.WX_APP_KEY, BuildConfig.WX_APP_SECRET)
                .setWeibo(BuildConfig.WEIBO_APP_KEY)
                .setWeiboRedirectUrl(BuildConfig.WEIBO_REDIRECT_URL)
                .init();
    }

}
```

##### 配置 ServicesProvider

> ServicesProvider 是提供跨进程服务的关键，已经内置实现了一些跨进程服务，
> 如 StorageManager, CookieStoreManager 等. 你也可以实现自定义的跨进程服务，
> 如登录信息 SessionManager, 关于如何实现自定义跨进程服务，参看进阶部分。

```
package com.zcool.sample.service;

import android.os.IBinder;
import android.support.annotation.Keep;

import com.zcool.inkstone.annotation.ServicesProvider;
import com.zcool.inkstone.service.BaseServicesProvider;

@Keep
@ServicesProvider
public class MainServicesProvider extends BaseServicesProvider {
    // 注解 @ServicesProvider 必不可少，全局有且只有一个类标记此注解

    public static final String SERVICE_SESSION = "session";

    @Override
    protected void onCreate() {
        super.onCreate();

        // 添加自定义服务 SessionManager
        addService(SERVICE_SESSION, new StaticServiceFetcher<IBinder>() {
            @Override
            public IBinder createService() {
                return new SessionService();
            }
        });
    }

}
```

### 其它配置

##### 处理自定义 Application

> 通常，自定义 Application 是不必要的，你可以将自定义 Application 中的初始化内容移动到 ApplicationDelegate 中

```
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // 如果有自定义 Application, 需要在此处初始化 Inkstone
        Inkstone.init(this);
    }

}
```

##### 处理自定义 Service

```
public class MyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        // 如果有自定义 Service, 需要在此处初始化 Inkstone
        Inkstone.init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
```

##### 处理自定义 ContentProvider

```
public class MyContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        // 如果有自定义 ContentProvider, 需要在此处初始化 Inkstone
        Inkstone.init(getContext());
        return true;
    }

}
```

##### 处理自定义 BroadcastReceiver

```
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果有自定义 BroadcastReceiver, 需要在此处初始化 Inkstone
        Inkstone.init(context);
    }

}
```

## 功能使用及扩展

### 自定义跨进程服务示例---实现登录信息的跨进程服务 SessionManager

> 跨进程服务框架的核心是基于 AIDL, 因此需要你对 AIDL 有一定的了解。

##### 定义实体类 Session

> 实体类 Session.java, 用于 AIDL 通信，实现 Parcelable 接口

```
package com.zcool.sample.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable {

    public long lastModify;
    public String token;
    public User user;

    public Session() {
    }

    protected Session(Parcel in) {
        lastModify = in.readLong();
        token = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(lastModify);
        dest.writeString(token);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

}
```

##### 定义实体类 User

> 实体类 User.java, 用于 AIDL 通信，实现 Parcelable 接口

```
package com.zcool.sample.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    public int id;
    public String name;

    public User() {
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
```

##### 定义 AIDL Session

> 申明 Session.aidl, 对应 Session.java

```
package com.zcool.sample.entity;

parcelable Session;
```

##### 定义 AIDL User

> 申明 User.aidl, 对应 User.java

```
package com.zcool.sample.entity;

parcelable User;
```

##### 定义 AIDL ISessionService

> ISessionService.aidl 是跨进程业务接口定义，后续会分别实现此接口的服务端和客户端代码。
> 其中服务端为实现为 SessionService extends ISessionService.Stub.
> 客户端实现为 ISessionService.Stub.asInterface(binder)。

```
package com.zcool.sample.service;

import com.zcool.sample.entity.Session;

interface ISessionService {

    Session getSession();
    void setSession(in Session session);
    String getToken();

}
```

##### 定义业务核心类 SessionServiceProvider

> 业务类 SessionServiceProvider.java 是服务端代码，实现了登录信息的核心业务逻辑，
> 仅供 ISessionService.Stub 的实现类 SessionService 调用。
> 此处将 SessionServiceProvider 类的访问权限定义为 default

```
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
```

##### 定义业务类 SessionService

> 业务类 SessionService.java 是 ISessionService 的服务端直接实现，继承自 ISessionService.Stub,
> 并且将对应的业务方法实现转为调用单例类 SessionServiceProvider 上的业务方法

```
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
```

##### 在 ServicesProvider 中注册 SessionService

> ServicesProvider 在此处的实现类是 MainServicesProvider.java, 继承自 BaseServicesProvider,
> 申明了注解 @ServicesProvider. 在 onCreate 方法中添加 SessionService 服务.
> 需要注意 addService 的 key 不要与已存在的服务 key 冲突.
> 客户端在使用此服务时通过 ServiceManager.getInstance().fetchRemote().getService(key)
> 获取此处的服务对象, 注意要通过 ISessionService.Stub.asInterface 进行转换

```
package com.zcool.sample.service;

import android.os.IBinder;
import android.support.annotation.Keep;

import com.zcool.inkstone.annotation.ServicesProvider;
import com.zcool.inkstone.service.BaseServicesProvider;

@Keep
@ServicesProvider
public class MainServicesProvider extends BaseServicesProvider {
    // 注解 @ServicesProvider 必不可少，全局有且只有一个类标记此注解

    public static final String SERVICE_SESSION = "session";

    @Override
    protected void onCreate() {
        super.onCreate();

        // 添加自定义服务 SessionManager
        addService(SERVICE_SESSION, new StaticServiceFetcher<IBinder>() {
            @Override
            public IBinder createService() {
                return new SessionService();
            }
        });
    }

}
```

##### 定义对外的服务类 SessionManager

> 类 SessionManager.java 是最终对外提供服务的客户端类，其他调用登录信息相关的业务时，都是通过
> SessionManager 实现，这是一个 public 的单例类. 它在背后调用的是远程服务 MainServicesProvider.SERVICE_SESSION,
> 该服务是在 ServicesProvider 的实现类 MainServicesProvider 中注册.
> SessionManager 支持在任意进程中调用，它们最终都调用到了核心业务实现类 SessionServiceProvider 中对应的方法.

```
package com.zcool.sample.manager;

import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.manager.ServiceManager;
import com.zcool.sample.entity.Session;
import com.zcool.sample.service.ISessionService;
import com.zcool.sample.service.MainServicesProvider;

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
```

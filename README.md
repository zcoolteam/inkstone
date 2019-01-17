inkstone
========

Android 应用开发中间件, 支持多进程与模块化

- [版本要求](#版本要求)
- [如何使用](#如何使用)
   - [必须配置](#必须配置)
   - [按需配置](#按需配置)
- [提高兼容性][#提高兼容性]

版本要求
--------

支持 Android 4.1+(API 16+)

如何使用
--------

### 必须配置
/build.gradle
```groovy
buildscript {
    dependencies {
        classpath 'com.zcool.inkstone:inkstone-gradle-plugin:0.1.110'
    }
}
```

/app/build.gradle
```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.zcool.inkstone'
```

### 按需配置
/app/build.gradle
```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.zcool.inkstone'

dependencies {
    implementation "com.zcool.inkstone:inkstone:0.1.110"
}
```

/appmodule/build.gradle
```groovy
apply plugin: 'com.android.library'

dependencies {
    implementation "com.zcool.inkstone:inkstone:0.1.110"
}
```

当在 application module 中增加了自定义的 application delegate 或者 services provider 时：

*注意：下面配置示例中的 com.zcool.sample 是指该 application module 下 AndroidManifest.xml 中 package 的值, 与 applicationId 无关*

/app/build.gradle
```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.zcool.inkstone'

android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ['MODULE_MANIFEST_PACKAGE': "com.zcool.sample"]
            }
        }
    }
}

dependencies {
    implementation "com.zcool.inkstone:inkstone:0.1.110"
    annotationProcessor "com.zcool.inkstone:inkstone-processor:0.1.110"
}
```
/app/src/main/AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.zcool.sample">
    <application>
        <activity
            android:name="com.zcool.inkstone.app.InkstoneConfigActivity">
            <intent-filter>
                <action android:name="MODULE_MANIFEST_PACKAGE:com.zcool.sample" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

当在 library module 中增加了自定义的 application delegate 或者 services provider 时：

*注意：下面配置示例中的 com.example.appmodule 是指该 library module 下 AndroidManifest.xml 中 package 的值*

/appmodule/build.gradle
```groovy
apply plugin: 'com.android.library'

android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ['MODULE_MANIFEST_PACKAGE': "com.example.appmodule"]
            }
        }
    }
}

dependencies {
    implementation "com.zcool.inkstone:inkstone:0.1.110"
    annotationProcessor "com.zcool.inkstone:inkstone-processor:0.1.110"
}
```
/appmodule/src/main/AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.appmodule">
    <application>
        <activity
            android:name="com.zcool.inkstone.app.InkstoneConfigActivity">
            <intent-filter>
                <action android:name="MODULE_MANIFEST_PACKAGE:com.example.appmodule" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

提高兼容性
-----
为了提高兼容性，如果你有自定义的 Application, ContentProvider, Service, BroadcastReceiver,
需要在其对应的入口处调用 `Inkstone.init(Context)` 方法.**
```java
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Inkstone.init(this);
    }

}
```
```java
public class MyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Inkstone.init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
```
```java
public class MyContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        Inkstone.init(getContext());
        return true;
    }

}
```
```java
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Inkstone.init(context);
    }

}
```

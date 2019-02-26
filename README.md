inkstone
=========

Android 应用多进程开发与模块化开发中间件

- [版本要求](#版本要求)
- [如何使用](#如何使用)
   - [必须配置](#必须配置)
   - [按需配置](#按需配置)

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
        classpath 'com.zcool.inkstone:inkstone-gradle-plugin:0.1.137'
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
    implementation "com.zcool.inkstone:inkstone:0.1.137"
}
```

/appmodule/build.gradle
```groovy
apply plugin: 'com.android.library'

dependencies {
    implementation "com.zcool.inkstone:inkstone:0.1.137"
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
    implementation "com.zcool.inkstone:inkstone:0.1.137"
    annotationProcessor "com.zcool.inkstone:inkstone-processor:0.1.137"
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
    implementation "com.zcool.inkstone:inkstone:0.1.137"
    annotationProcessor "com.zcool.inkstone:inkstone-processor:0.1.137"
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


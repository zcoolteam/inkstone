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

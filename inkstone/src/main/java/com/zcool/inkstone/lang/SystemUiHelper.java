package com.zcool.inkstone.lang;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import timber.log.Timber;

public class SystemUiHelper {

    private Window mWindow;
    private int mSystemUiVisibility;
    private boolean mLightStatusBar;
    private boolean mLightNavigationBar;

    private SystemUiHelper() {
    }

    public SystemUiHelper layoutStatusBar() {
        mSystemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        return this;
    }

    public SystemUiHelper hideStatusBar() {
        mSystemUiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        return this;
    }

    public SystemUiHelper immersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSystemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        return this;
    }

    public SystemUiHelper immersiveSticky() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSystemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return this;
    }

    public SystemUiHelper layoutNavigationBar() {
        mSystemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        return this;
    }

    public SystemUiHelper layoutStable() {
        mSystemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        return this;
    }

    public SystemUiHelper hideNavigationBar() {
        mSystemUiVisibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        return this;
    }

    public SystemUiHelper setLightStatusBar() {
        return setLightStatusBar(true);
    }

    public SystemUiHelper setLightStatusBar(boolean lightStatusBar) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (lightStatusBar) {
                mSystemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                mSystemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }

        mLightStatusBar = lightStatusBar;
        return this;
    }

    public SystemUiHelper setLightNavigationBar() {
        return setLightNavigationBar(true);
    }

    public SystemUiHelper setLightNavigationBar(boolean lightNavigationBar) {
        if (Build.VERSION.SDK_INT >= 26) {
            if (lightNavigationBar) {
                mSystemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                mSystemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }

        mLightNavigationBar = lightNavigationBar;
        return this;
    }

    public void apply() {
        // [4.4, 6.0) 之间的部分第三方 rom 支持设置 status bar 字体颜色
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 23) {
            if (MIUICompat.setStatusBarStyle(mWindow, mLightStatusBar)) {
                Timber.v("success set miui status bar black text color %s", mLightStatusBar);
            } else if (FlymeCompat.setStatusBarStyle(mWindow, mLightStatusBar)) {
                Timber.v("success set flyme status bar black text color %s", mLightStatusBar);
            }
        }

        mWindow.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {

            }
        });
        mWindow.getDecorView().setSystemUiVisibility(mSystemUiVisibility);
    }

    public static SystemUiHelper from(Window window) {
        SystemUiHelper systemUiHelper = new SystemUiHelper();
        systemUiHelper.mWindow = window;
        return systemUiHelper;
    }

    // 魅族
    private static class FlymeCompat {

        private static boolean setStatusBarStyle(Window window, boolean lightStatusBar) {
            boolean result = false;
            try {
                Class clazz = WindowManager.LayoutParams.class;
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = clazz.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = clazz.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (lightStatusBar) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Throwable e) {
                // ignore
            }
            return result;
        }

    }

    // 小米
    private static class MIUICompat {

        private static boolean setStatusBarStyle(Window window, boolean lightStatusBar) {
            boolean result = false;
            try {
                Class clazz = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field darkFlagField = clazz.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkFlagField.setAccessible(true);
                Method extraFlagField = window.getClass().getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.setAccessible(true);

                int darkModeFlag = darkFlagField.getInt(null);
                if (lightStatusBar) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag); // 状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag); // 清除黑色字体
                }
                result = true;
            } catch (Throwable e) {
                // ignore
            }
            return result;
        }

    }

}

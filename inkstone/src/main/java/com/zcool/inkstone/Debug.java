package com.zcool.inkstone;

public class Debug {

    public static boolean isDebug() {
        return BaseApplicationDelegate.getInstance().isDebug();
    }

    public static boolean isDebugHttpBody() {
        return BaseApplicationDelegate.getInstance().isDebugHttpBody();
    }

    public static boolean isDebugWidget() {
        return BaseApplicationDelegate.getInstance().isDebugWidget();
    }

}

package com.zcool.inkstone;

public class Debug {

    public static boolean isDebug() {
        return ApplicationDelegateRoot.getInstance().isDebug();
    }

    public static boolean isDebugHttpBody() {
        return ApplicationDelegateRoot.getInstance().isDebugHttpBody();
    }

    public static boolean isDebugWidget() {
        return ApplicationDelegateRoot.getInstance().isDebugWidget();
    }

}

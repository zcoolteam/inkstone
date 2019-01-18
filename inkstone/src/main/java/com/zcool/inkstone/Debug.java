package com.zcool.inkstone;

public class Debug {

    private Debug() {
    }

    public static boolean isDebug() {
        return Inkstone.isDebug();
    }

    public static boolean isDebugHttpBody() {
        return Inkstone.isDebugHttpBody();
    }

    public static boolean isDebugWidget() {
        return Inkstone.isDebugWidget();
    }

}

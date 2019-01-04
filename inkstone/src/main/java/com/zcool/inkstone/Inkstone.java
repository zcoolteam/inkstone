package com.zcool.inkstone;

import android.content.Context;

public class Inkstone {

    private Inkstone() {
    }

    public static void init(Context context) {
        ApplicationDelegateRoot.init(context);
    }

}

package com.zcool.inkstone.lang;

import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.zcool.inkstone.Inkstone;
import com.zcool.inkstone.util.ContextUtil;

import java.io.File;

public class CommonFileProvider extends FileProvider {

    @Override
    public boolean onCreate() {
        super.onCreate();
        Inkstone.init(getContext());
        return true;
    }

    public static Uri getUriForFile(File file) {
        return FileProvider.getUriForFile(ContextUtil.getContext(), getAuthority(), file);
    }

    public static String getAuthority() {
        return "inkstone." + ContextUtil.getContext().getPackageName() + ".CommonFileProvider";
    }

}

package com.zcool.inkstone.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Binder;
import android.text.TextUtils;

import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.util.ContextUtil;

import java.util.List;

import timber.log.Timber;

/**
 * 记录进程信息，在 app 中可能存在多个进程，在处理如缓存路径时进程之间的应当不同，否则可能出现读写冲突。
 */
public class ProcessManager {

    private static final Singleton<ProcessManager> sInstance =
            new Singleton<ProcessManager>() {
                @Override
                protected ProcessManager create() {
                    return new ProcessManager();
                }
            };

    private static boolean sInit;

    public static ProcessManager getInstance() {
        ProcessManager instance = sInstance.get();
        sInit = true;
        return instance;
    }

    public static boolean isInit() {
        return sInit;
    }

    private int mProcessId;
    private String mProcessName;
    private String mProcessTag;
    private boolean mMainProcess;

    private ProcessManager() {
        Timber.v("init");
        mProcessId = android.os.Process.myPid();
        mProcessName = fetchProcessName();

        if (TextUtils.isEmpty(mProcessName)) {
            throw new IllegalAccessError("process name not found");
        }

        String processName = mProcessName;
        int index = processName.lastIndexOf(':');
        String processSuffix = null;
        if (index >= 0) {
            if (index == 0 || index == processName.length() - 1) {
                throw new IllegalArgumentException("invalid process name " + processName);
            }
            processSuffix = processName.substring(index + 1);
        }

        mMainProcess = index < 0;
        if (mMainProcess) {
            mProcessTag = "main";
        } else {
            mProcessTag = "sub_" + processSuffix;
        }

        Timber.v("process tag:%s, id:%s, name:%s", mProcessTag, mProcessId, mProcessName);
    }

    private String fetchProcessName() {
        final int pid = android.os.Process.myPid();

        ActivityManager activityManager =
                (ActivityManager)
                        ContextUtil.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos =
                activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }

        String fallback = String.format("%s:invalid_p%s_u%s_cp%s_cu%s_%s",
                ContextUtil.getContext().getPackageName(),
                android.os.Process.myPid(),
                android.os.Process.myUid(),
                Binder.getCallingPid(),
                Binder.getCallingUid(),
                processInfos.size());
        Timber.e("fallback process name %s", fallback);
        return fallback;
    }

    public int getProcessId() {
        return mProcessId;
    }

    /**
     * 获取当前进程名称
     */
    public String getProcessName() {
        return mProcessName;
    }

    /**
     * 获取当前进程的标识，可以用于文件名
     */
    public String getProcessTag() {
        return mProcessTag;
    }

    /**
     * 判断当前进程是否为主进程， 主进程的进程名等于包名
     */
    public boolean isMainProcess() {
        return mMainProcess;
    }
}

package com.zcool.inkstone.ext.share.process;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.zcool.inkstone.thread.Threads;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import timber.log.Timber;

/**
 * 处理在多进程中的第三方分享与登录
 */
public class ProcessShareHelper {

    private static final int REQUEST_CODE_DEFAULT = 1;
    public static final String EXTRA_PROCESS_SHARE_ACTION = "process.share_action";

    public static final String PROCESS_SHARE_ACTION_QQ_SHARE = "QQ_SHARE";
    public static final String PROCESS_SHARE_ACTION_QZONE_SHARE = "QZONE_SHARE";
    public static final String PROCESS_SHARE_ACTION_WEIXIN_SHARE = "WEIXIN_SHARE";
    public static final String PROCESS_SHARE_ACTION_WEIXIN_TIMELINE_SHARE = "WEIXIN_TIMELINE_SHARE";
    public static final String PROCESS_SHARE_ACTION_WEIXIN_MINIPROGRAME_SHARE = "WEIXIN_MINIPROGRAME_SHARE";
    public static final String PROCESS_SHARE_ACTION_WEIBO_SHARE = "WEIBO_SHARE";

    public static final String PROCESS_SHARE_ACTION_QQ_AUTH = "QQ_AUTH";
    public static final String PROCESS_SHARE_ACTION_WEIXIN_AUTH = "WEIXIN_AUTH";
    public static final String PROCESS_SHARE_ACTION_WEIBO_AUTH = "WEIBO_AUTH";

    public static final String EXTRA_PROCESS_SHARE_ACTION_RESULT = "process.share_action.result";

    public static final String PROCESS_SHARE_ACTION_RESULT_SUCCESS = "SUCCESS";
    public static final String PROCESS_SHARE_ACTION_RESULT_FAIL = "FAIL";
    public static final String PROCESS_SHARE_ACTION_RESULT_CANCEL = "CANCEL";

    public static final String EXTRA_PROCESS_SHARE_ACTION_RESULT_DATA = "process.share_action.result.data";

    @Nullable
    public static String getProcessShareAction(Intent intent) {
        return intent.getStringExtra(EXTRA_PROCESS_SHARE_ACTION);
    }

    public static boolean isQQShare(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_QQ_SHARE.equals(processShareAction);
    }

    public static boolean isQzoneShare(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_QZONE_SHARE.equals(processShareAction);
    }

    public static boolean isWeixinShare(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_WEIXIN_SHARE.equals(processShareAction);
    }

    public static boolean isWeixinTimelineShare(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_WEIXIN_TIMELINE_SHARE.equals(processShareAction);
    }

    public static boolean isWeixinMiniprogrameShare(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_WEIXIN_MINIPROGRAME_SHARE.equals(processShareAction);
    }

    public static boolean isWeiboShare(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_WEIBO_SHARE.equals(processShareAction);
    }

    public static boolean isQQAuth(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_QQ_AUTH.equals(processShareAction);
    }

    public static boolean isWeixinAuth(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_WEIXIN_AUTH.equals(processShareAction);
    }

    public static boolean isWeiboAuth(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_WEIBO_AUTH.equals(processShareAction);
    }

    @Nullable
    public static String getProcessShareActionResult(@NonNull Intent intent) {
        return intent.getStringExtra(EXTRA_PROCESS_SHARE_ACTION_RESULT);
    }

    public static boolean isResultSuccess(String result) {
        return PROCESS_SHARE_ACTION_RESULT_SUCCESS.equals(result);
    }

    public static boolean isResultFail(String result) {
        return PROCESS_SHARE_ACTION_RESULT_FAIL.equals(result);
    }

    public static boolean isResultCancel(String result) {
        return PROCESS_SHARE_ACTION_RESULT_CANCEL.equals(result);
    }

    @Nullable
    public static Bundle getProcessShareActionResultData(@NonNull Intent intent) {
        return intent.getBundleExtra(EXTRA_PROCESS_SHARE_ACTION_RESULT_DATA);
    }

    public static void requestQQAuth(Activity activity) {
        if (!Threads.isUi()) {
            Timber.e("must call on ui thread");
            return;
        }

        if (activity == null) {
            Timber.e("activity is null");
            return;
        }

        if (!(activity instanceof FragmentActivity)) {
            Timber.e("activity must type of FragmentActivity %s", activity);
            return;
        }

        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        if (fragmentActivity.getSupportFragmentManager().isStateSaved()) {
            Timber.e("activity already state saved.");
            return;
        }

        ProcessShareFragment fragment = ProcessShareFragment.getOrCreate(fragmentActivity);

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_QQ_AUTH);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    public static void requestWeixinAuth(Activity activity) {
        if (!Threads.isUi()) {
            Timber.e("must call on ui thread");
            return;
        }

        if (activity == null) {
            Timber.e("activity is null");
            return;
        }

        if (!(activity instanceof FragmentActivity)) {
            Timber.e("activity must type of FragmentActivity %s", activity);
            return;
        }

        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        if (fragmentActivity.getSupportFragmentManager().isStateSaved()) {
            Timber.e("activity already state saved.");
            return;
        }

        ProcessShareFragment fragment = ProcessShareFragment.getOrCreate(fragmentActivity);

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIXIN_AUTH);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    public static void requestWeiboAuth(Activity activity) {
        if (!Threads.isUi()) {
            Timber.e("must call on ui thread");
            return;
        }

        if (activity == null) {
            Timber.e("activity is null");
            return;
        }

        if (!(activity instanceof FragmentActivity)) {
            Timber.e("activity must type of FragmentActivity %s", activity);
            return;
        }

        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        if (fragmentActivity.getSupportFragmentManager().isStateSaved()) {
            Timber.e("activity already state saved.");
            return;
        }

        ProcessShareFragment fragment = ProcessShareFragment.getOrCreate(fragmentActivity);

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIBO_AUTH);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    public static void onActivityResult(ProcessShareFragment fragment, int requestCode, int resultCode, Intent data) {
        Timber.v("onActivityResult requestCode:%s, resultCode:%s, data:%s", requestCode, resultCode, data);

        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        FragmentActivity activity = fragment.getActivity();
        if (activity == null) {
            Timber.e("activity is null");
            return;
        }

        if (!(activity instanceof ShareResultReceiverHost)) {
            Timber.e("activity is not type of ShareResultReceiverHost: %s", activity);
            return;
        }

        final ShareResultReceiverHost shareResultReceiverHost = (ShareResultReceiverHost) activity;

        if (data == null) {
            Timber.e("data is null");
            return;
        }

        final String processShareAction = getProcessShareAction(data);
        Timber.v("onActivityResult processShareAction:%s", processShareAction);

        if (isQQAuth(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("qq auth result:%s", result);

            AuthResultReceiver authResultReceiver = shareResultReceiverHost.getAuthResultReceiver();
            if (authResultReceiver == null) {
                Timber.e("auth result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                QQAuthInfo qqAuthInfo = QQAuthInfo.readFromBundle(getProcessShareActionResultData(data));
                if (qqAuthInfo == null) {
                    Timber.e("qq auth info is null");
                    return;
                }

                Timber.v("qq auth success:%s", qqAuthInfo);
                authResultReceiver.onQQAuthSuccess(qqAuthInfo);
                return;
            } else if (isResultFail(result)) {
                Timber.v("qq auth fail");
                authResultReceiver.onQQAuthFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("qq auth cancel");
                authResultReceiver.onQQAuthCancel();
                return;
            } else {
                Timber.e("unknown qq auth result:%s", result);
                return;
            }
        } else if (isWeixinAuth(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("weixin auth result:%s", result);

            AuthResultReceiver authResultReceiver = shareResultReceiverHost.getAuthResultReceiver();
            if (authResultReceiver == null) {
                Timber.e("auth result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                WeixinAuthInfo weixinAuthInfo = WeixinAuthInfo.readFromBundle(getProcessShareActionResultData(data));
                if (weixinAuthInfo == null) {
                    Timber.e("weixin auth info is null");
                    return;
                }

                Timber.v("weixin auth success:%s", weixinAuthInfo);
                authResultReceiver.onWeixinAuthSuccess(weixinAuthInfo);
                return;
            } else if (isResultFail(result)) {
                Timber.v("weixin auth fail");
                authResultReceiver.onWeixinAuthFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("weixin auth cancel");
                authResultReceiver.onWeixinAuthCancel();
                return;
            } else {
                Timber.e("unknown weixin auth result:%s", result);
                return;
            }
        } else if (isWeiboAuth(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("weibo auth result:%s", result);

            AuthResultReceiver authResultReceiver = shareResultReceiverHost.getAuthResultReceiver();
            if (authResultReceiver == null) {
                Timber.e("auth result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                WeiboAuthInfo weiboAuthInfo = WeiboAuthInfo.readFromBundle(getProcessShareActionResultData(data));
                if (weiboAuthInfo == null) {
                    Timber.e("weibo auth info is null");
                    return;
                }

                Timber.v("weibo auth success:%s", weiboAuthInfo);
                authResultReceiver.onWeiboAuthSuccess(weiboAuthInfo);
                return;
            } else if (isResultFail(result)) {
                Timber.v("weibo auth fail");
                authResultReceiver.onWeiboAuthFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("weibo auth cancel");
                authResultReceiver.onWeiboAuthCancel();
                return;
            } else {
                Timber.e("unknown weibo auth result:%s", result);
                return;
            }
        } else if (isQQShare(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("qq share result:%s", result);

            ShareResultReceiver shareResultReceiver = shareResultReceiverHost.getShareResultReceiver();
            if (shareResultReceiver == null) {
                Timber.e("share result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                Timber.v("qq share success");
                shareResultReceiver.onQQShareSuccess();
                return;
            } else if (isResultFail(result)) {
                Timber.v("qq share fail");
                shareResultReceiver.onQQShareFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("qq share cancel");
                shareResultReceiver.onQQShareCancel();
                return;
            } else {
                Timber.e("unknown qq share result:%s", result);
                return;
            }
        } else if (isQzoneShare(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("qzone share result:%s", result);

            ShareResultReceiver shareResultReceiver = shareResultReceiverHost.getShareResultReceiver();
            if (shareResultReceiver == null) {
                Timber.e("share result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                Timber.v("qzone share success");
                shareResultReceiver.onQzoneShareSuccess();
                return;
            } else if (isResultFail(result)) {
                Timber.v("qzone share fail");
                shareResultReceiver.onQzoneShareFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("qzone share cancel");
                shareResultReceiver.onQzoneShareCancel();
                return;
            } else {
                Timber.e("unknown qzone share result:%s", result);
                return;
            }
        } else if (isWeixinShare(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("weixin share result:%s", result);

            ShareResultReceiver shareResultReceiver = shareResultReceiverHost.getShareResultReceiver();
            if (shareResultReceiver == null) {
                Timber.e("share result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                Timber.v("weixin share success");
                shareResultReceiver.onWeixinShareSuccess();
                return;
            } else if (isResultFail(result)) {
                Timber.v("weixin share fail");
                shareResultReceiver.onWeixinShareFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("weixin share cancel");
                shareResultReceiver.onWeixinShareCancel();
                return;
            } else {
                Timber.e("unknown weixin share result:%s", result);
                return;
            }
        } else if (isWeixinTimelineShare(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("weixin timeline share result:%s", result);

            ShareResultReceiver shareResultReceiver = shareResultReceiverHost.getShareResultReceiver();
            if (shareResultReceiver == null) {
                Timber.e("share result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                Timber.v("weixin timeline share success");
                shareResultReceiver.onWeixinTimelineShareSuccess();
                return;
            } else if (isResultFail(result)) {
                Timber.v("weixin timeline share fail");
                shareResultReceiver.onWeixinTimelineShareFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("weixin timeline share cancel");
                shareResultReceiver.onWeixinTimelineShareCancel();
                return;
            } else {
                Timber.e("unknown weixin timeline share result:%s", result);
                return;
            }
        } else if (isWeixinMiniprogrameShare(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("weixin miniprograme share result:%s", result);

            ShareResultReceiver shareResultReceiver = shareResultReceiverHost.getShareResultReceiver();
            if (shareResultReceiver == null) {
                Timber.e("share result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                Timber.v("weixin miniprograme share success");
                shareResultReceiver.onWeixinMiniprogrameShareSuccess();
                return;
            } else if (isResultFail(result)) {
                Timber.v("weixin miniprograme share fail");
                shareResultReceiver.onWeixinMiniprogrameShareFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("weixin miniprograme share cancel");
                shareResultReceiver.onWeixinMiniprogrameShareCancel();
                return;
            } else {
                Timber.e("unknown weixin miniprograme share result:%s", result);
                return;
            }
        } else if (isWeiboShare(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("weibo share result:%s", result);

            ShareResultReceiver shareResultReceiver = shareResultReceiverHost.getShareResultReceiver();
            if (shareResultReceiver == null) {
                Timber.e("share result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                Timber.v("weibo share success");
                shareResultReceiver.onWeiboShareSuccess();
                return;
            } else if (isResultFail(result)) {
                Timber.v("weibo share fail");
                shareResultReceiver.onWeiboShareFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("weibo share cancel");
                shareResultReceiver.onWeiboShareCancel();
                return;
            } else {
                Timber.e("unknown weibo share result:%s", result);
                return;
            }
        } else {
            Timber.e("unknown processShareAction:%s", processShareAction);
            return;
        }
    }

    public interface ShareResultReceiverHost {
        ShareResultReceiver getShareResultReceiver();

        AuthResultReceiver getAuthResultReceiver();
    }

    public interface ShareResultReceiver {
        void onQQShareSuccess();

        void onQQShareFail();

        void onQQShareCancel();

        void onQzoneShareSuccess();

        void onQzoneShareFail();

        void onQzoneShareCancel();

        void onWeixinShareSuccess();

        void onWeixinShareFail();

        void onWeixinShareCancel();

        void onWeixinTimelineShareSuccess();

        void onWeixinTimelineShareFail();

        void onWeixinTimelineShareCancel();

        void onWeixinMiniprogrameShareSuccess();

        void onWeixinMiniprogrameShareFail();

        void onWeixinMiniprogrameShareCancel();

        void onWeiboShareSuccess();

        void onWeiboShareFail();

        void onWeiboShareCancel();
    }

    public static class SampleShareResultReceiver implements ShareResultReceiver {

        @Override
        public void onQQShareSuccess() {
            Timber.v("onQQShareSuccess");
        }

        @Override
        public void onQQShareFail() {
            Timber.v("onQQShareFail");
        }

        @Override
        public void onQQShareCancel() {
            Timber.v("onQQShareCancel");
        }

        @Override
        public void onQzoneShareSuccess() {
            Timber.v("onQzoneShareSuccess");
        }

        @Override
        public void onQzoneShareFail() {
            Timber.v("onQzoneShareFail");
        }

        @Override
        public void onQzoneShareCancel() {
            Timber.v("onQzoneShareCancel");
        }

        @Override
        public void onWeixinShareSuccess() {
            Timber.v("onWeixinShareSuccess");
        }

        @Override
        public void onWeixinShareFail() {
            Timber.v("onWeixinShareFail");
        }

        @Override
        public void onWeixinShareCancel() {
            Timber.v("onWeixinShareCancel");
        }

        @Override
        public void onWeixinTimelineShareSuccess() {
            Timber.v("onWeixinTimelineShareSuccess");
        }

        @Override
        public void onWeixinTimelineShareFail() {
            Timber.v("onWeixinTimelineShareFail");
        }

        @Override
        public void onWeixinTimelineShareCancel() {
            Timber.v("onWeixinTimelineShareCancel");
        }

        @Override
        public void onWeixinMiniprogrameShareSuccess() {
            Timber.v("onWeixinMiniprogrameShareSuccess");
        }

        @Override
        public void onWeixinMiniprogrameShareFail() {
            Timber.v("onWeixinMiniprogrameShareFail");
        }

        @Override
        public void onWeixinMiniprogrameShareCancel() {
            Timber.v("onWeixinMiniprogrameShareCancel");
        }

        @Override
        public void onWeiboShareSuccess() {
            Timber.v("onWeiboShareSuccess");
        }

        @Override
        public void onWeiboShareFail() {
            Timber.v("onWeiboShareFail");
        }

        @Override
        public void onWeiboShareCancel() {
            Timber.v("onWeiboShareCancel");
        }
    }

    public interface AuthResultReceiver {
        void onQQAuthSuccess(QQAuthInfo result);

        void onQQAuthFail();

        void onQQAuthCancel();

        void onWeixinAuthSuccess(WeixinAuthInfo result);

        void onWeixinAuthFail();

        void onWeixinAuthCancel();

        void onWeiboAuthSuccess(WeiboAuthInfo result);

        void onWeiboAuthFail();

        void onWeiboAuthCancel();
    }

    public static class SampleAuthResultReceiver implements AuthResultReceiver {

        @Override
        public void onQQAuthSuccess(QQAuthInfo result) {
            Timber.v("onQQAuthSuccess:%s", result);
        }

        @Override
        public void onQQAuthFail() {
            Timber.v("onQQAuthFail");
        }

        @Override
        public void onQQAuthCancel() {
            Timber.v("onQQAuthCancel");
        }

        @Override
        public void onWeixinAuthSuccess(WeixinAuthInfo result) {
            Timber.v("onWeixinAuthSuccess:%s", result);
        }

        @Override
        public void onWeixinAuthFail() {
            Timber.v("onWeixinAuthFail");
        }

        @Override
        public void onWeixinAuthCancel() {
            Timber.v("onWeixinAuthCancel");
        }

        @Override
        public void onWeiboAuthSuccess(WeiboAuthInfo result) {
            Timber.v("onWeiboAuthSuccess:%s", result);
        }

        @Override
        public void onWeiboAuthFail() {
            Timber.v("onWeiboAuthFail");
        }

        @Override
        public void onWeiboAuthCancel() {
            Timber.v("onWeiboAuthCancel");
        }
    }

}

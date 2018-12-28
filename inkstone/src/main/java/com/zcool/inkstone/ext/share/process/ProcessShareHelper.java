package com.zcool.inkstone.ext.share.process;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.zcool.inkstone.ext.share.process.entity.ImageShareQQParams;
import com.zcool.inkstone.ext.share.process.entity.ImageShareWeixinParams;
import com.zcool.inkstone.ext.share.process.entity.ImageShareWeixinTimelineParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareQQParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareQzoneParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareWeiboParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareWeixinMiniprogrameParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareWeixinParams;
import com.zcool.inkstone.ext.share.process.entity.ImageTextShareWeixinTimelineParams;
import com.zcool.inkstone.ext.share.process.entity.PayWeixinParams;
import com.zcool.inkstone.ext.share.process.entity.QQAuthInfo;
import com.zcool.inkstone.ext.share.process.entity.WeiboAuthInfo;
import com.zcool.inkstone.ext.share.process.entity.WeixinAuthInfo;
import com.zcool.inkstone.thread.Threads;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentActivity;
import timber.log.Timber;

/**
 * 处理在多进程中的第三方分享与登录
 */
public class ProcessShareHelper {

    private static final int REQUEST_CODE_DEFAULT = 1;

    /////////
    public static final String EXTRA_PROCESS_SHARE_ACTION = "process.share_action";

    public static final String PROCESS_SHARE_ACTION_QQ_SHARE = "QQ_SHARE";
    public static final String PROCESS_SHARE_ACTION_QZONE_SHARE = "QZONE_SHARE";
    public static final String PROCESS_SHARE_ACTION_WEIXIN_SHARE = "WEIXIN_SHARE";
    public static final String PROCESS_SHARE_ACTION_WEIXIN_PAY = "WEIXIN_PAY";
    public static final String PROCESS_SHARE_ACTION_WEIXIN_TIMELINE_SHARE = "WEIXIN_TIMELINE_SHARE";
    public static final String PROCESS_SHARE_ACTION_WEIXIN_MINIPROGRAME_SHARE = "WEIXIN_MINIPROGRAME_SHARE";
    public static final String PROCESS_SHARE_ACTION_WEIBO_SHARE = "WEIBO_SHARE";

    public static final String PROCESS_SHARE_ACTION_QQ_AUTH = "QQ_AUTH";
    public static final String PROCESS_SHARE_ACTION_WEIXIN_AUTH = "WEIXIN_AUTH";
    public static final String PROCESS_SHARE_ACTION_WEIBO_AUTH = "WEIBO_AUTH";

    /////////
    public static final String EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE = "process.share_action.sub_type";

    public static final String PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE_TEXT = "IMAGE_TEXT";
    public static final String PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE = "IMAGE";
    public static final String PROCESS_SHARE_ACTION_SUB_TYPE_PAY = "PAY";

    /////////
    public static final String EXTRA_PROCESS_SHARE_ACTION_RESULT = "process.share_action.result";

    public static final String PROCESS_SHARE_ACTION_RESULT_SUCCESS = "SUCCESS";
    public static final String PROCESS_SHARE_ACTION_RESULT_FAIL = "FAIL";
    public static final String PROCESS_SHARE_ACTION_RESULT_CANCEL = "CANCEL";

    /////////
    public static final String EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA = "process.share_action.request.data";

    /////////
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

    public static boolean isWeixinPay(@Nullable String processShareAction) {
        return PROCESS_SHARE_ACTION_WEIXIN_PAY.equals(processShareAction);
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
    public static String getProcessShareActionSubType(Intent intent) {
        return intent.getStringExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE);
    }

    public static boolean isProcessShareActionSubTypeImageText(@Nullable String processShareActionSubType) {
        return PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE_TEXT.equals(processShareActionSubType);
    }

    public static boolean isProcessShareActionSubTypePay(@Nullable String processShareActionSubType) {
        return PROCESS_SHARE_ACTION_SUB_TYPE_PAY.equals(processShareActionSubType);
    }

    public static boolean isProcessShareActionSubTypeImage(@Nullable String processShareActionSubType) {
        return PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE.equals(processShareActionSubType);
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
    public static Bundle getProcessShareActionRequestData(@NonNull Intent intent) {
        return intent.getBundleExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA);
    }

    @Nullable
    public static Bundle getProcessShareActionResultData(@NonNull Intent intent) {
        return intent.getBundleExtra(EXTRA_PROCESS_SHARE_ACTION_RESULT_DATA);
    }

    @UiThread
    public static void requestQQAuth(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_QQ_AUTH);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestWeixinAuth(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIXIN_AUTH);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestWeiboAuth(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIBO_AUTH);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestQQShare(Activity activity, ImageTextShareQQParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_QQ_SHARE);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE_TEXT);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestQQShare(Activity activity, ImageShareQQParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_QQ_SHARE);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestQzoneShare(Activity activity, ImageTextShareQzoneParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_QZONE_SHARE);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE_TEXT);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestWeixinShare(Activity activity, ImageTextShareWeixinParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIXIN_SHARE);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE_TEXT);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestWeixinShare(Activity activity, ImageShareWeixinParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIXIN_SHARE);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestWeixinPay(Activity activity, PayWeixinParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIXIN_PAY);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_PAY);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestWeixinTimelineShare(Activity activity, ImageTextShareWeixinTimelineParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIXIN_TIMELINE_SHARE);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE_TEXT);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestWeixinTimelineShare(Activity activity, ImageShareWeixinTimelineParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIXIN_TIMELINE_SHARE);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestWeixinMiniprogrameShare(Activity activity, ImageTextShareWeixinMiniprogrameParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIXIN_MINIPROGRAME_SHARE);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE_TEXT);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void requestWeiboShare(Activity activity, ImageTextShareWeiboParams params) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        if (fragment == null) {
            Timber.e("fragment is null");
            return;
        }

        Intent intent = new Intent(activity, ProcessShareActivity.class);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION, PROCESS_SHARE_ACTION_WEIBO_SHARE);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_SUB_TYPE, PROCESS_SHARE_ACTION_SUB_TYPE_IMAGE_TEXT);

        Bundle extrasData = new Bundle();
        params.writeToBundle(extrasData);
        intent.putExtra(EXTRA_PROCESS_SHARE_ACTION_REQUEST_DATA, extrasData);

        fragment.startActivityForResult(intent, REQUEST_CODE_DEFAULT);
    }

    @UiThread
    public static void onActivityResult(ProcessShareFragment fragment, int requestCode, int resultCode, Intent data) {
        Timber.v("onActivityResult requestCode:%s, resultCode:%s, data:%s", requestCode, resultCode, data);

        if (!Threads.mustUi()) {
            return;
        }

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
        } else if (isWeixinPay(processShareAction)) {
            String result = getProcessShareActionResult(data);
            Timber.v("weixin pay result:%s", result);

            PayResultReceiver payResultReceiver = shareResultReceiverHost.getPayResultReceiver();
            if (payResultReceiver == null) {
                Timber.e("pay result receiver is null");
                return;
            }

            if (isResultSuccess(result)) {
                Timber.v("weixin pay success");
                payResultReceiver.onWeixinPaySucess();
                return;
            } else if (isResultFail(result)) {
                Timber.v("weixin pay fail");
                payResultReceiver.onWeixinPayFail();
                return;
            } else if (isResultCancel(result)) {
                Timber.v("weixin pay cancel");
                payResultReceiver.onWeixinPayCancel();
                return;
            } else {
                Timber.e("unknown weixin pay result:%s", result);
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

        PayResultReceiver getPayResultReceiver();
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

    public static class SimpleShareResultReceiver implements ShareResultReceiver {

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

    public interface PayResultReceiver {
        void onWeixinPaySucess();

        void onWeixinPayFail();

        void onWeixinPayCancel();
    }

    public static class SimplePayResultReceiver implements PayResultReceiver {

        @Override
        public void onWeixinPaySucess() {
            Timber.v("onWeixinPaySucess");
        }

        @Override
        public void onWeixinPayFail() {
            Timber.v("onWeixinPaySucess");
        }

        @Override
        public void onWeixinPayCancel() {
            Timber.v("onWeixinPaySucess");
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

    public static class SimpleAuthResultReceiver implements AuthResultReceiver {

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

    @UiThread
    public static boolean isSupportQQAppAuth(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        return fragment != null && fragment.isSupportQQAppAuth();
    }

    @UiThread
    public static boolean isSupportQQAppShare(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        return fragment != null && fragment.isSupportQQAppShare();
    }

    @UiThread
    public static boolean isSupportQzoneAppShare(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        return fragment != null && fragment.isSupportQzoneAppShare();
    }

    @UiThread
    public static boolean isSupportWeixinAppAuth(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        return fragment != null && fragment.isSupportWeixinAppAuth();
    }

    @UiThread
    public static boolean isSupportWeixinAppShare(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        return fragment != null && fragment.isSupportWeixinAppShare();
    }

    @UiThread
    public static boolean isSupportWeixinAppPay(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        return fragment != null && fragment.isSupportWeixinAppPay();
    }

    @UiThread
    public static boolean isSupportWeiboAppAuth(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        return fragment != null && fragment.isSupportWeiboAppAuth();
    }

    @UiThread
    public static boolean isSupportWeiboAppShare(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        return fragment != null && fragment.isSupportWeiboAppShare();
    }

    @UiThread
    public static boolean isSupportWeiboAppShareWithMultiImage(Activity activity) {
        ProcessShareFragment fragment = getOrCreateFragment(activity);
        return fragment != null && fragment.isSupportWeiboAppShareWithMultiImage();
    }

    @UiThread
    @Nullable
    private static ProcessShareFragment getOrCreateFragment(Activity activity) {
        if (!Threads.mustUi()) {
            return null;
        }

        if (activity == null) {
            Timber.e("activity is null");
            return null;
        }

        if (!(activity instanceof FragmentActivity)) {
            Timber.e("activity must type of FragmentActivity %s", activity);
            return null;
        }

        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        if (fragmentActivity.getSupportFragmentManager().isStateSaved()) {
            Timber.e("activity already state saved.");
            return null;
        }

        return ProcessShareFragment.getOrCreate(fragmentActivity);
    }

}

package com.zcool.inkstone.ext.share.process;

import android.content.Intent;
import android.os.Bundle;

import com.zcool.inkstone.ext.share.LifecyclerShareHelper;
import com.zcool.inkstone.ext.share.ShareHelper;
import com.zcool.inkstone.ext.share.util.AuthUtil;
import com.zcool.inkstone.ext.share.util.ShareUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class ProcessShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.v("onCreate %s", savedInstanceState);

        if (!startRedirect(getIntent())) {
            finish();
        }
    }

    private boolean startRedirect(@NonNull Intent intent) {
        final String processShareAction = ProcessShareHelper.getProcessShareAction(intent);
        ShareHelper shareHelper = getShareHelper(true);
        if (shareHelper == null) {
            Timber.e("share helper is null");
            return false;
        }

        if (ProcessShareHelper.isQQAuth(processShareAction)) {
            AuthUtil.requestQQAuth(shareHelper);
            return true;
        } else if (ProcessShareHelper.isWeixinAuth(processShareAction)) {
            AuthUtil.requestWeixinAuth(shareHelper);
            return true;
        } else if (ProcessShareHelper.isWeiboAuth(processShareAction)) {
            AuthUtil.requestWeiboAuth(shareHelper);
            return true;
        } else if (ProcessShareHelper.isQQShare(processShareAction)) {
            // TODO
            return true;
        } else if (ProcessShareHelper.isQzoneShare(processShareAction)) {
            // TODO
            return true;
        } else if (ProcessShareHelper.isWeixinShare(processShareAction)) {
            // TODO
            return true;
        } else if (ProcessShareHelper.isWeixinTimelineShare(processShareAction)) {
            // TODO
            return true;
        } else if (ProcessShareHelper.isWeixinMiniprogrameShare(processShareAction)) {
            // TODO
            return true;
        } else if (ProcessShareHelper.isWeiboShare(processShareAction)) {
            // TODO
            return true;
        } else {
            Timber.e("unknown process share action:%s", processShareAction);
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ShareHelper shareHelper = getShareHelper(false);
        if (shareHelper != null) {
            shareHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ShareHelper mShareHelper;

    public ShareHelper getShareHelper(boolean autoCreate) {
        if (mShareHelper == null && autoCreate) {
            mShareHelper = LifecyclerShareHelper.create(
                    this,
                    mAuthListener,
                    mShareListener);
        }
        return mShareHelper;
    }

    private ShareHelper.AuthListener mAuthListener = AuthUtil.newAuthListener(new AuthUtil.SimpleAuthListener() {
        @Override
        public void onQQAuthSuccess(@NonNull QQAuthInfo info) {
            super.onQQAuthSuccess(info);

            Intent data = new Intent();
            Bundle extrasData = new Bundle();
            if (info != null) {
                info.writeToBundle(extrasData);
            }
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT_DATA, extrasData);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onQQAuthFail() {
            super.onQQAuthFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onQQAuthCancel() {
            super.onQQAuthCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinAuthSuccess(@NonNull WeixinAuthInfo info) {
            super.onWeixinAuthSuccess(info);

            Intent data = new Intent();
            Bundle extrasData = new Bundle();
            if (info != null) {
                info.writeToBundle(extrasData);
            }
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT_DATA, extrasData);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinAuthFail() {
            super.onWeixinAuthFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinAuthCancel() {
            super.onWeixinAuthCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboAuthSuccess(@NonNull WeiboAuthInfo info) {
            super.onWeiboAuthSuccess(info);

            Intent data = new Intent();
            Bundle extrasData = new Bundle();
            if (info != null) {
                info.writeToBundle(extrasData);
            }
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT_DATA, extrasData);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboAuthFail() {
            super.onWeiboAuthFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboAuthCancel() {
            super.onWeiboAuthCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_AUTH);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }
    });

    private ShareHelper.ShareListener mShareListener = ShareUtil.newShareListener(new ShareUtil.SimpleShareListener() {
        @Override
        public void onQQShareSuccess() {
            super.onQQShareSuccess();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onQQShareFail() {
            super.onQQShareFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onQQShareCancel() {
            super.onQQShareCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_QQ_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinShareSuccess() {
            super.onWeixinShareSuccess();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinShareFail() {
            super.onWeixinShareFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeixinShareCancel() {
            super.onWeixinShareCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIXIN_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboShareSuccess() {
            super.onWeiboShareSuccess();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_SUCCESS);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboShareFail() {
            super.onWeiboShareFail();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_FAIL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }

        @Override
        public void onWeiboShareCancel() {
            super.onWeiboShareCancel();

            Intent data = new Intent();
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION, ProcessShareHelper.PROCESS_SHARE_ACTION_WEIBO_SHARE);
            data.putExtra(ProcessShareHelper.EXTRA_PROCESS_SHARE_ACTION_RESULT, ProcessShareHelper.PROCESS_SHARE_ACTION_RESULT_CANCEL);
            setResult(RESULT_FIRST_USER, data);

            finish();
        }
    });

}

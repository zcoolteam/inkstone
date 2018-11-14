package com.zcool.inkstone.ext.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import timber.log.Timber;

public class FixWebView extends WebView {

    public FixWebView(Context context) {
        super(context);
        init();
    }

    public FixWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FixWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FixWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private CustomViewer mCustomViewer;

    protected void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setUserAgentString(settings.getUserAgentString());
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setLoadsImagesAutomatically(true);
        settings.setGeolocationEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        setWebViewClient(new WebViewClientImpl(this));
        setWebChromeClient(new WebChromeClientImpl(this));
    }

    public static class WebViewClientImpl extends WebViewClient {

        protected final FixWebView mFixWebView;

        public WebViewClientImpl(FixWebView fixWebView) {
            mFixWebView = fixWebView;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Timber.v("shouldOverrideUrlLoading %s", url);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Timber.v("onPageStarted %s, webview url %s", url, view.getUrl());
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            Timber.v("onPageCommitVisible %s", url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Timber.v("onPageFinished %s, webview url %s", url, view.getUrl());
        }
    }

    public static class WebChromeClientImpl extends WebChromeClient {

        protected final FixWebView mFixWebView;

        public WebChromeClientImpl(FixWebView fixWebView) {
            mFixWebView = fixWebView;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            Timber.v("onReceivedTitle %s", title);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Timber.v("onShowCustomView");
            if (mFixWebView.mCustomViewer != null) {
                mFixWebView.mCustomViewer.show(view, callback);
            }
        }

        @Override
        public void onShowCustomView(
                View view, int requestedOrientation, CustomViewCallback callback) {
            Timber.v("onShowCustomView requestedOrientation %s", requestedOrientation);
            onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            Timber.v("onHideCustomView");
            if (mFixWebView.mCustomViewer != null) {
                mFixWebView.mCustomViewer.hide();
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(
                String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, true);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        try {
            if (mCustomViewer != null) {
                mCustomViewer.hide();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean dispatchBackPressed() {
        if (canGoBack()) {
            goBack();
            return true;
        }
        return false;
    }

    public void setCustomViewer(CustomViewer customViewer) {
        if (mCustomViewer != null) {
            throw new IllegalAccessError("already set custom viewer");
        }
        mCustomViewer = customViewer;
    }

    public static class CustomViewer {

        private final Activity mActivity;
        private final int mOriginalRequestOrientation;
        private final ViewGroup mParent;
        private final boolean mIgnoreFullscreen;

        private View mView;
        private WebChromeClient.CustomViewCallback mCallback;

        public CustomViewer(Activity activity, ViewGroup parent) {
            this(activity, parent, false);
        }

        public CustomViewer(Activity activity, ViewGroup parent, boolean ignoreFullscreen) {
            mActivity = activity;
            mOriginalRequestOrientation = mActivity.getRequestedOrientation();
            mParent = parent;
            mIgnoreFullscreen = ignoreFullscreen;

            Timber.v("original request orientation %s", mOriginalRequestOrientation);
        }

        public void show(View view, WebChromeClient.CustomViewCallback callback) {
            if (view == null) {
                Timber.e("view is null");
                return;
            }

            if (mView != null) {
                Timber.e("already exist custom view %s", mView);
                return;
            }

            mView = createDecorView(view);
            mCallback = callback;

            mParent.addView(mView);
            if (!mIgnoreFullscreen) {
                requestFullscreen();
            }
        }

        public View createDecorView(View view) {
            FrameLayout decorView = new FrameLayout(view.getContext());
            decorView.setBackgroundColor(Color.BLACK);
            ViewGroup.LayoutParams decorViewLayoutParams =
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            decorView.setLayoutParams(decorViewLayoutParams);
            decorView.addView(view);
            return decorView;
        }

        public void hide() {
            if (mView == null) {
                return;
            }

            mParent.removeView(mView);
            mView = null;
            if (mCallback != null) {
                mCallback.onCustomViewHidden();
            }
            if (!mIgnoreFullscreen) {
                requestExitFullscreen();
            }
        }

        private Runnable mRequestSystemUiRunnable;

        protected void requestSystemUiFullscreen(final View view) {
            int systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            final int originalSystemUiVisibility = systemUiVisibility;
            final int normalSystemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            view.setSystemUiVisibility(systemUiVisibility);
            view.setOnSystemUiVisibilityChangeListener(
                    new OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            Timber.v(
                                    "requestSystemUiFullscreen onSystemUiVisibilityChange %s",
                                    visibility);

                            mRequestSystemUiRunnable =
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mRequestSystemUiRunnable != this) {
                                                return;
                                            }

                                            view.setSystemUiVisibility(originalSystemUiVisibility);
                                        }
                                    };

                            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                                view.setSystemUiVisibility(normalSystemUiVisibility);
                            }
                            view.postDelayed(mRequestSystemUiRunnable, 1800L);
                        }
                    });
        }

        public void requestFullscreen() {
            if (mView != null) {
                requestSystemUiFullscreen(mView);
            }
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        public void requestExitFullscreen() {
            mActivity.setRequestedOrientation(mOriginalRequestOrientation);
        }
    }

}

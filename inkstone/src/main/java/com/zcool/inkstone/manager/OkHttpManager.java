package com.zcool.inkstone.manager;

import android.text.TextUtils;

import com.zcool.inkstone.ApplicationDelegateRoot;
import com.zcool.inkstone.Debug;
import com.zcool.inkstone.lang.OkHttp3CookieJar;
import com.zcool.inkstone.lang.Singleton;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

/**
 * okhttp3
 */
public class OkHttpManager {

    private static final Singleton<OkHttpManager> sInstance =
            new Singleton<OkHttpManager>() {
                @Override
                protected OkHttpManager create() {
                    return new OkHttpManager();
                }
            };

    private static boolean sInit;

    public static OkHttpManager getInstance() {
        OkHttpManager instance = sInstance.get();
        sInit = true;
        return instance;
    }

    public static boolean isInit() {
        return sInit;
    }

    private OkHttpClient mDefaultOkHttpClient;

    private OkHttpManager() {
        Timber.v("init");

        mDefaultOkHttpClient = createNew(false);
    }

    public OkHttpClient createNew(final boolean disableDebugHttpBody) {
        return createNew(disableDebugHttpBody, null, false);
    }

    public OkHttpClient createNew(final boolean disableDebugHttpBody, final String customUserAgent) {
        return createNew(disableDebugHttpBody, customUserAgent, false);
    }

    public OkHttpClient createNew(final boolean disableDebugHttpBody, final String customUserAgent, final boolean alwaysUseCustomUserAgent) {
        final String HEADER_USER_AGENT = "User-Agent";
        Interceptor defaultUserAgentInterceptor =
                new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        if (alwaysUseCustomUserAgent) {
                            if (customUserAgent == null) {
                                return chain.proceed(
                                        chain.request()
                                                .newBuilder()
                                                .removeHeader(HEADER_USER_AGENT)
                                                .build());
                            }

                            return chain.proceed(
                                    chain.request()
                                            .newBuilder()
                                            .header(HEADER_USER_AGENT, customUserAgent)
                                            .build());
                        }

                        if (chain.request().header(HEADER_USER_AGENT) != null) {
                            return chain.proceed(chain.request());
                        }

                        String defaultUserAgent = customUserAgent;
                        if (TextUtils.isEmpty(defaultUserAgent)) {
                            defaultUserAgent = ApplicationDelegateRoot.getInstance().getDefaultUserAgent();
                        }
                        if (TextUtils.isEmpty(defaultUserAgent)) {
                            return chain.proceed(chain.request());
                        }

                        return chain.proceed(
                                chain.request()
                                        .newBuilder()
                                        .header(HEADER_USER_AGENT, defaultUserAgent)
                                        .build());
                    }
                };

        boolean debug = Debug.isDebug();
        boolean debugHttpBody = !disableDebugHttpBody && Debug.isDebugHttpBody();
        if (debug) {
            Timber.d("createNew OkHttpClient: debug");

            Interceptor contentEncodingInterceptor =
                    new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Timber.d("contentEncodingInterceptor intercept");
                            Request request =
                                    chain.request()
                                            .newBuilder()
                                            .header("Accept-Encoding", "identity")
                                            .build();
                            return chain.proceed(request);
                        }
                    };

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Timber.d(message);
                }
            });

            if (debugHttpBody) {
                Timber.w("createNew OkHttpClient: debug http body");
            }
            httpLoggingInterceptor.setLevel(debugHttpBody ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.HEADERS);

            return new OkHttpClient.Builder()
                    .addInterceptor(defaultUserAgentInterceptor)
                    .addInterceptor(contentEncodingInterceptor)
                    .addInterceptor(mExtInterceptorAdapter)
                    .addNetworkInterceptor(httpLoggingInterceptor)
                    .cookieJar(new OkHttp3CookieJar())
                    .build();
        } else {
            return new OkHttpClient.Builder()
                    .addInterceptor(defaultUserAgentInterceptor)
                    .addInterceptor(mExtInterceptorAdapter)
                    .cookieJar(new OkHttp3CookieJar())
                    .build();
        }
    }

    public OkHttpClient getDefaultOkHttpClient() {
        return mDefaultOkHttpClient;
    }

    private final Interceptor mExtInterceptorAdapter = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            if (mExtInterceptor != null) {
                return mExtInterceptor.intercept(chain);
            }
            return chain.proceed(chain.request());
        }
    };

    private Interceptor mExtInterceptor;

    public void setExtInterceptor(Interceptor mExtInterceptor) {
        this.mExtInterceptor = mExtInterceptor;
    }

    public Interceptor getExtInterceptor() {
        return mExtInterceptor;
    }

}

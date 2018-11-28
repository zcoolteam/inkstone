package com.zcool.inkstone;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import com.zcool.inkstone.util.ContextUtil;

import java.util.WeakHashMap;

import androidx.annotation.Nullable;
import timber.log.Timber;

public class AppCallbacks {

    private final InternalApplicationCallbacks mInternalApplicationCallbacks =
            new InternalApplicationCallbacks();

    AppCallbacks() {
        Timber.v("init");

        Application application = (Application) ContextUtil.getContext();
        application.registerActivityLifecycleCallbacks(mInternalApplicationCallbacks);
        application.registerComponentCallbacks(mInternalApplicationCallbacks);
    }

    /**
     * internal use weak ref to hold callbacks. so callbacks support auto remove when it's recycled
     */
    public void addApplicationCallbacks(ApplicationCallbacks callbacks) {
        mInternalApplicationCallbacks.addCallback(callbacks);
    }

    public void removeApplicationCallbacks(ApplicationCallbacks callbacks) {
        mInternalApplicationCallbacks.removeCallback(callbacks);
    }

    public interface ApplicationCallbacks
            extends Application.ActivityLifecycleCallbacks,
            ComponentCallbacks,
            ComponentCallbacks2 {
    }

    private static class InternalApplicationCallbacks implements ApplicationCallbacks {

        private final Object mEmptyObject = new Object();
        private final WeakHashMap<ApplicationCallbacks, Object> mOuterCallbacks =
                new WeakHashMap<>();

        private void addCallback(ApplicationCallbacks callback) {
            synchronized (mOuterCallbacks) {
                mOuterCallbacks.put(callback, mEmptyObject);
            }
        }

        private void removeCallback(ApplicationCallbacks callback) {
            synchronized (mOuterCallbacks) {
                mOuterCallbacks.remove(callback);
            }
        }

        @Nullable
        private Object[] getCallbacks() {
            Object[] callbacks = null;
            synchronized (mOuterCallbacks) {
                if (!mOuterCallbacks.isEmpty()) {
                    callbacks = mOuterCallbacks.keySet().toArray();
                }
            }
            return callbacks;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback)
                                .onActivityCreated(activity, savedInstanceState);
                    }
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback).onActivityStarted(activity);
                    }
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback).onActivityResumed(activity);
                    }
                }
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback).onActivityPaused(activity);
                    }
                }
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback).onActivityStopped(activity);
                    }
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback)
                                .onActivitySaveInstanceState(activity, outState);
                    }
                }
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback).onActivityDestroyed(activity);
                    }
                }
            }
        }

        @Override
        public void onTrimMemory(int level) {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback).onTrimMemory(level);
                    }
                }
            }
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback).onConfigurationChanged(newConfig);
                    }
                }
            }
        }

        @Override
        public void onLowMemory() {
            Object[] callbacks = getCallbacks();
            if (callbacks != null) {
                for (Object callback : callbacks) {
                    if (callback instanceof ApplicationCallbacks) {
                        ((ApplicationCallbacks) callback).onLowMemory();
                    }
                }
            }
        }
    }

    public static class SimpleApplicationCallbacks implements ApplicationCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        @Override
        public void onTrimMemory(int level) {
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
        }

        @Override
        public void onLowMemory() {
        }
    }

}

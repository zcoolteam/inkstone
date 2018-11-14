package com.zcool.inkstone.manager;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.logging.FLog;
import com.facebook.common.logging.FLogDefaultLoggingDelegate;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imageformat.ImageFormatChecker;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.zcool.inkstone.BaseApplicationDelegate;
import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.thread.Threads;
import com.zcool.inkstone.util.ContextUtil;
import com.zcool.inkstone.util.FileUtil;
import com.zcool.inkstone.util.IOUtil;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Executor;

import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import timber.log.Timber;

public class FrescoManager {

    private static final Singleton<FrescoManager> sInstance =
            new Singleton<FrescoManager>() {
                @Override
                protected FrescoManager create() {
                    return new FrescoManager();
                }
            };

    public static FrescoManager getInstance() {
        return sInstance.get();
    }

    private FrescoManager() {
        Timber.v("init");

        File frescoCacheBaseDir = FileUtil.getAppCacheDir();

        if (frescoCacheBaseDir == null) {
            throw new IllegalAccessError("fresco cache base dir null");
        }

        FLogDefaultLoggingDelegate fLogDefaultLoggingDelegate = FLogDefaultLoggingDelegate.getInstance();
        fLogDefaultLoggingDelegate.setApplicationTag(ContextUtil.getContext().getPackageName());
        if (BaseApplicationDelegate.getInstance().isDebug()) {
            fLogDefaultLoggingDelegate.setMinimumLoggingLevel(FLog.DEBUG);
        }

        ImagePipelineConfig.Builder imagePipelineConfigBuilder = ImagePipelineConfig.newBuilder(ContextUtil.getContext())
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(ContextUtil.getContext())
                        .setBaseDirectoryPath(frescoCacheBaseDir)
                        .setBaseDirectoryName("fresco_main_disk_" + ProcessManager.getInstance().getProcessTag())
                        .build())
                .setSmallImageDiskCacheConfig(DiskCacheConfig.newBuilder(ContextUtil.getContext())
                        .setBaseDirectoryPath(frescoCacheBaseDir)
                        .setBaseDirectoryName("fresco_small_disk_" + ProcessManager.getInstance().getProcessTag())
                        .build())
                .setNetworkFetcher(new OkHttpNetworkFetcher(OkHttpManager.getInstance().getDefaultOkHttpClient()))
                .setDownsampleEnabled(true);

        Fresco.initialize(ContextUtil.getContext(), imagePipelineConfigBuilder.build());
    }

    public SingleSource<File> fetchImage(final ImageRequest imageRequest) {
        return new SingleSource<File>() {
            @Override
            public void subscribe(final SingleObserver<? super File> observer) {
                Fresco.getImagePipeline().fetchEncodedImage(imageRequest, null)
                        .subscribe(new BaseDataSubscriber<CloseableReference<PooledByteBuffer>>() {
                            @Override
                            protected void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                                if (!dataSource.isFinished()) {
                                    return;
                                }

                                ImageFormat imageFormat = null;
                                InputStream is = null;
                                CloseableReference<PooledByteBuffer> buffer = null;

                                File errorTmpFile = null;

                                try {
                                    buffer = dataSource.getResult();

                                    is = new PooledByteBufferInputStream(buffer.get());
                                    imageFormat = ImageFormatChecker.getImageFormat(is);

                                    if (imageFormat == null || imageFormat == ImageFormat.UNKNOWN) {
                                        throw new IllegalAccessException("unknown image format " + getString(imageFormat));
                                    }

                                    String extension = imageFormat.getFileExtension();
                                    if (TextUtils.isEmpty(extension)) {
                                        throw new IllegalAccessException("unknown extension " + getString(imageFormat));
                                    }

                                    File targetFile = TmpFileManager.getInstance().createNewTmpFileQuietly(null, "." + extension);
                                    if (targetFile == null) {
                                        throw new IllegalAccessException("TmpFileManager create target file return null");
                                    }

                                    errorTmpFile = targetFile;
                                    IOUtil.copy(is, targetFile, null, null);
                                    errorTmpFile = null;

                                    observer.onSuccess(targetFile);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                    observer.onError(e);
                                } finally {
                                    IOUtil.closeQuietly(is);
                                    CloseableReference.closeSafely(buffer);
                                    FileUtil.deleteFileQuietly(errorTmpFile);
                                }
                            }

                            @Override
                            protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                                Throwable e;
                                if (dataSource == null) {
                                    e = new IllegalStateException("onFailureImpl throw: dataSource is null");
                                } else {
                                    e = new IllegalStateException("onFailureImpl throw", dataSource.getFailureCause());
                                }
                                observer.onError(e);
                            }
                        }, new Executor() {
                            @Override
                            public void execute(Runnable command) {
                                Threads.postBackground(command);
                            }
                        });
            }
        };
    }

    private static String getString(@Nullable ImageFormat imageFormat) {
        if (imageFormat == null) {
            return "null";
        }

        return imageFormat.getName() + "." + imageFormat.getFileExtension();
    }

}

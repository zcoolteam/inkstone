package com.zcool.sample.module.imagedownload;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zcool.inkstone.ext.permission.RxPermission;
import com.zcool.inkstone.ext.permission.RxPermissionResult;
import com.zcool.inkstone.lang.DialogHelper;
import com.zcool.inkstone.lang.DisposableHolder;
import com.zcool.inkstone.manager.FrescoManager;
import com.zcool.inkstone.util.FileUtil;
import com.zcool.inkstone.util.IOUtil;
import com.zcool.inkstone.util.SystemUtil;
import com.zcool.inkstone.util.ToastUtil;
import com.zcool.sample.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ImageDownloadFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_image_download, container, false);
    }

    private Unbinder mUnbinder;

    @BindView(R.id.simple_drawee_view)
    SimpleDraweeView mSimpleDraweeView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnbinder = ButterKnife.bind(this, view);

        mSimpleDraweeView.setImageURI("https://reviveimg.hellorf.com/www/images/cec342833d985a36101fe5ac6c936284.jpg");
    }

    private final DisposableHolder mDefaultRequestHolder = new DisposableHolder();

    @OnClick(R.id.simple_drawee_view)
    void onImageClick() {
        startRequestCacheImage();
    }

    private void startRequestCacheImage() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("activity is null");
            return;
        }

        final String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        mDefaultRequestHolder.set(Single.wrap(RxPermission.from(activity, permissions, new Function<RxPermissionResult, SingleSource<RxPermissionResult>>() {
            @Override
            public SingleSource<RxPermissionResult> apply(final RxPermissionResult result) throws Exception {
                return new SingleSource<RxPermissionResult>() {
                    @Override
                    public void subscribe(final SingleObserver<? super RxPermissionResult> observer) {
                        FragmentActivity activity = getActivity();
                        if (activity == null) {
                            Timber.e("activity is null");
                            return;
                        }

                        DialogHelper.createPermissionConfirmDialog(
                                activity,
                                activity.findViewById(Window.ID_ANDROID_CONTENT),
                                "授权确认",
                                "下载图片到相册需要授予以下权限:访问存储卡，访问网络",
                                false,
                                null,
                                v -> observer.onSuccess(result.create())
                        ).show();
                    }
                };
            }
        })).flatMap(new Function<RxPermissionResult, SingleSource<File>>() {
            @Override
            public SingleSource<File> apply(RxPermissionResult result) throws Exception {
                if (result.isAllGranted()) {
                    final String url = "https://img.zcool.cn/community/010cdb5bd93295a8012092525f0e8b.png";
                    return FrescoManager.getInstance().fetchImage(ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                            .build());
                }

                throw new IllegalStateException("权限被拒绝，无法下载图片");
            }
        }).subscribeOn(Schedulers.io()).map(new Function<File, File>() {
            @Override
            public File apply(File file) throws Exception {
                String extension = FileUtil.getFileExtensionFromUrl(file.getAbsolutePath());
                if (TextUtils.isEmpty(extension)) {
                    throw new IllegalStateException("empty extension for file " + file.getAbsolutePath());
                }

                extension = extension.toLowerCase();
                List<String> supportExtension = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
                if (!supportExtension.contains(extension)) {
                    throw new IllegalStateException("extension not support " + extension + " " + file.getAbsolutePath());
                }

                File dir = FileUtil.getAppMediaDir();
                File copyFile = FileUtil.createNewTmpFileQuietly("zcool", extension, dir);
                if (copyFile == null) {
                    throw new IllegalStateException("create media file return null on dir " + dir);
                }

                File errorCopyFile = copyFile;
                try {
                    IOUtil.copy(file, copyFile, null, null);
                    errorCopyFile = null;
                } catch (Throwable e) {
                    throw new IllegalStateException("fail to copy file", e);
                } finally {
                    FileUtil.deleteFileQuietly(errorCopyFile);
                }

                return copyFile;
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).map(new Function<File, File>() {
            @Override
            public File apply(File file) throws Exception {
                if (!SystemUtil.addToMediaStore(file)) {
                    throw new IllegalStateException("fail to add file to media store " + file);
                }
                return file;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
            @Override
            public void accept(File file) throws Exception {
                ToastUtil.show("图片已添加到相册");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                e.printStackTrace();
                ToastUtil.show("图片保存失败：" + e.getLocalizedMessage());
            }
        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDefaultRequestHolder.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

}

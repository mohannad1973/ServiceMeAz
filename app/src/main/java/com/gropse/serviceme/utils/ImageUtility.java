package com.gropse.serviceme.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.gropse.serviceme.R;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.miguelbcr.ui.rx_paparazzo2.entities.Response;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.OriginalSize;
import com.yalantis.ucrop.UCrop;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ImageUtility {
    public static final int ONE_MEGABYTE_IN_BYTES = 1000000;
    public static final int CAMERA = 0;
    public static final int GALLERY = 1;

    public static class ImageBean {
        public Bitmap bitmap;
        public String imagePath;
    }

    public static Observable<ImageBean> getImageBean(final Activity mActivity, Observable<Response<Activity, FileData>> takePhotoAndCrop) {
        ImageBean imageBean = new ImageBean();
        return Observable.create((ObservableOnSubscribe<ImageBean>) emitter -> {
            try {

                takePhotoAndCrop.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(response -> {
                            if (checkResultCode(mActivity, response.resultCode())) {
                                imageBean.imagePath = response.data().getFile().getAbsolutePath();
                                return RxImageConverters.uriToBitmap(mActivity, Uri.fromFile(response.data().getFile()));
                            } else return null;
                        }).onErrorReturn(throwable -> null)
                        .subscribe(new Observer<Bitmap>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Bitmap bitmap) {
                                if (bitmap != null) {
                                    imageBean.bitmap = bitmap;
                                    emitter.onNext(imageBean);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                                emitter.onComplete();
                            }
                        });
            } catch (Exception e) {
                Log.e(RxImageConverters.class.getSimpleName(), "Error converting uri", e);
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Response<Activity, FileData>> pickSingle(final Activity mActivity, int source) {
        UCrop.Options options = new UCrop.Options();
        options.withAspectRatio(1,1);
        options.setToolbarColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark));
        options.setToolbarTitle("Crop");

        OriginalSize size = new OriginalSize();

        RxPaparazzo.SingleSelectionBuilder<Activity> resized = RxPaparazzo.single(mActivity)
                .setMaximumFileSizeInBytes(ONE_MEGABYTE_IN_BYTES)
                .size(size)
                .sendToMediaScanner();

        resized.crop(options);

        return source == 0 ? resized.usingCamera() : resized.usingGallery();
    }

    public static boolean checkResultCode(Context context, int code) {
        if (code == RxPaparazzo.RESULT_DENIED_PERMISSION) {
            showUserDidNotGrantPermissions(context);
        } else if (code == RxPaparazzo.RESULT_DENIED_PERMISSION_NEVER_ASK) {
            showUserDidNotGrantPermissionsNeverAsk(context);
        } else if (code != Activity.RESULT_OK) {
            showUserCanceled(context);
        }

        return code == Activity.RESULT_OK;
    }

    private static void showUserCanceled(Context context) {
//        AppUtility.showMessage(context, R.string.user_canceled);
    }

    private static void showUserDidNotGrantPermissions(Context context) {
//        AppUtility.showMessage(context, R.string.user_did_not_grant_permissions);
    }

    private static void showUserDidNotGrantPermissionsNeverAsk(Context context) {
//        AppUtility.showMessage(context, R.string.user_did_not_grant_permissions_never_ask);
    }
}

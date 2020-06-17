package com.yxytech.parkingcloud.baselibrary.http.common;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.yxytech.parkingcloud.baselibrary.dialog.DialogUtils;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import io.reactivex.CompletableTransformer;
import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import java.lang.ref.WeakReference;

/**
 * Created by zhpan on 2018/3/22.
 */

public class ProgressUtils {
    private static final String TAG = ProgressUtils.class.getName();

    public static <T> ObservableTransformer<T, T> applyProgressBar(
            @NonNull final Activity activity, String msg) {
        final WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        final DialogUtils dialogUtils = new DialogUtils();
        dialogUtils.showProgress(activityWeakReference.get(), msg);
        return upstream -> upstream.doOnSubscribe(disposable -> {
        }).doOnTerminate(() -> {//订阅被终止
            Activity context;
            if ((context = activityWeakReference.get()) != null
                    && !context.isFinishing()) {
                dialogUtils.dismissProgress();
            }
        }).doOnSubscribe((Consumer<Disposable>) disposable -> {
            /*Activity context;
                        if ((context = activityWeakReference.get()) != null
                                && !context.isFinishing()) {
                            dialogUtils.dismissProgress();
                        }*/
        });
    }

    public static <T> FlowableTransformer<T, T> applyProgressBarStomp(
            @NonNull final Activity activity, String msg) {
        final WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        DialogUtils dialogUtils = new DialogUtils();
        dialogUtils.showProgress(activityWeakReference.get(), msg);
        return upstream -> upstream.doOnSubscribe(subscription -> {
        }).doOnTerminate(() -> {//订阅被终止
            Activity context;
            if ((context = activityWeakReference.get()) != null
                    && !context.isFinishing()) {
                dialogUtils.dismissProgress();
            }
        }).doFinally(() -> {
//            Activity context = activityWeakReference.get();
//            if (null != context && !context.isFinishing()) {
//                dialogUtils.dismissProgress();
//            }
        });
    }

    public static <T> CompletableTransformer applyProgressBarStomp1(
            @NonNull final Activity activity, String msg) {
        final WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        final DialogUtils dialogUtils = new DialogUtils();
        dialogUtils.showProgress(activityWeakReference.get(), msg);
        return upstream -> upstream
                .doOnSubscribe(disposable -> {
                })
                .doOnTerminate(() -> {//订阅被终止
                    LogUtil.d(TAG, Thread.currentThread().getName() + ",doOnTerminate");
                    Activity context = activityWeakReference.get();
                    if (context != null
                            && !context.isFinishing()) {
                        dialogUtils.dismissProgress();
                    }
                }).doFinally(() -> {
                    LogUtil.d(TAG, Thread.currentThread().getName() + ",doFinally");
//            Activity context = activityWeakReference.get();
//            if (null != context && !context.isFinishing()) {
//                dialogUtils.dismissProgress();
//            }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtil.d(TAG, Thread.currentThread().getName() + ",doOnComplete");
                    }
                });
    }

    public static <T> ObservableTransformer<T, T> applyProgressBar(
            @NonNull final Activity activity) {
        return applyProgressBar(activity, "");
    }

    public static <T> FlowableTransformer<T, T> applyProgressBarStomp(@NonNull final Activity activity) {
        return applyProgressBarStomp(activity, "");
    }

    public static <T> CompletableTransformer applyProgressBarStomp1(@NonNull final Activity activity) {
        return applyProgressBarStomp1(activity, "");
    }


}
